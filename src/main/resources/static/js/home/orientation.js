/* ============================================================
   IFP-PO — MODULE ORIENTATION
   Quiz 5 questions → Profil personnalisé → Filières IFP-PO
   ============================================================ */

(function () {
    'use strict';

    /* ========================================================
       CONFIGURATION DES QUESTIONS
       ======================================================== */
    const QUESTIONS = [
        {
            id: 'q1',
            question: 'Qu\'est-ce que tu préfères faire ?',
            helper: 'Il n\'y a pas de bonne ou mauvaise réponse.',
            options: [
                { id: 'creer',      label: 'Créer, imaginer, concevoir',        desc: 'Tu aimes inventer, dessiner, écrire, coder',      icon: 'fa-palette',           tags: ['creatif'] },
                { id: 'aider',      label: 'Aider, soigner, accompagner',       desc: 'Tu te sens utile auprès des autres',              icon: 'fa-heart',             tags: ['humain'] },
                { id: 'organiser',  label: 'Organiser, planifier, gérer',       desc: 'Tu aimes l\'ordre, les chiffres, la méthode',     icon: 'fa-clipboard-list',    tags: ['organisateur'] },
                { id: 'analyser',   label: 'Analyser, résoudre des problèmes',  desc: 'Tu aimes comprendre comment ça marche',           icon: 'fa-brain',             tags: ['analyste', 'tech'] },
                { id: 'tech',       label: 'Manipuler, réparer, construire',    desc: 'Tu aimes le concret, les outils, le terrain',     icon: 'fa-screwdriver-wrench',tags: ['tech', 'terrain'] }
            ]
        },
        {
            id: 'q2',
            question: 'Tu te vois plutôt…',
            helper: 'Ton environnement de travail idéal.',
            options: [
                { id: 'terrain', label: 'Sur le terrain',        desc: 'En extérieur, en déplacement, au contact direct', icon: 'fa-mountain-sun',  tags: ['terrain'] },
                { id: 'bureau',  label: 'Dans un bureau',        desc: 'Assis, posé, avec mes outils et mes dossiers',    icon: 'fa-chair-office',  tags: ['bureau'] },
                { id: 'mixte',   label: 'Un peu des deux',       desc: 'Alterner terrain et bureau',                      icon: 'fa-shuffle',       tags: ['terrain', 'bureau'] }
            ]
        },
        {
            id: 'q3',
            question: 'Ton BAC, c\'est plutôt…',
            helper: 'Aucune série ne ferme de porte. C\'est juste une information.',
            options: [
                { id: 'A',  label: 'Bac A (Littéraire)',          desc: 'Français, philo, langues',                     icon: 'fa-book',          tags: ['litteraire'] },
                { id: 'C',  label: 'Bac C (Maths + Sciences)',    desc: 'Maths, physique, chimie',                      icon: 'fa-square-root-variable', tags: ['scientifique', 'analyste'] },
                { id: 'D',  label: 'Bac D (Sciences)',            desc: 'SVT, maths, physique',                         icon: 'fa-dna',           tags: ['scientifique', 'humain'] },
                { id: 'TI', label: 'Bac TI / Informatique',       desc: 'Technologies, informatique',                   icon: 'fa-laptop-code',   tags: ['tech', 'creatif'] },
                { id: 'autre', label: 'Autre série',              desc: 'G, ACC, ESF, CG, etc.',                        icon: 'fa-graduation-cap',tags: [] }
            ]
        },
        {
            id: 'q4',
            question: 'Dans 5 ans, tu te vois…',
            helper: 'Ton ambition, ton rythme.',
            options: [
                { id: 'travail_rapide', label: 'Déjà au travail',               desc: 'Je veux gagner ma vie vite et bien',            icon: 'fa-briefcase',      tags: ['rapide'] },
                { id: 'longues_etudes', label: 'En train de continuer mes études', desc: 'Je veux aller plus loin académiquement',      icon: 'fa-book-open-reader',tags: ['academique'] },
                { id: 'entreprendre',   label: 'À mon propre compte',            desc: 'Créer mon business, mon activité',              icon: 'fa-rocket',         tags: ['entrepreneur'] }
            ]
        },
        {
            id: 'q5',
            question: 'Ce qui te fait vibrer…',
            helper: 'Ta passion, ton centre d\'intérêt.',
            options: [
                { id: 'num',   label: 'Le numérique et la tech',      desc: 'Ordinateurs, web, réseaux, applications',       icon: 'fa-microchip',      tags: ['tech', 'creatif'] },
                { id: 'sante', label: 'Le corps humain et la santé',  desc: 'Soigner, comprendre le vivant',                  icon: 'fa-stethoscope',    tags: ['humain'] },
                { id: 'terre', label: 'La terre, les animaux',        desc: 'Agropastoral, agriculture, élevage',             icon: 'fa-seedling',       tags: ['agro', 'terrain'] },
                { id: 'bati',  label: 'Construire, bâtir, réparer',   desc: 'Chantiers, machines, outils',                    icon: 'fa-helmet-safety',  tags: ['bati', 'terrain'] },
                { id: 'gestion', label: 'Chiffres, gestion, accueil', desc: 'Comptabilité, secrétariat, hôtellerie',          icon: 'fa-calculator',     tags: ['organisateur', 'service'] }
            ]
        }
    ];

    /* ========================================================
       CONFIGURATION DES PROFILS
       ======================================================== */
    const PROFILES = {
        creatif_tech: {
            id: 'creatif_tech',
            name: 'CRÉATIF & TECH',
            icon: 'fa-wand-magic-sparkles',
            description: 'Tu <strong>crées</strong>, tu <strong>imagines</strong>, tu <strong>expérimentes</strong>. Les outils numériques sont tes alliés naturels et tu cherches un métier où tu vois concrètement le fruit de ton travail. Tu es fait pour les métiers de demain : <strong>web, design, digital</strong>.',
            keywords: ['creatif', 'tech']
        },
        organisateur_gestion: {
            id: 'organisateur_gestion',
            name: 'ORGANISATEUR & GESTION',
            icon: 'fa-chart-line',
            description: 'Tu es <strong>structuré</strong>, <strong>rigoureux</strong>, à l\'aise avec les <strong>chiffres et les procédures</strong>. Tu aimes que tout soit carré et bien géré. Ce profil ouvre la voie aux métiers de <strong>gestion, secrétariat, comptabilité et marketing</strong>.',
            keywords: ['organisateur']
        },
        humain_sante: {
            id: 'humain_sante',
            name: 'HUMAIN & SANTÉ',
            icon: 'fa-heart-pulse',
            description: 'Tu es <strong>empathique</strong>, à l\'écoute, tourné vers les autres. Tu veux un métier qui a du <strong>sens</strong> et où tu prends soin des gens. Ce profil mène aux carrières <strong>paramédicales et d\'accompagnement</strong>.',
            keywords: ['humain']
        },
        technicien_terrain: {
            id: 'technicien_terrain',
            name: 'TECHNICIEN & TERRAIN',
            icon: 'fa-screwdriver-wrench',
            description: 'Tu es <strong>concret</strong>, <strong>manuel</strong>, tu aimes <strong>réparer, construire, manipuler</strong>. Tu te sens à l\'aise en extérieur, sur le terrain. Ce profil correspond aux métiers de la <strong>maintenance, du BTP, de l\'agropastoral</strong>.',
            keywords: ['tech', 'terrain', 'bati', 'agro']
        },
        service_accueil: {
            id: 'service_accueil',
            name: 'ACCUEIL & SERVICE',
            icon: 'fa-concierge-bell',
            description: 'Tu es <strong>sociable</strong>, <strong>souriant</strong>, tu aimes le contact et le service. Tu veux un métier où chaque jour est différent. Ce profil correspond aux métiers de l\'<strong>hôtellerie, la restauration et l\'accueil</strong>.',
            keywords: ['service']
        },
        analyste_scientifique: {
            id: 'analyste_scientifique',
            name: 'ANALYSTE & SCIENTIFIQUE',
            icon: 'fa-flask-vial',
            description: 'Tu es <strong>logique</strong>, <strong>méthodique</strong>, tu aimes <strong>comprendre et résoudre</strong>. Tu cherches un métier qui demande de la réflexion. Ce profil mène aux métiers <strong>techniques, scientifiques et de gestion informatisée</strong>.',
            keywords: ['analyste', 'scientifique']
        }
    };

    /* ========================================================
       FILIÈRES IFP-PO
       ======================================================== */
    const FILIERES = [
        {
            id: 'dev_web',
            name: 'Développement Web',
            icon: 'fa-code',
            duration: '24 mois',
            level: 'DQP',
            why: 'Tu aimes créer et résoudre des problèmes. Le code est le langage des métiers de demain.',
            debouches: 'Développeur front-end • Intégrateur • Freelance • Technicien support',
            keywords: ['creatif', 'tech', 'analyste']
        },
        {
            id: 'design_graphique',
            name: 'Design Graphique',
            icon: 'fa-palette',
            duration: '18 mois',
            level: 'CQP',
            why: 'Tu es visuel et créatif. Tu transformes les idées en images qui parlent.',
            debouches: 'Infographiste • Community manager • Designer freelance • Imprimerie',
            keywords: ['creatif', 'tech']
        },
        {
            id: 'marketing_digital',
            name: 'Marketing Digital',
            icon: 'fa-bullhorn',
            duration: '12 mois',
            level: 'AQP',
            why: 'Tu aimes créer ET organiser. Le digital est le terrain de jeu parfait.',
            debouches: 'Community manager • Chargé de communication • Freelance digital',
            keywords: ['creatif', 'organisateur', 'tech']
        },
        {
            id: 'secretariat',
            name: 'Secrétariat Bureautique',
            icon: 'fa-keyboard',
            duration: '12 mois',
            level: 'CQP',
            why: 'Tu es organisé, rigoureux et à l\'aise avec les outils de bureau.',
            debouches: 'Secrétaire • Assistant de direction • Agent administratif',
            keywords: ['organisateur', 'bureau']
        },
        {
            id: 'compta',
            name: 'Comptabilité Informatisée',
            icon: 'fa-calculator',
            duration: '24 mois',
            level: 'DQP',
            why: 'Tu aimes les chiffres, la précision et la méthode.',
            debouches: 'Comptable • Assistant comptable • Gestionnaire PME',
            keywords: ['organisateur', 'analyste']
        },
        {
            id: 'maintenance',
            name: 'Maintenance Informatique',
            icon: 'fa-tools',
            duration: '24 mois',
            level: 'DQP',
            why: 'Tu aimes le concret ET la tech. Tu veux comprendre et réparer.',
            debouches: 'Technicien de maintenance • Réparateur • Support IT',
            keywords: ['tech', 'terrain', 'analyste']
        },
        {
            id: 'paramedical',
            name: 'Médecine Paramédicale',
            icon: 'fa-user-nurse',
            duration: '24 mois',
            level: 'DQP',
            why: 'Tu es empathique et tourné vers les autres. Ce métier a du sens.',
            debouches: 'Aide-soignant • Assistant paramédical • Agent de santé',
            keywords: ['humain']
        },
        {
            id: 'agropastoral',
            name: 'Agropastoral',
            icon: 'fa-seedling',
            duration: '18 mois',
            level: 'CQP',
            why: 'Tu aimes la terre et les animaux. Le secteur agricole est l\'avenir du Cameroun.',
            debouches: 'Agri-entrepreneur • Technicien agricole • Éleveur moderne',
            keywords: ['agro', 'terrain']
        },
        {
            id: 'btp',
            name: 'BTP (Bâtiment & Travaux Publics)',
            icon: 'fa-helmet-safety',
            duration: '24 mois',
            level: 'DQP',
            why: 'Tu aimes construire, bâtir, voir le résultat de tes mains.',
            debouches: 'Maçon qualifié • Chef de chantier • Entrepreneur BTP',
            keywords: ['bati', 'terrain']
        },
        {
            id: 'hotellerie',
            name: 'Hôtellerie & Restauration',
            icon: 'fa-utensils',
            duration: '18 mois',
            level: 'CQP',
            why: 'Tu aimes le contact, le service et l\'accueil. Chaque jour est différent.',
            debouches: 'Serveur • Cuisinier • Réceptionniste • Gérant d\'hôtel',
            keywords: ['service', 'organisateur']
        }
    ];

    /* ========================================================
       ÉTAT DU QUIZ
       ======================================================== */
    let currentQuestionIndex = 0;
    let userAnswers = {}; // { q1: 'creer', q2: 'terrain', ... }

    /* ========================================================
       RÉFÉRENCES DOM
       ======================================================== */
    const heroSection    = document.querySelector('.orient-hero');
    const quizSection    = document.getElementById('quizSection');
    const resultSection  = document.getElementById('resultSection');
    const quizCard       = document.getElementById('quizCard');
    const progressLabel  = document.getElementById('progressLabel');
    const progressPercent= document.getElementById('progressPercent');
    const progressFill   = document.getElementById('progressFill');
    const quizBack       = document.getElementById('quizBack');
    const quizRestart    = document.getElementById('quizRestart');
    const startQuiz      = document.getElementById('startQuiz');
    const restartFromResult = document.getElementById('restartFromResult');
    const filieresGrid   = document.getElementById('filieresGrid');
    const profileMain    = document.getElementById('profileMain');
    const profileSecondary = document.getElementById('profileSecondary');
    const ctaFilieresCount = document.getElementById('ctaFilieresCount');
    const reassuranceDates = document.getElementById('reassuranceDates');
    const footerYear     = document.getElementById('footerYear');

    /* ========================================================
       UTILITAIRES
       ======================================================== */
    function scrollToTop() {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    function getSecondMondayOfSeptember(year) {
        const firstSeptember = new Date(year, 8, 1);
        const dayOfWeek = firstSeptember.getDay();
        const offset = (dayOfWeek === 1) ? 0 : (8 - dayOfWeek) % 7;
        const firstMonday = new Date(year, 8, 1 + offset);
        const secondMonday = new Date(firstMonday);
        secondMonday.setDate(firstMonday.getDate() + 7);
        return secondMonday;
    }

    function getClosingDate(year) {
        return new Date(year, 9, 31, 23, 59, 59);
    }

    function formatDate(date) {
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        });
    }

    /* ========================================================
       RENDU DU QUIZ
       ======================================================== */
    function renderQuestion() {
        const question = QUESTIONS[currentQuestionIndex];

        // Progression
        const total = QUESTIONS.length;
        const current = currentQuestionIndex + 1;
        const percent = Math.round((current / total) * 100);

        progressLabel.textContent = `Question ${current} sur ${total}`;
        progressPercent.textContent = `${percent}%`;
        progressFill.style.width = `${percent}%`;

        // Bouton précédent
        quizBack.disabled = currentQuestionIndex === 0;

        // Construire le HTML
        const optionsHTML = question.options.map(opt => {
            const isSelected = userAnswers[question.id] === opt.id;
            return `
                <button class="quiz-option ${isSelected ? 'selected' : ''}" data-option-id="${opt.id}">
                    <div class="quiz-option-icon">
                        <i class="fas ${opt.icon}"></i>
                    </div>
                    <div class="quiz-option-text">
                        <span class="quiz-option-label">${opt.label}</span>
                        <span class="quiz-option-desc">${opt.desc}</span>
                    </div>
                </button>
            `;
        }).join('');

        quizCard.innerHTML = `
            <h2 class="quiz-question">${question.question}</h2>
            <p class="quiz-helper">${question.helper}</p>
            <div class="quiz-options">
                ${optionsHTML}
            </div>
        `;

        // Réattacher les événements
        quizCard.querySelectorAll('.quiz-option').forEach(btn => {
            btn.addEventListener('click', () => selectOption(btn.dataset.optionId));
        });

        // Re-animer
        quizCard.style.animation = 'none';
        setTimeout(() => { quizCard.style.animation = 'slideIn 0.45s var(--ease)'; }, 10);
    }

    function selectOption(optionId) {
        const question = QUESTIONS[currentQuestionIndex];
        userAnswers[question.id] = optionId;

        // Feedback visuel
        quizCard.querySelectorAll('.quiz-option').forEach(btn => {
            btn.classList.toggle('selected', btn.dataset.optionId === optionId);
        });

        // Passer à la question suivante après un court délai
        setTimeout(() => {
            if (currentQuestionIndex < QUESTIONS.length - 1) {
                currentQuestionIndex++;
                renderQuestion();
            } else {
                showResult();
            }
        }, 380);
    }

    /* ========================================================
       CALCUL DU PROFIL
       ======================================================== */
    function computeProfile() {
        // 1. Rassembler tous les tags sélectionnés
        const tagCounts = {};

        QUESTIONS.forEach(q => {
            const selectedId = userAnswers[q.id];
            if (!selectedId) return;

            const selectedOption = q.options.find(o => o.id === selectedId);
            if (!selectedOption) return;

            selectedOption.tags.forEach(tag => {
                tagCounts[tag] = (tagCounts[tag] || 0) + 1;
            });
        });

        // 2. Scorer chaque profil
        const profileScores = Object.values(PROFILES).map(profile => {
            let score = 0;
            profile.keywords.forEach(kw => {
                score += tagCounts[kw] || 0;
            });
            return { profile, score };
        });

        // 3. Trier par score décroissant
        profileScores.sort((a, b) => b.score - a.score);

        // 4. Retourner les 2 meilleurs (le secondaire doit avoir score > 0)
        const main = profileScores[0].profile;
        const secondary = profileScores[1].score > 0 ? profileScores[1].profile : null;

        return { main, secondary };
    }

    /* ========================================================
       CALCUL DES FILIÈRES RECOMMANDÉES
       ======================================================== */
    function computeFilieres(profileMainData, profileSecondaryData) {
        // Compter les mots-clés pondérés
        const tagCounts = {};
        QUESTIONS.forEach(q => {
            const selectedId = userAnswers[q.id];
            if (!selectedId) return;
            const opt = q.options.find(o => o.id === selectedId);
            if (!opt) return;
            opt.tags.forEach(t => {
                tagCounts[t] = (tagCounts[t] || 0) + 1;
            });
        });

        // Scorer chaque filière
        const scored = FILIERES.map(f => {
            let score = 0;
            f.keywords.forEach(kw => {
                score += tagCounts[kw] || 0;
            });
            return { filiere: f, score };
        });

        // Trier par score
        scored.sort((a, b) => b.score - a.score);

        // Prendre les 3 meilleures (avec score > 0)
        const top = scored.filter(s => s.score > 0).slice(0, 3);

        // Si aucune filière ne matche, retourner les 3 premières
        if (top.length === 0) {
            return scored.slice(0, 3).map(s => ({ filiere: s.filiere, score: 1 }));
        }

        return top.map(s => ({
            filiere: s.filiere,
            score: Math.min(100, Math.round((s.score / (QUESTIONS.length * 1.5)) * 100) + 50)
        }));
    }

    /* ========================================================
       AFFICHAGE DU RÉSULTAT
       ======================================================== */
    function showResult() {
        heroSection.style.display = 'none';
        quizSection.style.display = 'none';
        resultSection.style.display = 'block';

        const { main, secondary } = computeProfile();
        const filieresReco = computeFilieres(main, secondary);

        // Profil dominant
        profileMain.innerHTML = `
            <div class="profile-header">
                <div class="profile-icon">
                    <i class="fas ${main.icon}"></i>
                </div>
                <div class="profile-name-block">
                    <div class="profile-label">Profil dominant</div>
                    <div class="profile-name">${main.name}</div>
                </div>
            </div>
            <p class="profile-desc">${main.description}</p>
        `;

        // Profil secondaire
        if (secondary) {
            profileSecondary.style.display = 'block';
            profileSecondary.innerHTML = `
                <div class="profile-header">
                    <div class="profile-icon">
                        <i class="fas ${secondary.icon}"></i>
                    </div>
                    <div class="profile-name-block">
                        <div class="profile-label">Profil secondaire</div>
                        <div class="profile-name">${secondary.name}</div>
                    </div>
                </div>
                <p class="profile-desc">${secondary.description}</p>
            `;
        } else {
            profileSecondary.style.display = 'none';
        }

        // Filières
        filieresGrid.innerHTML = filieresReco.map((item, i) => {
            const f = item.filiere;
            const score = item.score;
            return `
                <div class="filiere-card">
                    <div class="filiere-card-head">
                        <div class="filiere-head-top">
                            <div class="filiere-head-icon">
                                <i class="fas ${f.icon}"></i>
                            </div>
                            <div class="filiere-score">${score}% match</div>
                        </div>
                        <div class="filiere-card-title">${f.name}</div>
                    </div>
                    <div class="filiere-card-body">
                        <div class="filiere-meta">
                            <div class="filiere-meta-item">
                                <i class="fas fa-clock"></i>
                                ${f.duration}
                            </div>
                            <div class="filiere-meta-item">
                                <i class="fas fa-certificate"></i>
                                ${f.level}
                            </div>
                        </div>
                        <p class="filiere-why">
                            <strong>Pourquoi cette filière ?</strong> ${f.why}
                        </p>
                        <div class="filiere-debouches">
                            <span class="filiere-debouches-label">Débouchés</span>
                            <div class="filiere-debouches-list">${f.debouches}</div>
                        </div>
                        <a href="/offres?filiereId=${f.id}" class="filiere-cta">
                            En savoir plus
                            <i class="fas fa-arrow-right"></i>
                        </a>
                    </div>
                </div>
            `;
        }).join('');

        // CTA compte
        ctaFilieresCount.textContent = filieresReco.length + ' filières';

        // Date rassurance
        if (reassuranceDates) {
            const now = new Date();
            const year = now.getFullYear();
            const opening = getSecondMondayOfSeptember(year);
            const closing = getClosingDate(year);

            if (now >= opening && now <= closing) {
                reassuranceDates.textContent = `Clôture le ${formatDate(closing)}`;
            } else {
                const nextOpening = now < opening
                    ? opening
                    : getSecondMondayOfSeptember(year + 1);
                reassuranceDates.textContent = `Ouverture le ${formatDate(nextOpening)}`;
            }
        }

        scrollToTop();
    }

    /* ========================================================
       GESTION DE LA NAVIGATION
       ======================================================== */
    function startQuizFlow() {
        currentQuestionIndex = 0;
        userAnswers = {};

        heroSection.style.display = 'none';
        resultSection.style.display = 'none';
        quizSection.style.display = 'block';

        renderQuestion();
        scrollToTop();
    }

    function restartQuiz() {
        currentQuestionIndex = 0;
        userAnswers = {};
        startQuizFlow();
    }

    /* ========================================================
       ÉVÉNEMENTS
       ======================================================== */
    startQuiz.addEventListener('click', startQuizFlow);
    quizRestart.addEventListener('click', restartQuiz);
    restartFromResult.addEventListener('click', restartQuiz);

    quizBack.addEventListener('click', () => {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            renderQuestion();
        }
    });

    // Année footer
    if (footerYear) {
        footerYear.textContent = new Date().getFullYear();
    }

    // Log de démarrage
    console.log('%c🎯 Module Orientation IFP-PO chargé', 'color: #d4af37; font-weight: bold; font-size: 14px;');
    console.log('   → ' + QUESTIONS.length + ' questions, ' + Object.keys(PROFILES).length + ' profils, ' + FILIERES.length + ' filières');

})();