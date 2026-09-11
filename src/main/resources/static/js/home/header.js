
// ============================================================
// MENU MOBILE
// ============================================================
const mobileMenu = document.getElementById('mobileMenu');
const navLinks = document.getElementById('navLinks');

mobileMenu.addEventListener('click', function(e) {
    e.stopPropagation();
    navLinks.classList.toggle('open');
});

document.querySelectorAll('.nav-links .nav-item').forEach(function(item) {
    item.addEventListener('click', function() {
        navLinks.classList.remove('open');
    });
});

document.addEventListener('click', function(e) {
    if (!e.target.closest('.navbar')) {
        navLinks.classList.remove('open');
    }
});

// ============================================================
// NAVIGATION ACTIVE
// ============================================================
const navItems = document.querySelectorAll('.nav-item');
const sections = document.querySelectorAll('section[id]');

window.addEventListener('scroll', function() {
    let current = 'accueil';
    sections.forEach(function(section) {
        const sectionTop = section.offsetTop - 150;
        if (window.scrollY >= sectionTop) {
            current = section.getAttribute('id');
        }
    });

    navItems.forEach(function(item) {
        item.classList.remove('active');
        if (item.getAttribute('href') === '#' + current) {
            item.classList.add('active');
        }
    });
});


// ============================================================
// SLIDER ÉQUIPE — AUTO & MANUEL
// ============================================================
document.addEventListener('DOMContentLoaded', function() {
    const sliderContainer = document.getElementById('equipeSlider');
    const prevBtn = document.getElementById('prevSlide');
    const nextBtn = document.getElementById('nextSlide');
    const dotsContainer = document.getElementById('sliderDots');

    if (!sliderContainer) return;

    const slides = sliderContainer.querySelectorAll('.slide');
    const totalSlides = slides.length;
    let currentIndex = 0;
    let autoSlideInterval = null;
    let isTransitioning = false;

    // ============================================================
    // CRÉATION DES DOTS
    // ============================================================
    if (dotsContainer && totalSlides > 1) {
        for (let i = 0; i < totalSlides; i++) {
            const dot = document.createElement('button');
            dot.className = 'dot' + (i === 0 ? ' active' : '');
            dot.setAttribute('data-index', i);
            dot.setAttribute('aria-label', 'Aller à la diapositive ' + (i + 1));
            dot.addEventListener('click', function() {
                goToSlide(parseInt(this.dataset.index));
            });
            dotsContainer.appendChild(dot);
        }
    }

    // ============================================================
    // FONCTIONS DE NAVIGATION
    // ============================================================
    function goToSlide(index) {
        if (isTransitioning || index === currentIndex) return;
        if (index < 0) index = totalSlides - 1;
        if (index >= totalSlides) index = 0;

        isTransitioning = true;
        currentIndex = index;

        // Scroll vers la slide
        const slide = slides[index];
        if (slide) {
            sliderContainer.scrollTo({
                left: slide.offsetLeft,
                behavior: 'smooth'
            });
        }

        // Mise à jour des dots
        updateDots();

        // Réinitialiser l'auto-slide
        resetAutoSlide();

        setTimeout(function() {
            isTransitioning = false;
        }, 500);
    }

    function updateDots() {
        if (!dotsContainer) return;
        const dots = dotsContainer.querySelectorAll('.dot');
        dots.forEach(function(dot, i) {
            dot.classList.toggle('active', i === currentIndex);
        });
    }

    function nextSlide() {
        goToSlide(currentIndex + 1);
    }

    function prevSlide() {
        goToSlide(currentIndex - 1);
    }

    // ============================================================
    // AUTO-SLIDE
    // ============================================================
    function startAutoSlide() {
        if (autoSlideInterval) clearInterval(autoSlideInterval);
        if (totalSlides > 1) {
            autoSlideInterval = setInterval(nextSlide, 5000);
        }
    }

    function resetAutoSlide() {
        if (autoSlideInterval) {
            clearInterval(autoSlideInterval);
            startAutoSlide();
        }
    }

    function stopAutoSlide() {
        if (autoSlideInterval) {
            clearInterval(autoSlideInterval);
            autoSlideInterval = null;
        }
    }

    // ============================================================
    // DÉTECTION DE LA FIN DU SCROLL
    // ============================================================
    sliderContainer.addEventListener('scroll', function() {
        if (isTransitioning) return;

        // Trouver la slide la plus proche
        let closestIndex = 0;
        let closestDistance = Infinity;

        slides.forEach(function(slide, index) {
            const rect = slide.getBoundingClientRect();
            const containerRect = sliderContainer.getBoundingClientRect();
            const distance = Math.abs(rect.left - containerRect.left);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = index;
            }
        });

        if (closestIndex !== currentIndex) {
            currentIndex = closestIndex;
            updateDots();
        }
    });

    // ============================================================
    // PAUSE AU SURVOL
    // ============================================================
    const sliderWrapper = document.querySelector('.slider-wrapper');
    if (sliderWrapper) {
        sliderWrapper.addEventListener('mouseenter', stopAutoSlide);
        sliderWrapper.addEventListener('mouseleave', startAutoSlide);
    }

    // ============================================================
    // ÉVÉNEMENTS BOUTONS
    // ============================================================
    if (prevBtn) prevBtn.addEventListener('click', prevSlide);
    if (nextBtn) nextBtn.addEventListener('click', nextSlide);

    // ============================================================
    // TOUCHES CLAVIER (accessibilité)
    // ============================================================
    document.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowLeft') prevSlide();
        if (e.key === 'ArrowRight') nextSlide();
    });

    // ============================================================
    // INITIALISATION
    // ============================================================
    startAutoSlide();

    // ============================================================
    // AOS ANIMATION (simple)
    // ============================================================
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(function(entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add('aos-animate');
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    document.querySelectorAll('[data-aos]').forEach(function(el) {
        observer.observe(el);
    });

    console.log('🚀 Slider équipe initialisé avec ' + totalSlides + ' membres.');



});