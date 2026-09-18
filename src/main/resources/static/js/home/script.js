/**
 * ============================================================
 * MODULE DATES D'INSCRIPTION — 4 états
 * IFP-PO Les Perles d'Or
 * ============================================================
 */
(function () {
    'use strict';

    // ============================================================
    // 1. UTILITAIRES
    // ============================================================
    function getSecondMondayOfSeptember(year) {
        const firstSeptember = new Date(year, 8, 1);
        const dayOfWeek = firstSeptember.getDay();
        const firstMondayOffset = (dayOfWeek === 1) ? 0 : (8 - dayOfWeek) % 7;
        const firstMonday = new Date(year, 8, 1 + firstMondayOffset);
        const secondMonday = new Date(firstMonday);
        secondMonday.setDate(firstMonday.getDate() + 7);
        secondMonday.setHours(0, 0, 0, 0);
        return secondMonday;
    }

    function getOpeningDate(year) {
        return new Date(year, 7, 1, 0, 0, 0);       // 1er août
    }

    function getClosingDate(year) {
        return new Date(year, 9, 31, 23, 59, 59);   // 31 octobre
    }

    function formatDateShort(date) {
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        });
    }

    function formatDateLong(date) {
        return date.toLocaleDateString('fr-FR', {
            day: 'numeric',
            month: 'long',
            year: 'numeric'
        });
    }

    function getDaysRemaining(targetDate) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const target = new Date(targetDate);
        target.setHours(0, 0, 0, 0);
        return Math.ceil((target.getTime() - today.getTime()) / 86400000);
    }

    function isSameDay(d1, d2) {
        return d1.getFullYear() === d2.getFullYear()
            && d1.getMonth() === d2.getMonth()
            && d1.getDate() === d2.getDate();
    }

    // ============================================================
    // 2. DÉTECTION DE L'ÉTAT
    // ============================================================
    function getRegistrationSession() {
        const now = new Date();
        now.setHours(0, 0, 0, 0);

        const currentYear = now.getFullYear();
        const openingThisYear = getOpeningDate(currentYear);
        const closingThisYear = getClosingDate(currentYear);
        const rentreeThisYear = getSecondMondayOfSeptember(currentYear);

        // ÉTAT 1 : RENTREE (jour J)
        if (isSameDay(now, rentreeThisYear)) {
            return {
                state: 'rentree',
                sessionYear: currentYear,
                openingDate: openingThisYear,
                closingDate: closingThisYear,
                rentreeDate: rentreeThisYear,
                referenceDate: closingThisYear,
                daysRemaining: getDaysRemaining(closingThisYear)
            };
        }

        // ÉTAT 2 : OPEN (avant la rentrée)
        if (now >= openingThisYear && now < rentreeThisYear) {
            return {
                state: 'open',
                sessionYear: currentYear,
                openingDate: openingThisYear,
                closingDate: closingThisYear,
                rentreeDate: rentreeThisYear,
                referenceDate: closingThisYear,
                daysRemaining: getDaysRemaining(closingThisYear)
            };
        }

        // ÉTAT 3 : OPEN_AFTER (après la rentrée)
        if (now > rentreeThisYear && now <= closingThisYear) {
            return {
                state: 'open_after',
                sessionYear: currentYear,
                openingDate: openingThisYear,
                closingDate: closingThisYear,
                rentreeDate: rentreeThisYear,
                referenceDate: closingThisYear,
                daysRemaining: getDaysRemaining(closingThisYear)
            };
        }

        // ÉTAT 4 : CLOSED
        let nextYear = (now > closingThisYear) ? currentYear + 1 : currentYear;
        const openingNext = getOpeningDate(nextYear);
        const closingNext = getClosingDate(nextYear);
        const rentreeNext = getSecondMondayOfSeptember(nextYear);

        return {
            state: 'closed',
            sessionYear: nextYear,
            openingDate: openingNext,
            closingDate: closingNext,
            rentreeDate: rentreeNext,
            referenceDate: openingNext,
            daysRemaining: getDaysRemaining(openingNext)
        };
    }

    // ============================================================
    // 3. AFFICHAGE
    // ============================================================
    function updateRegistrationDisplay() {
        const session = getRegistrationSession();

        // ---- Récupération DOM ----
        const badgeDiplome = document.querySelector('.badge-diplome');
        const badgeLabelEl = document.getElementById('badgeLabel');       // 🎯
        const prefixEl     = document.getElementById('datePrefix');       // 🎯
        const dateEl       = document.getElementById('date-inscription');
        const badgeEl      = document.getElementById('dateBadgeImportant');
        const joursEl      = document.getElementById('jours-restants-num');
        const wrapperEl    = document.querySelector('.jours-restants');
        const nextDateEl   = document.getElementById('Next-date-inscription');

        // ---- 1. Texte principal (#badgeLabel) ----
        if (badgeLabelEl) {
            if (session.state === 'open') {
                badgeLabelEl.textContent = 'Inscriptions ouvertes jusqu\'au';
            } else if (session.state === 'rentree') {
                badgeLabelEl.textContent = '🎉 C\'est la rentrée ! Encore ouvert jusqu\'au';
            } else if (session.state === 'open_after') {
                badgeLabelEl.textContent = 'Inscriptions encore ouvertes jusqu\'au';
            } else {
                badgeLabelEl.textContent = 'Inscriptions fermées — Reprise le';
            }
        }

        // ---- 2. Préfixe (#datePrefix) ----
        if (prefixEl) {
            if (session.state === 'closed') {
                prefixEl.textContent = 'Ouverture le';
            } else {
                prefixEl.textContent = 'Clôture le';
            }
        }

        // ---- 3. Date ----
        if (dateEl) {
            if (session.state === 'closed') {
                dateEl.textContent = formatDateShort(session.openingDate);
            } else {
                dateEl.textContent = formatDateShort(session.referenceDate);
            }
        }

        // ---- 4. Badge dynamique ----
        if (badgeEl) {
            badgeEl.style.animation = '';
            badgeEl.style.background = '';

            if (session.state === 'open') {
                if (session.daysRemaining <= 7) {
                    badgeEl.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Derniers jours !';
                    badgeEl.style.background = '#ef4444';
                    badgeEl.style.animation = 'pulseBadge 0.8s ease-in-out infinite';
                } else if (session.daysRemaining <= 30) {
                    badgeEl.innerHTML = '<i class="fas fa-bolt"></i> Dernière ligne droite';
                    badgeEl.style.background = '#f59e0b';
                } else {
                    badgeEl.innerHTML = '<i class="fas fa-check-circle"></i> Ouvert';
                    badgeEl.style.background = '#22c55e';
                }
            } else if (session.state === 'rentree') {
                badgeEl.innerHTML = '<i class="fas fa-star"></i> Rentrée !';
                badgeEl.style.background = '#8b5cf6';
                badgeEl.style.animation = 'pulseBadge 0.8s ease-in-out infinite';
            } else if (session.state === 'open_after') {
                if (session.daysRemaining <= 7) {
                    badgeEl.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Derniers jours !';
                    badgeEl.style.background = '#ef4444';
                    badgeEl.style.animation = 'pulseBadge 0.8s ease-in-out infinite';
                } else {
                    badgeEl.innerHTML = '<i class="fas fa-check-circle"></i> Encore ouvert';
                    badgeEl.style.background = '#22c55e';
                }
            } else {
                badgeEl.innerHTML = '<i class="fas fa-lock"></i> Fermé';
                badgeEl.style.background = '#6b7280';
            }
        }

        // ---- 5. Jours restants ----
        if (joursEl) {
            const j = session.daysRemaining;
            joursEl.textContent = (j > 0) ? j : (j === 0 ? '0' : '—');
        }

        // ---- 6. Cacher le J-XX si RENTREE ----
        if (wrapperEl) {
            wrapperEl.style.display = (session.state === 'rentree') ? 'none' : '';
        }

        // ---- 7. Couleur du badge global ----
        if (badgeDiplome) {
            if (session.state === 'rentree') {
                badgeDiplome.style.borderColor = 'rgba(139, 92, 246, 0.3)';
                badgeDiplome.style.background  = 'rgba(139, 92, 246, 0.08)';
            } else if (session.state === 'open' && session.daysRemaining <= 7) {
                badgeDiplome.style.borderColor = 'rgba(239, 68, 68, 0.3)';
                badgeDiplome.style.background  = 'rgba(239, 68, 68, 0.06)';
            } else if (session.state === 'open' && session.daysRemaining <= 30) {
                badgeDiplome.style.borderColor = 'rgba(245, 158, 11, 0.2)';
                badgeDiplome.style.background  = 'rgba(245, 158, 11, 0.06)';
            } else if (session.state === 'open_after') {
                badgeDiplome.style.borderColor = 'rgba(34, 197, 94, 0.25)';
                badgeDiplome.style.background  = 'rgba(34, 197, 94, 0.06)';
            } else if (session.state === 'closed') {
                badgeDiplome.style.borderColor = 'rgba(107, 114, 128, 0.2)';
                badgeDiplome.style.background  = 'rgba(107, 114, 128, 0.06)';
            } else {
                badgeDiplome.style.borderColor = 'rgba(0, 150, 220, 0.12)';
                badgeDiplome.style.background  = 'rgba(0, 150, 220, 0.04)';
            }
        }

        // ---- 8. Stat "Prochaine rentrée" (dans about) ----
        if (nextDateEl) {
            nextDateEl.textContent = formatDateLong(session.rentreeDate);
        }

        console.log('📅 État inscriptions :', session.state, '| Jours restants :', session.daysRemaining);
    }

    // ============================================================
    // 4. EXÉCUTION
    // ============================================================
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', updateRegistrationDisplay);
    } else {
        updateRegistrationDisplay();
    }

    setInterval(updateRegistrationDisplay, 60 * 60 * 1000);

    window.updateRegistrationDisplay = updateRegistrationDisplay;
    window.getRegistrationSession = getRegistrationSession;

    console.log('📅 Module inscriptions chargé — 4 états');
})();