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

    // Création des dots
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

    // Déterminer le nombre de slides visibles selon la largeur
    function getSlidesPerView() {
        if (window.innerWidth <= 768) return 1;
        if (window.innerWidth <= 992) return 2;
        return 3;
    }

    // Mettre à jour le slider
    function goToSlide(index) {
        if (index < 0) index = totalSlides - 1;
        if (index >= totalSlides) index = 0;
        currentIndex = index;

        const offset = -(currentIndex * (100 / slidesPerView));
        slider.style.transform = `translateX(${offset}%)`;

        // Mettre à jour les dots
        document.querySelectorAll('.dot').forEach((dot, i) => {
            dot.classList.toggle('active', i === currentIndex);
        });
    }

    // Aller au slide suivant
    function nextSlide() {
        goToSlide(currentIndex + 1);
    }

    // Aller au slide précédent
    function prevSlide() {
        goToSlide(currentIndex - 1);
    }

    // Événements des boutons
    prevBtn.addEventListener('click', prevSlide);
    nextBtn.addEventListener('click', nextSlide);

    // Récupérer le nombre de slides visibles au redimensionnement
    let resizeTimeout;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimeout);
        resizeTimeout = setTimeout(() => {
            const newSlidesPerView = getSlidesPerView();
            if (newSlidesPerView !== slidesPerView) {
                slidesPerView = newSlidesPerView;
                // Ajuster la position pour éviter les décalages
                goToSlide(currentIndex);
            }
        }, 200);
    });



    // Initialisation
    goToSlide(0);

    // Navigation au clavier (flèches gauche/droite)
    document.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowLeft') prevSlide();
        if (e.key === 'ArrowRight') nextSlide();
    });


    function getDeuxiemeSeptembre(annee){
        const date = new Date(annee, 8, 1);// septembre = 9 ime mois

        //jour de la semaine : dimanche =0, lundi =1
        const jour = date.getDay();

        //nbre de jours jusqu'aux premier lundi
        const joursJusquAuLundi = (1 - jour + 7) % 7;

        // date premier lundi
        date.setDate(1 +joursJusquAuLundi);

        //deuxième lundi
        date.setDate(date.getDate() + 7);

        return date;

    }



    const nextYears = new Date().getFullYear() + 1;
    const nextDay = getDeuxiemeSeptembre(nextYears);

    const NextdateFormateer = nextDay.toLocaleDateString("fr-FR", {
        day:"numeric",
        month:"long",
        year:"numeric"

    });

    // document.getElementById("date-inscription").textContent = dateFormateer;
    document.getElementById("Next-date-inscription").textContent = NextdateFormateer;



    /**
     * ============================================================
     * DATE DE RENTRÉE – Calcul automatique du 2ème lundi de septembre
     * ============================================================
     */

    (function() {
        'use strict';

        /**
         * Calcule le 2ème lundi du mois de septembre pour une année donnée
         * @param {number} year - Année (ex: 2026)
         * @returns {Date} - Date du 2ème lundi de septembre
         */
        function getSecondMondayOfSeptember(year) {
            // 1er septembre de l'année
            const firstSeptember = new Date(year, 8, 1); // Mois 8 = Septembre
            const dayOfWeek = firstSeptember.getDay(); // 0 = Dimanche, 1 = Lundi, ...

            // Calcul du premier lundi de septembre
            let firstMondayOffset = (dayOfWeek === 1) ? 0 : (8 - dayOfWeek) % 7;
            const firstMonday = new Date(year, 8, 1 + firstMondayOffset);

            // Deuxième lundi = premier lundi + 7 jours
            const secondMonday = new Date(firstMonday);
            secondMonday.setDate(firstMonday.getDate() + 7);

            return secondMonday;
        }

        /**
         * Formate une date en "dd mmm yyyy"
         * @param {Date} date
         * @returns {string}
         */
        function formatDate(date) {
            const options = { day: '2-digit', month: 'short', year: 'numeric' };
            return date.toLocaleDateString('fr-FR', options);
        }

        /**
         * Calcule le nombre de jours entre aujourd'hui et une date cible
         * @param {Date} targetDate
         * @returns {number}
         */
        function getDaysRemaining(targetDate) {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const target = new Date(targetDate);
            target.setHours(0, 0, 0, 0);
            const diffTime = target.getTime() - today.getTime();
            return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        }

        /**
         * Met à jour l'affichage de la date de rentrée
         */
        function updateRentreeDate() {
            const currentYear = new Date().getFullYear();
            const rentreeDate = getSecondMondayOfSeptember(currentYear);

            // Formatage de la date
            const formattedDate = formatDate(rentreeDate);

            // Mise à jour de l'élément
            const dateElement = document.getElementById('date-inscription');
            if (dateElement) {
                dateElement.textContent = formattedDate;
            }

            // Calcul des jours restants
            const daysRemaining = getDaysRemaining(rentreeDate);

            // Mise à jour du compteur J-XX
            const joursRestantsSpan = document.getElementById('jours-restants-num');
            if (joursRestantsSpan) {
                if (daysRemaining > 0) {
                    joursRestantsSpan.textContent = daysRemaining;
                } else if (daysRemaining === 0) {
                    joursRestantsSpan.textContent = 'Aujourd\'hui !';
                    // Optionnel : changer le badge pour "C'est aujourd'hui !"
                    const badge = document.getElementById('dateBadgeImportant');
                    if (badge) {
                        badge.innerHTML = '<i class="fas fa-star"></i> C\'est aujourd\'hui !';
                        badge.style.background = '#22c55e';
                    }
                } else {
                    // Passé : afficher la date de l'année prochaine ?
                    // On peut recalculer pour l'année suivante
                    const nextYear = currentYear + 1;
                    const nextRentree = getSecondMondayOfSeptember(nextYear);
                    const nextDays = getDaysRemaining(nextRentree);
                    joursRestantsSpan.textContent = nextDays + ' (prochaine)';
                    // Mettre à jour la date affichée avec l'année prochaine
                    if (dateElement) {
                        dateElement.textContent = formatDate(nextRentree);
                    }
                    const badge = document.getElementById('dateBadgeImportant');
                    if (badge) {
                        badge.innerHTML = '<i class="fas fa-clock"></i> Prochaine session';
                        badge.style.background = '#f59e0b';
                    }
                }
            }

            // Mise à jour du badge "Bientôt" ou "Urgent" selon le nombre de jours
            const badgeImportant = document.getElementById('dateBadgeImportant');
            if (badgeImportant && daysRemaining > 0) {
                if (daysRemaining <= 7) {
                    badgeImportant.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Urgent !';
                    badgeImportant.style.background = '#ef4444';
                    badgeImportant.style.animation = 'pulseBadge 0.8s ease-in-out infinite';
                } else if (daysRemaining <= 30) {
                    badgeImportant.innerHTML = '<i class="fas fa-bolt"></i> Bientôt';
                    badgeImportant.style.background = '#f59e0b';
                } else {
                    badgeImportant.innerHTML = '<i class="fas fa-calendar-check"></i> Inscriptions ouvertes';
                    badgeImportant.style.background = '#22c55e';
                }
            }

            // Mise à jour du titre du badge principal (si besoin)
            const badgeDiplome = document.querySelector('.badge-diplome');
            if (badgeDiplome) {
                if (daysRemaining <= 7 && daysRemaining > 0) {
                    badgeDiplome.style.borderColor = 'rgba(239, 68, 68, 0.3)';
                    badgeDiplome.style.background = 'rgba(239, 68, 68, 0.06)';
                } else if (daysRemaining <= 30 && daysRemaining > 0) {
                    badgeDiplome.style.borderColor = 'rgba(245, 158, 11, 0.2)';
                    badgeDiplome.style.background = 'rgba(245, 158, 11, 0.06)';
                } else {
                    badgeDiplome.style.borderColor = 'rgba(0, 150, 220, 0.12)';
                    badgeDiplome.style.background = 'rgba(0, 150, 220, 0.04)';
                }
            }

            console.log('📅 Date de rentrée calculée :', formattedDate);
            console.log('📆 Jours restants :', daysRemaining);
        }

        // ============================================================
        // EXÉCUTION
        // ============================================================

        // Exécuter au chargement de la page
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', updateRentreeDate);
        } else {
            updateRentreeDate();
        }

        // Optionnel : mettre à jour toutes les 24h (si la page reste ouverte)
        setInterval(updateRentreeDate, 24 * 60 * 60 * 1000);

        // Exposer la fonction pour utilisation dans la console
        window.updateRentreeDate = updateRentreeDate;

        console.log('📅 Module de date de rentrée chargé (2ème lundi de septembre)');
    })();





})();

