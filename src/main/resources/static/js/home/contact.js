/*
(function () {

    "use strict";


    // ============================================================
    // CONFIGURATION
    // ============================================================

    const API_WHATSAPP = "/api/contact/whatsapp";


    // ============================================================
    // INITIALISATION
    // ============================================================

    document.addEventListener("DOMContentLoaded", function () {

        initContactForm();
        initWhatsAppModal();

        console.log("📱 Contact WhatsApp — module chargé.");

    });


    // ============================================================
    // INITIALISATION DU FORMULAIRE
    // ============================================================

    function initContactForm() {

        const form = document.getElementById("contactForm");

        if (!form) {
            console.warn(
                "⚠️ Formulaire #contactForm introuvable."
            );
            return;
        }


        const whatsappButton =
            form.querySelector(".btn-submit.whatsapp");


        if (!whatsappButton) {

            console.warn(
                "⚠️ Bouton WhatsApp introuvable."
            );

            return;
        }


        whatsappButton.addEventListener(
            "click",
            function () {

                envoyerMessageWhatsApp(
                    form,
                    whatsappButton
                );

            }
        );

    }


    // ============================================================
    // ENVOYER MESSAGE WHATSAPP
    // ============================================================

    async function envoyerMessageWhatsApp(
        form,
        whatsappButton
    ) {

        // --------------------------------------------------------
        // Vérification du formulaire
        // --------------------------------------------------------

        if (!form.checkValidity()) {

            form.reportValidity();

            return;
        }


        // --------------------------------------------------------
        // Récupération des champs
        // --------------------------------------------------------

        const nom =
            form.querySelector(
                'input[placeholder="Votre nom"]'
            )?.value.trim() || "";


        const prenom =
            form.querySelector(
                'input[placeholder="Votre prénom"]'
            )?.value.trim() || "";


        const email =
            form.querySelector(
                'input[type="email"]'
            )?.value.trim() || "";


        const objet =
            form.querySelector(
                'input[placeholder="Inscription, Renseignement..."]'
            )?.value.trim() || "";


        const filiere =
            form.querySelector(
                "select"
            )?.value.trim() || "";


        const message =
            form.querySelector(
                "textarea"
            )?.value.trim() || "";


        // --------------------------------------------------------
        // Vérification supplémentaire
        // --------------------------------------------------------

        if (
            !nom ||
            !prenom ||
            !email ||
            !objet ||
            !message
        ) {

            afficherErreurContact(
                "Veuillez remplir tous les champs obligatoires."
            );

            return;
        }


        // --------------------------------------------------------
        // Objet envoyé au backend
        // --------------------------------------------------------

        const data = {

            nom: nom,

            prenom: prenom,

            email: email,

            objet: objet,

            filiere: filiere,

            message: message

        };


        // --------------------------------------------------------
        // Sauvegarde état bouton
        // --------------------------------------------------------

        const originalHTML =
            whatsappButton.innerHTML;


        whatsappButton.disabled = true;


        whatsappButton.innerHTML =
            '<i class="fas fa-spinner fa-spin"></i> Préparation...';


        try {

            // ====================================================
            // APPEL BACKEND
            // ====================================================

            const response = await fetch(
                API_WHATSAPP,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },

                    body: JSON.stringify(data)
                }
            );


            console.log(
                "STATUS :",
                response.status
            );


            console.log(
                "URL FINALE :",
                response.url
            );


            // ----------------------------------------------------
            // Lire d'abord la réponse brute
            // ----------------------------------------------------

            const responseText =
                await response.text();


            console.log(
                "RÉPONSE BRUTE DU SERVEUR :",
                responseText
            );


            // ----------------------------------------------------
            // Vérification HTTP
            // ----------------------------------------------------

            if (!response.ok) {

                throw new Error(
                    "Erreur HTTP " +
                    response.status
                );

            }


            // ----------------------------------------------------
            // Vérification que la réponse est du JSON
            // ----------------------------------------------------

            let contacts;


            try {

                contacts =
                    JSON.parse(responseText);

            } catch (error) {

                console.error(
                    "JSON reçu invalide :",
                    responseText
                );


                throw new Error(
                    "Le serveur n'a pas renvoyé du JSON."
                );

            }


            // ====================================================
            // VÉRIFICATION DE LA LISTE
            // ====================================================

            if (!Array.isArray(contacts)) {

                throw new Error(
                    "Format de réponse inattendu."
                );

            }


            // ----------------------------------------------------
            // Maximum 2 administrateurs
            // ----------------------------------------------------

            contacts =
                contacts.slice(0, 2);


            // ====================================================
            // AUCUN ADMINISTRATEUR
            // ====================================================

            if (contacts.length === 0) {

                afficherErreurContact(
                    "Aucun administrateur disponible pour le moment. " +
                    "Veuillez réessayer plus tard."
                );

                return;
            }


            // ====================================================
            // UN SEUL ADMINISTRATEUR
            // ====================================================

            if (contacts.length === 1) {

                const admin =
                    contacts[0];


                if (!admin.whatsappUrl) {

                    afficherErreurContact(
                        "Le numéro WhatsApp de l'administrateur " +
                        "n'est pas disponible."
                    );

                    return;
                }


                console.log(
                    "📱 Ouverture WhatsApp :",
                    admin.nom,
                    admin.prenom
                );


                ouvrirWhatsApp(
                    admin.whatsappUrl
                );


                return;
            }


            // ====================================================
            // DEUX ADMINISTRATEURS
            // ====================================================

            console.log(
                "👥 Administrateurs disponibles :",
                contacts
            );


            afficherChoixAdministrateurs(
                contacts
            );


        } catch (error) {

            console.error(
                "Erreur lors de la préparation WhatsApp :",
                error
            );


            afficherErreurContact(
                "Impossible de préparer le message WhatsApp. " +
                "Veuillez réessayer."
            );


        } finally {

            // ----------------------------------------------------
            // Restaurer bouton
            // ----------------------------------------------------

            whatsappButton.disabled = false;

            whatsappButton.innerHTML =
                originalHTML;

        }

    }


    // ============================================================
    // OUVRIR WHATSAPP
    // ============================================================

    function ouvrirWhatsApp(url) {

        if (!url) {

            afficherErreurContact(
                "Lien WhatsApp indisponible."
            );

            return;
        }


        /!*
         * Ouverture dans un nouvel onglet.
         *
         * Le navigateur peut bloquer cette ouverture
         * si elle n'est plus considérée comme directement
         * déclenchée par l'action utilisateur.
         *!/

        const whatsappWindow =
            window.open(
                url,
                "_blank"
            );


        if (!whatsappWindow) {

            afficherErreurContact(
                "Votre navigateur a bloqué l'ouverture de WhatsApp. " +
                "Autorisez les fenêtres pop-up puis réessayez."
            );

        }

    }


    // ============================================================
    // MODAL WHATSAPP
    // ============================================================

    function initWhatsAppModal() {

        const closeButton =
            document.getElementById(
                "whatsappModalClose"
            );


        const cancelButton =
            document.getElementById(
                "whatsappCancel"
            );


        const overlay =
            document.getElementById(
                "whatsappModalOverlay"
            );


        if (closeButton) {

            closeButton.addEventListener(
                "click",
                fermerModalWhatsApp
            );

        }


        if (cancelButton) {

            cancelButton.addEventListener(
                "click",
                fermerModalWhatsApp
            );

        }


        if (overlay) {

            overlay.addEventListener(
                "click",
                fermerModalWhatsApp
            );

        }


        // --------------------------------------------------------
        // Touche ESC
        // --------------------------------------------------------

        document.addEventListener(
            "keydown",
            function (event) {

                if (event.key === "Escape") {

                    fermerModalWhatsApp();

                }

            }
        );

    }


    // ============================================================
    // AFFICHER LES ADMINISTRATEURS
    // ============================================================

    function afficherChoixAdministrateurs(
        contacts
    ) {

        const modal =
            document.getElementById(
                "whatsappModal"
            );


        const adminsContainer =
            document.getElementById(
                "whatsappAdmins"
            );


        if (!modal || !adminsContainer) {

            console.error(
                "❌ Modal WhatsApp introuvable."
            );


            // Fallback :
            // si la modal n'existe pas, on ouvre
            // le premier administrateur.

            if (
                contacts.length > 0 &&
                contacts[0].whatsappUrl
            ) {

                ouvrirWhatsApp(
                    contacts[0].whatsappUrl
                );

            }

            return;
        }


        // --------------------------------------------------------
        // Nettoyage
        // --------------------------------------------------------

        adminsContainer.innerHTML = "";


        // --------------------------------------------------------
        // Création des cartes
        // --------------------------------------------------------

        contacts.forEach(
            function (admin) {

                const nomComplet =
                    (
                        (admin.prenom || "") +
                        " " +
                        (admin.nom || "")
                    ).trim();


                const nomAffiche =
                    nomComplet ||
                    "Administrateur";


                const telephone =
                    admin.telephone ||
                    "Numéro indisponible";


                const initiale =
                    nomAffiche
                        .charAt(0)
                        .toUpperCase();


                // ------------------------------------------------
                // Carte
                // ------------------------------------------------

                const adminElement =
                    document.createElement("div");


                adminElement.className =
                    "whatsapp-admin";


                adminElement.innerHTML = `

                    <div class="whatsapp-admin-avatar">
                        ${escapeHtml(initiale)}
                    </div>

                    <div class="whatsapp-admin-info">

                        <span class="whatsapp-admin-name">
                            ${escapeHtml(nomAffiche)}
                        </span>

                        <span class="whatsapp-admin-phone">
                            ${escapeHtml(telephone)}
                        </span>

                    </div>

                    <button
                        type="button"
                        class="whatsapp-admin-button"
                    >
                        <i class="fab fa-whatsapp"></i>
                        <span>Contacter</span>
                    </button>

                `;


                // ------------------------------------------------
                // Bouton contacter
                // ------------------------------------------------

                const button =
                    adminElement.querySelector(
                        ".whatsapp-admin-button"
                    );


                if (button) {

                    button.addEventListener(
                        "click",
                        function () {

                            if (
                                !admin.whatsappUrl
                            ) {

                                afficherErreurContact(
                                    "Le numéro WhatsApp de " +
                                    nomAffiche +
                                    " n'est pas disponible."
                                );

                                return;
                            }


                            console.log(
                                "📱 Contact sélectionné :",
                                nomAffiche
                            );


                            fermerModalWhatsApp();


                            ouvrirWhatsApp(
                                admin.whatsappUrl
                            );

                        }
                    );

                }


                adminsContainer.appendChild(
                    adminElement
                );

            }
        );


        // --------------------------------------------------------
        // Afficher modal
        // --------------------------------------------------------

        modal.classList.add(
            "show"
        );


        modal.setAttribute(
            "aria-hidden",
            "false"
        );


        // Empêcher le scroll derrière la modal

        document.body.style.overflow =
            "hidden";

    }


    // ============================================================
    // FERMER MODAL
    // ============================================================

    function fermerModalWhatsApp() {

        const modal =
            document.getElementById(
                "whatsappModal"
            );


        if (!modal) {
            return;
        }


        modal.classList.remove(
            "show"
        );


        modal.setAttribute(
            "aria-hidden",
            "true"
        );


        // Réactiver le scroll

        document.body.style.overflow =
            "";

    }


    // ============================================================
    // MESSAGE D'ERREUR
    // ============================================================

    function afficherErreurContact(
        message
    ) {

        console.error(
            "❌ Contact WhatsApp :",
            message
        );


        /!*
         * Si ton projet possède déjà une fonction
         * showToast(), on l'utilise.
         *!/

        if (
            typeof window.showToast ===
            "function"
        ) {

            window.showToast(
                message
            );

            return;
        }


        /!*
         * Sinon fallback simple.
         *!/

        alert(message);

    }


    // ============================================================
    // PROTECTION CONTRE HTML INJECTÉ
    // ============================================================

    function escapeHtml(value) {

        return String(value)

            .replace(
                /&/g,
                "&amp;"
            )

            .replace(
                /</g,
                "&lt;"
            )

            .replace(
                />/g,
                "&gt;"
            )

            .replace(
                /"/g,
                "&quot;"
            )

            .replace(
                /'/g,
                "&#039;"
            );

    }


})();*/
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

    /**
     * ✅ CORRECTION : Valide une URL WhatsApp
     */
    function isValidUrl(url) {
        if (!url) return false;
        try {
            const parsed = new URL(url);
            // N'autoriser que HTTPS
            if (parsed.protocol !== 'https:') return false;

            // N'autoriser que les domaines WhatsApp
            const allowedHosts = ['wa.me', 'api.whatsapp.com'];
            const isAllowed = allowedHosts.some(host => parsed.hostname.includes(host));

            if (!isAllowed) {
                console.warn('⚠️ Domaine non autorisé:', parsed.hostname);
                return false;
            }

            return true;
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
            window.__csrf = {
                token: csrfToken,
                header: csrfHeader
            };
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
            input.addEventListener('blur', function() {
                validateField(this);
            });
        });
    }

    // ============================================================
    // VALIDATION DE CHAMP
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
        } else if (input.placeholder && input.placeholder.includes('Téléphone')) {
            if (!isValidPhone(value)) {
                isValid = false;
                errorMessage = 'Veuillez entrer un numéro de téléphone valide.';
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
    // ENVOYER MESSAGE WHATSAPP
    // ============================================================

    async function envoyerMessageWhatsApp(form, whatsappButton) {
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const nom = sanitizeInput(form.querySelector('input[placeholder="Votre nom"]')?.value);
        const prenom = sanitizeInput(form.querySelector('input[placeholder="Votre prénom"]')?.value);
        const email = sanitizeInput(form.querySelector('input[type="email"]')?.value, 100);
        const objet = sanitizeInput(form.querySelector('input[placeholder="Inscription, Renseignement..."]')?.value, 100);
        const filiere = sanitizeInput(form.querySelector("select")?.value, 100);
        const message = sanitizeInput(form.querySelector("textarea")?.value, MAX_MESSAGE_LENGTH);

        if (!nom || !prenom || !email || !objet || !message) {
            afficherErreurContact("Veuillez remplir tous les champs obligatoires.", "error");
            return;
        }

        if (!isValidEmail(email)) {
            afficherErreurContact("Veuillez entrer une adresse email valide.", "error");
            return;
        }

        if (message.length > MAX_MESSAGE_LENGTH) {
            afficherErreurContact(`Le message ne doit pas dépasser ${MAX_MESSAGE_LENGTH} caractères.`, "error");
            return;
        }

        const data = {
            nom: nom,
            prenom: prenom,
            email: email,
            objet: objet,
            filiere: filiere,
            message: message
        };

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
                body: JSON.stringify(data)
            });

            const responseText = await response.text();

            if (!response.ok) {
                let errorMsg = `Erreur ${response.status}`;
                try {
                    const errorData = JSON.parse(responseText);
                    if (errorData.message) {
                        errorMsg = errorData.message;
                    }
                } catch {
                    if (response.status === 403 && responseText.includes('CSRF')) {
                        errorMsg = 'Erreur de sécurité. Veuillez rafraîchir la page.';
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

            // Sanitization des contacts
            contacts = contacts.map(admin => ({
                ...admin,
                nom: sanitizeInput(admin.nom),
                prenom: sanitizeInput(admin.prenom),
                telephone: sanitizeInput(admin.telephone),
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

                // ✅ Vérification de l'URL avant ouverture
                if (!admin.whatsappUrl) {
                    afficherErreurContact(
                        "Le lien WhatsApp de " + escapeHtml(admin.nom || 'l\'administrateur') + " n'est pas disponible.",
                        "error"
                    );
                    return;
                }

                console.log('🔗 URL WhatsApp:', admin.whatsappUrl);

                // ✅ Validation de l'URL
                if (!isValidUrl(admin.whatsappUrl)) {
                    afficherErreurContact(
                        "Le lien WhatsApp de " + escapeHtml(admin.nom || 'l\'administrateur') + " est invalide.",
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
        console.log('📱 Ouverture de WhatsApp:', url);

        // ✅ Vérification supplémentaire
        if (!url) {
            afficherErreurContact("Lien WhatsApp indisponible.", "error");
            return;
        }

        if (!isValidUrl(url)) {
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

        function closeModal() {
            modal.classList.remove("show");
            modal.setAttribute("aria-hidden", "true");
            document.body.style.overflow = "";
        }

        if (closeButton) closeButton.addEventListener("click", closeModal);
        if (cancelButton) cancelButton.addEventListener("click", closeModal);

        if (overlay) {
            overlay.addEventListener("click", function(e) {
                if (e.target === overlay) closeModal();
            });
        }

        document.addEventListener("keydown", function(event) {
            if (event.key === "Escape" && modal.classList.contains("show")) {
                closeModal();
            }
        });

        const modalContent = modal.querySelector('.whatsapp-modal-content');
        if (modalContent) {
            modalContent.addEventListener('click', function(e) {
                e.stopPropagation();
            });
        }
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
                if (!admin.whatsappUrl) {
                    afficherErreurContact(
                        "Le numéro WhatsApp de " + escapeHtml(nomAffiche) + " n'est pas disponible.",
                        "error"
                    );
                    return;
                }

                if (!isValidUrl(admin.whatsappUrl)) {
                    afficherErreurContact(
                        "Le lien WhatsApp de " + escapeHtml(nomAffiche) + " est invalide.",
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
            window.showToast(safeMessage, type);
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
