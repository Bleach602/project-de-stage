/**
 * ============================================================
 * FICHIER : login_register.js
 * VERSION : Finale – avec intégration Spring Boot
 * ============================================================
 */

(function() {
    'use strict';

    // ============================================================
    // 1. PERLES FLOTTANTES DÉCORATIVES
    // ============================================================
    (function() {
        const field = document.getElementById('pearlsField');
        if (!field) return;
        const count = 12;
        for (let i = 0; i < count; i++) {
            const p = document.createElement('div');
            p.className = 'pearl-dot';
            p.style.left = Math.random() * 100 + '%';
            p.style.top = (Math.random() * 55) + '%';
            p.style.animationDelay = (Math.random() * 9) + 's';
            p.style.animationDuration = (7 + Math.random() * 6) + 's';
            p.style.opacity = (0.3 + Math.random() * 0.4).toFixed(2);
            field.appendChild(p);
        }
    })();

    // ============================================================
    // 2. TABS
    // ============================================================
    window.switchTab = function(mode) {
        document.getElementById('app').setAttribute('data-mode', mode);
    };

    // ============================================================
    // 3. PASSWORD TOGGLE
    // ============================================================
    window.togglePassword = function(id, btn) {
        const input = document.getElementById(id);
        if (!input) return;
        const isHidden = input.type === 'password';
        input.type = isHidden ? 'text' : 'password';
        btn.innerHTML = isHidden
            ? '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M3 3l18 18"/><path d="M10.6 10.6a3 3 0 004.24 4.24"/><path d="M6.6 6.6C4 8.3 2 12 2 12s4 7.5 10 7.5c1.8 0 3.4-.5 4.8-1.3M12 4.5c6 0 10 7.5 10 7.5a15.6 15.6 0 01-2.1 3"/></svg>'
            : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M1.5 12s4-7.5 10.5-7.5S22.5 12 22.5 12s-4 7.5-10.5 7.5S1.5 12 1.5 12z"/><circle cx="12" cy="12" r="3"/></svg>';
        btn.setAttribute('aria-label', isHidden ? 'Masquer le mot de passe' : 'Afficher le mot de passe');
    };

    // ============================================================
    // 4. REGISTER WIZARD
    // ============================================================
    function setError(fieldId, hasError) {
        const el = document.getElementById(fieldId);
        if (el) {
            el.classList.toggle('error', hasError);
        }
    }

    function validateStep(step) {
        let ok = true;
        if (step === 1) {
            const prenom = document.getElementById('prenom');
            const nom = document.getElementById('nom');
            const pVal = prenom ? prenom.value.trim() : '';
            const nVal = nom ? nom.value.trim() : '';
            setError('prenomField', pVal.length < 2);
            setError('nomField', nVal.length < 2);
            if (pVal.length < 2) ok = false;
            if (nVal.length < 2) ok = false;
        }
        if (step === 2) {
            const email = document.getElementById('regEmail');
            const tel = document.getElementById('tel');
            const eVal = email ? email.value.trim() : '';
            const tVal = tel ? tel.value.trim() : '';
            const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(eVal);
            const telOk = /^[+\d][\d\s]{7,}$/.test(tVal);
            setError('regEmailField', !emailOk);
            setError('telField', !telOk);
            if (!emailOk) ok = false;
            if (!telOk) ok = false;
        }
        if (step === 3) {
            const pwd = document.getElementById('regPassword');
            const val = pwd ? pwd.value : '';
            setError('regPasswordField', val.length < 8);
            if (val.length < 8) ok = false;
        }
        return ok;
    }

    function goToStep(n) {
        document.querySelectorAll('.step').forEach(function(s) {
            s.classList.toggle('active', parseInt(s.dataset.step) === n);
        });
        document.querySelectorAll('.neck-bead').forEach(function(b) {
            const idx = parseInt(b.dataset.bead);
            b.classList.toggle('active', idx === n);
            b.classList.toggle('done', idx < n);
        });
        document.querySelectorAll('.neck-thread').forEach(function(t) {
            t.classList.toggle('filled', parseInt(t.dataset.thread) < n);
        });
    }

    window.nextStep = function(current) {
        if (!validateStep(current)) return;
        goToStep(current + 1);
    };

    window.prevStep = function(current) {
        goToStep(current - 1);
    };

    // ============================================================
    // 5. PASSWORD STRENGTH
    // ============================================================
    window.updateStrength = function() {
        const pwd = document.getElementById('regPassword');
        if (!pwd) return;
        const value = pwd.value;
        let score = 0;
        if (value.length >= 8) score++;
        if (/[A-Z]/.test(value) && /[a-z]/.test(value)) score++;
        if (/\d/.test(value)) score++;
        if (/[^A-Za-z0-9]/.test(value)) score++;

        const pearls = document.querySelectorAll('#strengthPearls .pearl');
        pearls.forEach(function(p, i) {
            p.classList.toggle('filled', i < score);
        });

        const labels = ['Trop faible', 'Faible', 'Moyen', 'Fort', 'Excellent'];
        const label = document.getElementById('strengthLabel');
        if (label) {
            label.textContent = value.length === 0 ? 'Robustesse' : labels[score];
        }
    };

    // ============================================================
    // 6. TOAST
    // ============================================================
    window.showToast = function(msg) {
        const toast = document.getElementById('toast');
        const toastMsg = document.getElementById('toastMsg');
        if (!toast || !toastMsg) return;
        toastMsg.textContent = msg;
        toast.classList.add('show');
        setTimeout(function() {
            toast.classList.remove('show');
        }, 3200);
    };

    // ============================================================
    // 7. LOGIN SUBMIT (Spring Boot)
    // ============================================================
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        const email = document.getElementById('loginEmail');
        const pwd = document.getElementById('loginPassword');
        const eVal = email ? email.value.trim() : '';
        const pVal = pwd ? pwd.value : '';
        const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(eVal);

        setError('loginEmailField', !emailOk);
        setError('loginPasswordField', pVal.length === 0);

        if (!emailOk || pVal.length === 0) {
            e.preventDefault();
            return;
        }

        // Le formulaire est soumis normalement vers /login
        // Spring Security gère l'authentification
        console.log('🔐 Tentative de connexion...');
    });

    // ============================================================
    // 8. REGISTER SUBMIT (Spring Boot)
    // ============================================================
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        if (!validateStep(3)) {
            e.preventDefault();
            return;
        }
        // Le formulaire est soumis normalement vers /inscription/etape1
        console.log('📝 Tentative d\'inscription...');
    });

    // ============================================================
    // 9. INITIALISATION
    // ============================================================
    if (document.querySelector('.error-global')) {
        switchTab('login');
    }

    console.log('🔐 IFP Perle d\'Or — Portail connexion/inscription chargé.');

})();