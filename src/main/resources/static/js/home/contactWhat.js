(function () {
    "use strict";

    // ============================================================
    // CONFIGURATION
    // ============================================================

    const API_WHATSAPP = "/api/contact/whatsapp";
    const MAX_INPUT_LENGTH = 500;
    const MAX_MESSAGE_LENGTH = 2000;

    // ============================================================
    // SANITIZATION UTILITIES
    // ============================================================

    function escapeHtml(value) {
        if (!value) return '';
        const str = String(value);
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;')
            .replace(/\//g, '&#x2F;');
    }

    function sanitizeInput(value, maxLength = MAX_INPUT_LENGTH) {
        if (!value) return '';
        let clean = String(value)
            .replace(/[\x00-\x08\x0B\x0C\x0E-\x1F\x7F]/g, '')
            .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
            .replace(/javascript:/gi, '')
            .replace(/on\w+\s*=/gi, '')
            .substring(0, maxLength);
        return clean.trim();
    }

    function isValidEmail(email) {
        if (!email) return false;
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        return emailRegex.test(email);
    }

    function isValidPhone(phone) {
        if (!phone) return false;
        const phoneRegex = /^\+?[0-9\s\-()]{8,20}$/;
        return phoneRegex.test(phone);
    }

    function isValidUrl(url) {
        if (!url) return false;
        try {
            const parsed = new URL(url);
            if (parsed.protocol !== 'https:') return false;
            const allowedHosts = ['wa.me', 'api.whatsapp.com'];
            return allowedHosts.some(host => parsed.hostname.includes(host));
        } catch (error) {
            console.warn('⚠️ URL invalide:', url);
            return false;
        }
    }

    // ============================================================
    // INITIALISATION
    // ============================================================

    document.addEventListener("DOMContentLoaded", function () {
        initContactForm();
        initWhatsAppModal();
        initCSRFToken();
        console.log("📱 Contact WhatsApp — module chargé.");
    });

    // ============================================================
    // CSRF TOKEN
    // ============================================================

    function initCSRFToken() {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        if (csrfToken && csrfHeader) {
            window.__csrf = { token: csrfToken, header: csrfHeader };
            console.log('✅ Token CSRF chargé');
        } else {
            console.warn('⚠️ Token CSRF non trouvé');
        }
    }

    // ============================================================
    // INITIALISATION DU FORMULAIRE
    // ============================================================

    function initContactForm() {
        const form = document.getElementById("contactForm");
        if (!form) {
            console.warn("⚠️ Formulaire #contactForm introuvable.");
            return;
        }

        const whatsappButton = form.querySelector(".btn-submit.whatsapp");
        if (!whatsappButton) {
            console.warn("⚠️ Bouton WhatsApp introuvable.");
            return;
        }

        let isSubmitting = false;

        whatsappButton.addEventListener("click", function (e) {
            e.preventDefault();

            if (isSubmitting) {
                console.warn("⏳ Une requête est déjà en cours...");
                return;
            }

            isSubmitting = true;

            envoyerMessageWhatsApp(form, whatsappButton)
                .finally(() => {
                    isSubmitting = false;
                });
        });

        const inputs = form.querySelectorAll('input[required], textarea[required]');
        inputs.forEach(input => {
            input.addEventListener('blur', function () {
                validateField(this);
            });
        });
    }

    // ============================================================
    // VALIDATION DE CHAMP (blur)
    // ============================================================

    function validateField(input) {
        const value = input.value.trim();
        let isValid = true;
        let errorMessage = '';

        if (input.type === 'email') {
            if (!isValidEmail(value)) {
                isValid = false;
                errorMessage = 'Veuillez entrer un email valide.';
            }
        } else if (input.tagName === 'TEXTAREA') {
            if (value.length > MAX_MESSAGE_LENGTH) {
                isValid = false;
                errorMessage = `Le message ne doit pas dépasser ${MAX_MESSAGE_LENGTH} caractères.`;
            }
        }

        if (!isValid && value.length > 0) {
            input.setCustomValidity(errorMessage);
            input.style.borderColor = '#dc3545';
        } else {
            input.setCustomValidity('');
            input.style.borderColor = '';
        }

        return isValid;
    }

    // ============================================================
    // LECTURE DES CHAMPS PAR ID (fiable)
    // ============================================================

    function lireFormulaire() {
        return {
            nom:     sanitizeInput(document.getElementById("contactNom")?.value),
            prenom:  sanitizeInput(document.getElementById("contactPrenom")?.value),
            email:   sanitizeInput(document.getElementById("contactEmail")?.value, 100),
            objet:   sanitizeInput(document.getElementById("contactObjet")?.value, 100),
            filiere: sanitizeInput(document.getElementById("contactFiliere")?.value, 100),
            message: sanitizeInput(document.getElementById("contactMessage")?.value, MAX_MESSAGE_LENGTH)
        };
    }

    // ============================================================
    // ENVOYER MESSAGE WHATSAPP
    // ============================================================

    async function envoyerMessageWhatsApp(form, whatsappButton) {

        const data = lireFormulaire();

        // Vérification des champs obligatoires
        const champsManquants = [];
        if (!data.nom)     champsManquants.push("Nom");
        if (!data.prenom)  champsManquants.push("Prénom");
        if (!data.email)   champsManquants.push("Email");
        if (!data.objet)   champsManquants.push("Objet");
        if (!data.message) champsManquants.push("Message");

        if (champsManquants.length > 0) {
            afficherErreurContact(
                "Champs manquants : " + champsManquants.join(", "),
                "error"
            );
            return;
        }

        if (!isValidEmail(data.email)) {
            afficherErreurContact("Veuillez entrer une adresse email valide.", "error");
            return;
        }

        if (data.message.length > MAX_MESSAGE_LENGTH) {
            afficherErreurContact(
                `Le message ne doit pas dépasser ${MAX_MESSAGE_LENGTH} caractères.`,
                "error"
            );
            return;
        }

        // Préparation UI
        const originalHTML = whatsappButton.innerHTML;
        whatsappButton.disabled = true;
        whatsappButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Préparation...';

        try {
            const headers = {
                "Content-Type": "application/json",
                "Accept": "application/json"
            };

            if (window.__csrf) {
                headers[window.__csrf.header] = window.__csrf.token;
            }

            console.log('📤 Envoi vers:', API_WHATSAPP);

            const response = await fetch(API_WHATSAPP, {
                method: "POST",
                headers: headers,
                credentials: "same-origin",
                body: JSON.stringify(data)
            });

            const responseText = await response.text();

            if (!response.ok) {
                let errorMsg = `Erreur ${response.status}`;
                try {
                    const errorData = JSON.parse(responseText);
                    if (errorData.message) errorMsg = errorData.message;
                } catch {
                    if (response.status === 403) {
                        errorMsg = "Erreur de sécurité. Rafraîchissez la page.";
                    } else if (response.status === 401) {
                        errorMsg = "Veuillez vous reconnecter.";
                    }
                }
                throw new Error(errorMsg);
            }

            let contacts;
            try {
                contacts = JSON.parse(responseText);
            } catch (error) {
                console.error("JSON invalide:", responseText);
                throw new Error("Le serveur n'a pas renvoyé de données valides.");
            }

            if (!Array.isArray(contacts)) {
                throw new Error("Format de réponse inattendu.");
            }

            contacts = contacts.map(admin => ({
                ...admin,
                nom:         sanitizeInput(admin.nom),
                prenom:      sanitizeInput(admin.prenom),
                telephone:   sanitizeInput(admin.telephone),
                whatsappUrl: sanitizeInput(admin.whatsappUrl)
            })).slice(0, 2);

            console.log('👥 Contacts reçus:', contacts);

            if (contacts.length === 0) {
                afficherErreurContact(
                    "Aucun administrateur disponible pour le moment.",
                    "warning"
                );
                return;
            }

            if (contacts.length === 1) {
                const admin = contacts[0];

                if (!admin.whatsappUrl || !isValidUrl(admin.whatsappUrl)) {
                    afficherErreurContact(
                        "Le lien WhatsApp de " +
                        escapeHtml(admin.nom || "l'administrateur") +
                        " est indisponible.",
                        "error"
                    );
                    return;
                }

                ouvrirWhatsApp(admin.whatsappUrl);
                return;
            }

            afficherChoixAdministrateurs(contacts);

        } catch (error) {
            console.error("❌ Erreur:", error);
            afficherErreurContact(
                error.message || "Impossible de préparer le message WhatsApp.",
                "error"
            );
        } finally {
            whatsappButton.disabled = false;
            whatsappButton.innerHTML = originalHTML;
        }
    }

    // ============================================================
    // OUVRIR WHATSAPP
    // ============================================================

    function ouvrirWhatsApp(url) {
        if (!url || !isValidUrl(url)) {
            afficherErreurContact("Lien WhatsApp invalide.", "error");
            return;
        }

        const whatsappWindow = window.open(url, "_blank");

        if (!whatsappWindow) {
            afficherErreurContact(
                "Votre navigateur a bloqué l'ouverture de WhatsApp. " +
                "Autorisez les fenêtres pop-up puis réessayez.",
                "info"
            );
        }
    }

    // ============================================================
    // MODAL WHATSAPP
    // ============================================================

    function initWhatsAppModal() {
        const modal = document.getElementById("whatsappModal");
        if (!modal) return;

        const closeButton = document.getElementById("whatsappModalClose");
        const cancelButton = document.getElementById("whatsappCancel");
        const overlay = document.getElementById("whatsappModalOverlay");

        if (closeButton) closeButton.addEventListener("click", fermerModalWhatsApp);
        if (cancelButton) cancelButton.addEventListener("click", fermerModalWhatsApp);

        if (overlay) {
            overlay.addEventListener("click", function (e) {
                if (e.target === overlay) fermerModalWhatsApp();
            });
        }

        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape" && modal.classList.contains("show")) {
                fermerModalWhatsApp();
            }
        });
    }

    // ============================================================
    // AFFICHER LES ADMINISTRATEURS
    // ============================================================

    function afficherChoixAdministrateurs(contacts) {
        const modal = document.getElementById("whatsappModal");
        const adminsContainer = document.getElementById("whatsappAdmins");

        if (!modal || !adminsContainer) {
            console.error("❌ Modal WhatsApp introuvable.");
            if (contacts.length > 0 && contacts[0].whatsappUrl) {
                ouvrirWhatsApp(contacts[0].whatsappUrl);
            }
            return;
        }

        adminsContainer.innerHTML = "";

        contacts.forEach(function (admin) {
            const nomComplet = ((admin.prenom || "") + " " + (admin.nom || "")).trim();
            const nomAffiche = nomComplet || "Administrateur";
            const telephone = admin.telephone || "Numéro indisponible";
            const initiale = nomAffiche.charAt(0).toUpperCase();

            const adminElement = document.createElement("div");
            adminElement.className = "whatsapp-admin";

            const avatar = document.createElement("div");
            avatar.className = "whatsapp-admin-avatar";
            avatar.textContent = escapeHtml(initiale);

            const info = document.createElement("div");
            info.className = "whatsapp-admin-info";

            const nameSpan = document.createElement("span");
            nameSpan.className = "whatsapp-admin-name";
            nameSpan.textContent = escapeHtml(nomAffiche);

            const phoneSpan = document.createElement("span");
            phoneSpan.className = "whatsapp-admin-phone";
            phoneSpan.textContent = escapeHtml(telephone);

            info.appendChild(nameSpan);
            info.appendChild(phoneSpan);

            const button = document.createElement("button");
            button.type = "button";
            button.className = "whatsapp-admin-button";
            button.innerHTML = '<i class="fab fa-whatsapp"></i> <span>Contacter</span>';

            button.addEventListener("click", function () {
                if (!admin.whatsappUrl || !isValidUrl(admin.whatsappUrl)) {
                    afficherErreurContact(
                        "Le lien WhatsApp de " + escapeHtml(nomAffiche) + " est indisponible.",
                        "error"
                    );
                    return;
                }

                fermerModalWhatsApp();
                ouvrirWhatsApp(admin.whatsappUrl);
            });

            adminElement.appendChild(avatar);
            adminElement.appendChild(info);
            adminElement.appendChild(button);
            adminsContainer.appendChild(adminElement);
        });

        modal.classList.add("show");
        modal.setAttribute("aria-hidden", "false");
        document.body.style.overflow = "hidden";
    }

    // ============================================================
    // FERMER MODAL
    // ============================================================

    function fermerModalWhatsApp() {
        const modal = document.getElementById("whatsappModal");
        if (!modal) return;

        modal.classList.remove("show");
        modal.setAttribute("aria-hidden", "true");
        document.body.style.overflow = "";
    }

    // ============================================================
    // MESSAGE D'ERREUR (TOAST)
    // ============================================================

    function afficherErreurContact(message, type = "error") {
        const safeMessage = escapeHtml(message);

        if (typeof window.showToast === "function") {
            window.showToast(message, type);
            return;
        }

        const notification = document.createElement('div');
        notification.className = `toast-notification toast-${type}`;
        notification.innerHTML = `
            <div class="toast-content">
                <i class="fas ${type === 'error' ? 'fa-exclamation-circle' : type === 'warning' ? 'fa-warning' : 'fa-info-circle'}"></i>
                <span>${safeMessage}</span>
            </div>
            <button class="toast-close" onclick="this.parentElement.remove()">×</button>
        `;

        if (!document.getElementById('toast-styles')) {
            const style = document.createElement('style');
            style.id = 'toast-styles';
            style.textContent = `
                .toast-notification {
                    position: fixed;
                    bottom: 20px;
                    right: 20px;
                    max-width: 400px;
                    padding: 16px 20px;
                    background: #fff;
                    border-radius: 12px;
                    box-shadow: 0 10px 40px rgba(0,0,0,0.15);
                    z-index: 99999;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    animation: slideIn 0.3s ease;
                    border-left: 4px solid;
                }
                .toast-notification.toast-error { border-left-color: #dc3545; }
                .toast-notification.toast-warning { border-left-color: #f59e0b; }
                .toast-notification.toast-info { border-left-color: #0d6efd; }
                .toast-notification .toast-content {
                    flex: 1;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    color: #1a1a1a;
                    font-size: 14px;
                }
                .toast-notification .toast-content i { font-size: 20px; }
                .toast-notification.toast-error .toast-content i { color: #dc3545; }
                .toast-notification.toast-warning .toast-content i { color: #f59e0b; }
                .toast-notification.toast-info .toast-content i { color: #0d6efd; }
                .toast-notification .toast-close {
                    background: none;
                    border: none;
                    font-size: 20px;
                    cursor: pointer;
                    color: #999;
                    padding: 0 4px;
                }
                .toast-notification .toast-close:hover { color: #333; }
                @keyframes slideIn {
                    from { opacity: 0; transform: translateX(100px); }
                    to { opacity: 1; transform: translateX(0); }
                }
            `;
            document.head.appendChild(style);
        }

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentElement) {
                notification.style.opacity = '0';
                notification.style.transform = 'translateX(100px)';
                notification.style.transition = 'all 0.3s ease';
                setTimeout(() => {
                    if (notification.parentElement) notification.remove();
                }, 300);
            }
        }, 6000);
    }

    // ============================================================
    // EXPOSER LES FONCTIONS
    // ============================================================

    window.__contactUtils = {
        escapeHtml,
        sanitizeInput,
        isValidEmail,
        isValidPhone,
        isValidUrl
    };

})();