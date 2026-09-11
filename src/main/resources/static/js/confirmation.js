/**
 * ============================================================
 * FICHIER : confirmation.js
 * VERSION : Avec compte à rebours 3 jours
 * ============================================================
 */

(function() {
    'use strict';

    // ============================================================
    // 1. COMPTE À REBOURS 3 JOURS (72h)
    // ============================================================

    const DAYS_LIMIT = 3; // 3 jours
    let timeRemaining = DAYS_LIMIT * 24 * 60 * 60; // en secondes
    let timerInterval = null;
    let isExpired = false;

    // Éléments DOM
    const daysEl = document.getElementById('days');
    const hoursEl = document.getElementById('hours');
    const minutesEl = document.getElementById('minutes');
    const secondsEl = document.getElementById('seconds');
    const alertContainer = document.getElementById('alertInactivite');
    const alertCloseBtn = document.getElementById('alertClose');

    /**
     * Formate un nombre en string avec 2 chiffres
     */
    function pad(num) {
        return String(num).padStart(2, '0');
    }

    /**
     * Met à jour l'affichage du compte à rebours
     */
    function updateCountdown() {
        if (timeRemaining <= 0) {
            clearInterval(timerInterval);
            isExpired = true;
            if (daysEl) daysEl.textContent = '00';
            if (hoursEl) hoursEl.textContent = '00';
            if (minutesEl) minutesEl.textContent = '00';
            if (secondsEl) secondsEl.textContent = '00';

            // Changer l'alerte en mode "expiré"
            if (alertContainer) {
                alertContainer.classList.remove('urgent');
                alertContainer.style.background = 'linear-gradient(135deg, #fee2e2 0%, #fca5a5 100%)';
                alertContainer.style.borderColor = '#dc2626';
                const title = alertContainer.querySelector('.alert-title');
                if (title) {
                    title.innerHTML = '<i class="fas fa-exclamation-circle"></i> Compte expiré !';
                    title.style.color = '#991b1b';
                }
                const desc = alertContainer.querySelector('.alert-content p');
                if (desc) {
                    desc.innerHTML = 'Votre compte a été <strong>automatiquement supprimé</strong> après 3 jours d\'inactivité. Veuillez créer un nouveau compte.';
                }
                const countdownContainer = document.getElementById('countdownContainer');
                if (countdownContainer) {
                    countdownContainer.innerHTML = '<span style="color: #991b1b; font-weight: 700;"><i class="fas fa-times-circle"></i> Délai expiré</span>';
                }
            }
            return;
        }

        // Calcul des jours, heures, minutes, secondes
        const days = Math.floor(timeRemaining / (24 * 60 * 60));
        const hours = Math.floor((timeRemaining % (24 * 60 * 60)) / (60 * 60));
        const minutes = Math.floor((timeRemaining % (60 * 60)) / 60);
        const seconds = Math.floor(timeRemaining % 60);

        // Mise à jour des éléments
        if (daysEl) daysEl.textContent = pad(days);
        if (hoursEl) hoursEl.textContent = pad(hours);
        if (minutesEl) minutesEl.textContent = pad(minutes);
        if (secondsEl) secondsEl.textContent = pad(seconds);

        // Si moins de 24h restantes, passer en mode "urgent"
        if (timeRemaining < 24 * 60 * 60 && alertContainer) {
            alertContainer.classList.add('urgent');
        }

        // Décrémenter
        timeRemaining--;
    }

    /**
     * Démarre le compte à rebours
     */
    function startCountdown() {
        // Initialiser l'affichage
        updateCountdown();

        // Démarrer l'intervalle (toutes les secondes)
        if (timerInterval) {
            clearInterval(timerInterval);
        }
        timerInterval = setInterval(updateCountdown, 1000);
    }

    // ============================================================
    // 2. FERMETURE DE L'ALERTE
    // ============================================================

    if (alertCloseBtn) {
        alertCloseBtn.addEventListener('click', function() {
            if (alertContainer) {
                alertContainer.style.transition = 'all 0.4s ease';
                alertContainer.style.opacity = '0';
                alertContainer.style.transform = 'translateY(-10px) scale(0.95)';
                setTimeout(function() {
                    alertContainer.style.display = 'none';
                }, 400);
            }
        });
    }

    // ============================================================
    // 3. ANIMATION D'ENTRÉE DES ÉTAPES
    // ============================================================

    const steps = document.querySelectorAll('.step-item');

    function animateSteps() {
        steps.forEach(function(step, index) {
            step.style.opacity = '0';
            step.style.transform = 'translateY(15px)';
            step.style.transition = 'opacity 0.5s ease, transform 0.5s ease';

            setTimeout(function() {
                step.style.opacity = '1';
                step.style.transform = 'translateY(0)';
            }, 100 + index * 150);
        });
    }

    // ============================================================
    // 4. ANIMATION DE L'ICÔNE DE SUCCÈS
    // ============================================================

    const ring = document.querySelector('.success-ring');
    if (ring) {
        ring.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.05)';
            this.style.transition = 'transform 0.3s ease';
        });
        ring.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    }

    // ============================================================
    // 5. INITIALISATION
    // ============================================================

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            animateSteps();
            startCountdown();
        });
    } else {
        animateSteps();
        startCountdown();
    }

    // Sauvegarde du temps restant dans la console
    console.log('⏳ Compte à rebours 3 jours démarré !');
    console.log('📅 Votre compte expirera dans 72h si vous ne finalisez pas votre candidature.');

})();