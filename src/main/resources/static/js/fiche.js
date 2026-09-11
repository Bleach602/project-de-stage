
(function () {
    "use strict";

    /* ============================================================
       ÉLÉMENTS
       ============================================================ */

    const step1 = document.getElementById("ficheStep1");
    const step2 = document.getElementById("ficheStep2");
    const step3 = document.getElementById("ficheStep3");

    const stepIndicators =
        document.querySelectorAll("#ficheStepIndicator .fiche-step");

    const nextStep2 = document.getElementById("ficheNextStep2");
    const backStep1 = document.getElementById("ficheBackStep1");
    const nextStep3 = document.getElementById("ficheNextStep3");
    const backStep2 = document.getElementById("ficheBackStep2");

    const filiereCards =
        document.querySelectorAll(".filiere-card");

    const selectedFiliereDisplay =
        document.getElementById("selectedFiliereDisplay");

    const selectedSpecialiteId =
        document.getElementById("selectedSpecialiteId");

    const specialiteModal =
        document.getElementById("specialiteModal");

    const closeSpecialiteModal =
        document.getElementById("closeSpecialiteModal");

    const cancelSpecialiteModal =
        document.getElementById("cancelSpecialiteModal");

    const validateSpecialiteModal =
        document.getElementById("validateSpecialiteModal");

    const modalFiliereName =
        document.getElementById("modalFiliereName");

    const specialiteList =
        document.getElementById("specialiteList");

    const maladies =
        document.getElementById("maladies");

    const precisionMaladie =
        document.getElementById("Precision");

    const handicap =
        document.getElementById("handicaps");

    const precisionHandicap =
        document.getElementById("motifHandicap");


    let selectedFiliereId = null;
    let selectedSpecialite = null;
    let currentFiliereId = null;


    /* ============================================================
       NAVIGATION ÉTAPES
       ============================================================ */

    function goToFicheStep(step) {

        [step1, step2, step3].forEach(function (section) {
            section.classList.remove("active-section");
        });

        stepIndicators.forEach(function (indicator) {
            indicator.classList.remove("active");
            indicator.classList.remove("done");
        });


        if (step === 1) {

            step1.classList.add("active-section");
            stepIndicators[0].classList.add("active");

        } else if (step === 2) {

            step2.classList.add("active-section");

            stepIndicators[0].classList.add("done");
            stepIndicators[1].classList.add("active");

        } else if (step === 3) {

            step3.classList.add("active-section");

            stepIndicators[0].classList.add("done");
            stepIndicators[1].classList.add("done");
            stepIndicators[2].classList.add("active");
        }
    }


    /* ============================================================
       VALIDATION ÉTAPE 1
       ============================================================ */

    function updateNextButton() {

        nextStep2.disabled =
            !(selectedFiliereId && selectedSpecialite);

    }


    nextStep2.addEventListener("click", function () {

        if (!selectedFiliereId || !selectedSpecialite) {

            alert(
                "Veuillez sélectionner une filière et une spécialité."
            );

            return;
        }

        goToFicheStep(2);
    });


    backStep1.addEventListener("click", function () {
        goToFicheStep(1);
    });


    nextStep3.addEventListener("click", function () {

        if (!step2.querySelector("input, select").checkValidity()) {

            step2.querySelector("input, select").reportValidity();
            return;
        }

        goToFicheStep(3);
    });


    backStep2.addEventListener("click", function () {
        goToFicheStep(2);
    });


    /* ============================================================
       STEPPER
       ============================================================ */

    stepIndicators.forEach(function (indicator) {

        indicator.addEventListener("click", function () {

            const stepNum =
                parseInt(this.dataset.step, 10);

            /*
             * On ne permet pas d'aller arbitrairement à une étape
             * sans avoir réalisé la sélection initiale.
             */

            if (stepNum === 1) {

                goToFicheStep(1);

            } else if (stepNum === 2) {

                if (!selectedFiliereId || !selectedSpecialite) {
                    return;
                }

                goToFicheStep(2);

            } else if (stepNum === 3) {

                if (!selectedFiliereId || !selectedSpecialite) {
                    return;
                }

                goToFicheStep(3);
            }

        });

    });


    /* ============================================================
       SÉLECTION FILIÈRE
       ============================================================ */

    filiereCards.forEach(function (card) {

        card.addEventListener("click", function () {

            filiereCards.forEach(function (item) {
                item.classList.remove("selected");
            });

            this.classList.add("selected");

            selectedFiliereId =
                this.dataset.filiereId;

            selectedSpecialite = null;
            selectedSpecialiteId.value = "";

            selectedFiliereDisplay.textContent =
                "Filière sélectionnée — choisissez maintenant une spécialité.";

            updateNextButton();
        });

    });


    /* ============================================================
       OUVERTURE MODALE
       ============================================================ */

    document.addEventListener("click", function (event) {

        const target = event.target.closest(".voir-specialites");

        if (!target) {
            return;
        }

        event.stopPropagation();

        const filiereId =
            target.dataset.filiereId;

        const filiereNom =
            target.dataset.filiereNom;

        openSpecialiteModal(filiereId, filiereNom);
    });


    function openSpecialiteModal(filiereId, filiereNom) {

        currentFiliereId = filiereId;

        modalFiliereName.textContent =
            filiereNom;


        const items =
            specialiteList.querySelectorAll(".specialite-item");


        let visibleCount = 0;

        items.forEach(function (item) {

            const belongsToFiliere =
                item.dataset.filiere === currentFiliereId;

            item.style.display =
                belongsToFiliere ? "block" : "none";

            if (belongsToFiliere) {
                visibleCount++;
            }

        });


        if (visibleCount === 0) {

            specialiteList.innerHTML = "";

            const emptyMessage =
                document.createElement("div");

            emptyMessage.className =
                "specialite-empty";

            emptyMessage.textContent =
                "Aucune spécialité disponible pour cette filière.";

            specialiteList.appendChild(emptyMessage);
        }

        specialiteModal.classList.add("active");
        specialiteModal.setAttribute("aria-hidden", "false");

        document.body.style.overflow = "hidden";
    }


    /* ============================================================
       FERMETURE MODALE
       ============================================================ */

    function closeSpecialiteModalFn() {

        specialiteModal.classList.remove("active");

        specialiteModal.setAttribute(
            "aria-hidden",
            "true"
        );

        document.body.style.overflow = "";
    }


    closeSpecialiteModal.addEventListener(
        "click",
        closeSpecialiteModalFn
    );

    cancelSpecialiteModal.addEventListener(
        "click",
        closeSpecialiteModalFn
    );


    specialiteModal.addEventListener("click", function (event) {

        if (event.target === specialiteModal) {
            closeSpecialiteModalFn();
        }

    });


    /* ============================================================
       VALIDATION SPÉCIALITÉ
       ============================================================ */

    validateSpecialiteModal.addEventListener(
        "click",
        function () {

            const selected =
                specialiteList.querySelector(
                    'input[name="specialiteChoisie"]:checked'
                );


            if (!selected) {

                alert(
                    "Veuillez sélectionner une spécialité."
                );

                return;
            }


            const item =
                selected.closest(".specialite-item");


            /*
             * Vérification côté interface :
             * la spécialité sélectionnée doit appartenir
             * à la filière actuellement ouverte.
             */

            if (
                !item ||
                item.dataset.filiere !== currentFiliereId
            ) {

                alert(
                    "La spécialité sélectionnée ne correspond pas à la filière."
                );

                selected.checked = false;

                return;
            }


            const label =
                document.querySelector(
                    'label[for="' + selected.id + '"]'
                );


            const specialiteNom =
                label
                    ? label.textContent.trim()
                    : "Spécialité sélectionnée";


            selectedSpecialite =
                selected.value;

            selectedSpecialiteId.value =
                selected.value;


            selectedFiliereDisplay.textContent =
                "Filière : " +
                modalFiliereName.textContent.trim() +
                " · Spécialité : " +
                specialiteNom;


            updateNextButton();

            closeSpecialiteModalFn();
        }
    );


    /* ============================================================
       HANDICAP
       ============================================================ */

    function toggleHandicap() {

        const disabled =
            handicap.value === "NON";

        precisionHandicap.disabled =
            disabled;

        if (disabled) {
            precisionHandicap.value = "";
        }
    }


    handicap.addEventListener(
        "change",
        toggleHandicap
    );


    /* ============================================================
       MALADIE
       ============================================================ */

    function toggleMaladie() {

        const disabled =
            maladies.value === "NON";

        precisionMaladie.disabled =
            disabled;

        if (disabled) {
            precisionMaladie.value = "";
        }
    }


    maladies.addEventListener(
        "change",
        toggleMaladie
    );


    /* ============================================================
       ESCAPE
       ============================================================ */

    document.addEventListener("keydown", function (event) {

        if (
            event.key === "Escape" &&
            specialiteModal.classList.contains("active")
        ) {
            closeSpecialiteModalFn();
        }

    });


    /* ============================================================
       INITIALISATION
       ============================================================ */

    goToFicheStep(1);

    updateNextButton();

    toggleMaladie();

    toggleHandicap();


})();

