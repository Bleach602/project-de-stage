// ============================================================
// SLIDER ÉQUIPE
// ============================================================
(function() {
    "use strict";

    const slider = document.getElementById('equipeSlider');
    const slides = slider.querySelectorAll('.slide');
    const totalSlides = slides.length;
    let currentIndex = 0;
    let slidesPerView = getSlidesPerView();

    const prevBtn = document.getElementById('prevSlide');
    const nextBtn = document.getElementById('nextSlide');
    const dotsContainer = document.getElementById('sliderDots');

    function createDots() {
        dotsContainer.innerHTML = '';
        for (let i = 0; i < totalSlides; i++) {
            const dot = document.createElement('button');
            dot.classList.add('dot');
            if (i === 0) dot.classList.add('active');
            dot.dataset.index = i;
            dot.addEventListener('click', function() {
                goToSlide(parseInt(this.dataset.index));
            });
            dotsContainer.appendChild(dot);
        }
    }
    createDots();

    function getSlidesPerView() {
        if (window.innerWidth <= 768) return 1;
        if (window.innerWidth <= 992) return 2;
        return 3;
    }

    function goToSlide(index) {
        if (index < 0) index = totalSlides - 1;
        if (index >= totalSlides) index = 0;
        currentIndex = index;

        const offset = -(currentIndex * (100 / slidesPerView));
        slider.style.transform = `translateX(${offset}%)`;

        document.querySelectorAll('.dot').forEach((dot, i) => {
            dot.classList.toggle('active', i === currentIndex);
        });
    }

    function nextSlide() { goToSlide(currentIndex + 1); }
    function prevSlide() { goToSlide(currentIndex - 1); }

    prevBtn.addEventListener('click', prevSlide);
    nextBtn.addEventListener('click', nextSlide);

    let resizeTimeout;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimeout);
        resizeTimeout = setTimeout(() => {
            const newSlidesPerView = getSlidesPerView();
            if (newSlidesPerView !== slidesPerView) {
                slidesPerView = newSlidesPerView;
                goToSlide(currentIndex);
            }
        }, 200);
    });

    goToSlide(0);

    document.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowLeft') prevSlide();
        if (e.key === 'ArrowRight') nextSlide();
    });


    /**
     * ============================================================
     * MODULE DATES D'INSCRIPTION
     * ------------------------------------------------------------
     * Règle métier :
     *   - Ouverture  : 2ème lundi de septembre (année N)
     *   - Clôture    : 31 octobre 23h59 (année N)
     *   - Du 1er novembre (N) au 2ème lundi de septembre (N+1) :
     *     on affiche la PROCHAINE session (année N+1)
     *
     * États :
     *   - "soon"   : avant l'ouverture de cette année
     *   - "open"   : pendant la période d'inscription
     *   - "closed" : après le 31 octobre
     * ============================================================
     */
    (function() {
        'use strict';

        function getSecondMondayOfSeptember(year) {
            const firstSeptember = new Date(year, 8, 1); // Mois 8 = septembre
            const dayOfWeek = firstSeptember.getDay();   // 0 = dimanche
            const firstMondayOffset = (dayOfWeek === 1) ? 0 : (8 - dayOfWeek) % 7;
            const firstMonday = new Date(year, 8, 1 + firstMondayOffset);
            const secondMonday = new Date(firstMonday);
            secondMonday.setDate(firstMonday.getDate() + 7);
            secondMonday.setHours(0, 0, 0, 0);
            return secondMonday;
        }

        function getClosingDate(year) {
            // 31 octobre 23:59:59
            return new Date(year, 9, 31, 23, 59, 59);
        }

        function formatDate(date) {
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

        function getRegistrationSession() {
            const now = new Date();
            const currentYear = now.getFullYear();

            const openingThisYear = getSecondMondayOfSeptember(currentYear);
            const closingThisYear = getClosingDate(currentYear);

            // Cas 1 : avant l'ouverture de cette année
            if (now < openingThisYear) {
                return {
                    state: 'soon',
                    sessionYear: currentYear,
                    openingDate: openingThisYear,
                    closingDate: closingThisYear,
                    referenceDate: openingThisYear,
                    daysRemaining: getDaysRemaining(openingThisYear)
                };
            }

            // Cas 2 : pendant la période d'inscription
            if (now >= openingThisYear && now <= closingThisYear) {
                return {
                    state: 'open',
                    sessionYear: currentYear,
                    openingDate: openingThisYear,
                    closingDate: closingThisYear,
                    referenceDate: closingThisYear,
                    daysRemaining: getDaysRemaining(closingThisYear)
                };
            }

            // Cas 3 : après la clôture (à partir du 1er novembre)
            const nextYear = currentYear + 1;
            const openingNextYear = getSecondMondayOfSeptember(nextYear);
            const closingNextYear = getClosingDate(nextYear);

            return {
                state: 'closed',
                sessionYear: nextYear,
                openingDate: openingNextYear,
                closingDate: closingNextYear,
                referenceDate: openingNextYear,
                daysRemaining: getDaysRemaining(openingNextYear)
            };
        }

        function updateRegistrationDisplay() {
            const session = getRegistrationSession();

            const dateEl       = document.getElementById('date-inscription');
            const badgeEl      = document.getElementById('dateBadgeImportant');
            const joursEl      = document.getElementById('jours-restants-num');
            const nextDateEl   = document.getElementById('Next-date-inscription');
            const badgeDiplome = document.querySelector('.badge-diplome');
            const prefixEl     = document.getElementById('datePrefix');
            const badgeLabelEl = document.getElementById('badgeLabel');

            // 1. Date dans le badge hero
            if (dateEl) {
                dateEl.textContent = formatDate(session.referenceDate);
            }

            // 2. Préfixe "Clôture le" / "Ouverture le" / "Prochaine session :"
            if (prefixEl) {
                if (session.state === 'open') {
                    prefixEl.textContent = 'Clôture le';
                } else if (session.state === 'soon') {
                    prefixEl.textContent = 'Ouverture le';
                } else {
                    prefixEl.textContent = 'Prochaine session :';
                }
            }

            // 3. Libellé principal du badge
            if (badgeLabelEl) {
                if (session.state === 'open') {
                    badgeLabelEl.textContent = 'Inscriptions ouvertes';
                } else if (session.state === 'soon') {
                    badgeLabelEl.textContent = 'Inscriptions bientôt ouvertes';
                } else {
                    badgeLabelEl.textContent = 'Inscriptions clôturées';
                }
            }

            // 4. Stat "Prochaine rentrée"
            if (nextDateEl) {
                nextDateEl.textContent = formatDateLong(session.openingDate);
            }

            // 5. Jours restants
            if (joursEl) {
                joursEl.textContent = session.daysRemaining;
            }

            // 6. Badge principal (couleur + texte)
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
                } else if (session.state === 'soon') {
                    badgeEl.innerHTML = '<i class="fas fa-clock"></i> Ouverture bientôt';
                    badgeEl.style.background = '#f59e0b';
                } else {
                    badgeEl.innerHTML = '<i class="fas fa-lock"></i> Fermé';
                    badgeEl.style.background = '#6b7280';
                }
            }

            // 7. Couleur du badge-diplome global
            if (badgeDiplome) {
                if (session.state === 'open' && session.daysRemaining <= 7) {
                    badgeDiplome.style.borderColor = 'rgba(239, 68, 68, 0.3)';
                    badgeDiplome.style.background  = 'rgba(239, 68, 68, 0.06)';
                } else if (session.state === 'open' && session.daysRemaining <= 30) {
                    badgeDiplome.style.borderColor = 'rgba(245, 158, 11, 0.2)';
                    badgeDiplome.style.background  = 'rgba(245, 158, 11, 0.06)';
                } else {
                    badgeDiplome.style.borderColor = 'rgba(0, 150, 220, 0.12)';
                    badgeDiplome.style.background  = 'rgba(0, 150, 220, 0.04)';
                }
            }

        }

        // ============================================================
        // EXÉCUTION
        // ============================================================
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', updateRegistrationDisplay);
        } else {
            updateRegistrationDisplay();
        }

        setInterval(updateRegistrationDisplay, 60 * 60 * 1000);

        window.updateRegistrationDisplay = updateRegistrationDisplay;
        window.getRegistrationSession   = getRegistrationSession;

        console.log('📅 Module inscriptions chargé — 2ème lundi sept. → 31 octobre');
    })();

})();