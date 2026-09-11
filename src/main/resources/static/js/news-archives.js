document.addEventListener("DOMContentLoaded", function () {



    /* ================================================
       FERMETURE DES ALERTES AVEC LIMITE DE TEMPS
    ================================================= */
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(alertElement => {
        // Fonction réutilisable pour masquer et supprimer l'alerte
        const dismissAlert = () => {
            if (alertElement && alertElement.parentNode) {
                alertElement.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
                alertElement.style.opacity = '0';
                alertElement.style.transform = 'translateY(-5px)';

                setTimeout(() => {
                    alertElement.remove();
                }, 300);
            }
        };

        // 1. Fermeture automatique après 5 secondes (5000 ms)
        const autoDismissTimer = setTimeout(() => {
            dismissAlert();
        }, 5000);

        // 2. Fermeture manuelle via la croix
        const closeButtonMessages = alertElement.querySelector('.alert-close');
        if (closeButtonMessages) {
            closeButtonMessages.addEventListener('click', () => {
                clearTimeout(autoDismissTimer); // Annule le minuteur automatique si l'utilisateur clique
                dismissAlert();
            });
        }
    });

    /* =========================================
       AFFICHAGE DU NOM DE L'IMAGE SÉLECTIONNÉE
    ========================================== */
    const fileInputs = document.querySelectorAll('input[type="file"]');

    fileInputs.forEach(function (input) {
        input.addEventListener("change", function (event) {
            const file = event.target.files[0];
            const uploadContainer = input.closest(".file-upload") || input.parentElement;
            const labelText = uploadContainer.querySelector(".file-name-text") || uploadContainer.querySelector("span");

            if (file && labelText) {
                labelText.textContent = file.name;
                labelText.style.fontWeight = "600";
            }
        });
    });

    function resetFileInputs() {
        fileInputs.forEach(function (input) {
            input.value = "";
            const uploadContainer = input.closest(".file-upload") || input.parentElement;
            const labelText = uploadContainer.querySelector(".file-name-text") || uploadContainer.querySelector("span");

            if (labelText) {
                if (input.id === "editImage") {
                    labelText.textContent = "Remplacer l'image";
                } else {
                    labelText.textContent = "Choisir une image";
                }
                labelText.style.fontWeight = "normal";
            }
        });
    }

    /* =========================================
       UTILITAIRES MODALS
    ========================================== */

    function openModal(modal) {
        if (modal) {
            modal.classList.add("active");
            document.body.style.overflow = "hidden";
        }
    }


    function closeModal(modal) {
        if (modal) {
            modal.classList.remove("active");
            document.body.style.overflow = "";
        }
    }


    /* =========================================
       MODAL CRÉATION
    ========================================== */

    const createModal =
        document.getElementById("createModal");

    const openCreateButton =
        document.getElementById("openCreateModal");

    const openCreateButtonEmpty =
        document.getElementById("openCreateModalEmpty");


    if (openCreateButton) {

        openCreateButton.addEventListener("click", function () {
            resetFileInputs();
            openModal(createModal);
        });

    }


    if (openCreateButtonEmpty) {

        openCreateButtonEmpty.addEventListener("click", function () {

            openModal(createModal);

        });

    }


    /* =========================================
       MODAL MODIFICATION
    ========================================== */

    const editModal =
        document.getElementById("editModal");

    const editForm =
        document.getElementById("editForm");

    const editTitre =
        document.getElementById("editTitre");

    const editDescription =
        document.getElementById("editDescription");

    const editDateEvenement =
        document.getElementById("editDateEvenement");

    const editImage =
        document.getElementById("editImage");


    const editButtons =
        document.querySelectorAll(".btn-edit");


    editButtons.forEach(function (button) {

        button.addEventListener("click", function () {

            const card =
                button.closest(".news-card");

            if (!card) {
                return;
            }


            const id =
                card.dataset.id;

            const titre =
                card.dataset.titre;

            const description =
                card.dataset.description;

            const dateEvenement =
                card.dataset.dateEvenement;


            /*
             * Remplissage du formulaire
             */
            editTitre.value =
                titre || "";

            editDescription.value =
                description || "";


            /*
             * Conversion de la date
             *
             * LocalDateTime venant de Thymeleaf
             * peut arriver sous la forme :
             *
             * 2026-08-20T14:30:00
             *
             * datetime-local attend :
             *
             * 2026-08-20T14:30
             */
            if (dateEvenement) {

                editDateEvenement.value =
                    dateEvenement.substring(0, 16);

            } else {

                editDateEvenement.value = "";

            }


            /*
                Construction dynamique de l'action du formulaire
            */
            editForm.action =
                "/dashboard/actualites/modifier/" + id;


            /* Réinitialisation de l'image */
            resetFileInputs();

            openModal(editModal);
        });
    });


    /* =========================================
       MODAL SUPPRESSION
    ========================================== */

    const deleteModal =
        document.getElementById("deleteModal");

    const deleteForm =
        document.getElementById("deleteForm");

    const deleteNewsTitle =
        document.getElementById("deleteNewsTitle");


    const deleteButtons =
        document.querySelectorAll(".btn-delete");


    deleteButtons.forEach(function (button) {

        button.addEventListener("click", function () {

            const id =
                button.dataset.id;

            const titre =
                button.dataset.titre;


            deleteNewsTitle.textContent =
                titre || "Cette actualité";


            deleteForm.action =
                "/dashboard/actualites/" +
                id +
                "/supprimer";


            openModal(deleteModal);

        });

    });


    /* =========================================
       FERMETURE DES MODALS
    ========================================== */

    const closeButtons =
        document.querySelectorAll(
            "[data-close-modal]"
        );


    closeButtons.forEach(function (button) {

        button.addEventListener("click", function () {

            const modalId =
                button.dataset.closeModal;

            const modal =
                document.getElementById(modalId);

            closeModal(modal);

        });

    });


    /* =========================================
       FERMETURE EN CLIQUANT SUR L'OVERLAY
    ========================================== */

    document.querySelectorAll(".modal-overlay")
        .forEach(function (overlay) {

            overlay.addEventListener("click", function (event) {

                if (event.target === overlay) {

                    closeModal(overlay);

                }

            });

        });


    /* =========================================
       FERMETURE AVEC ESC
    ========================================== */

    document.addEventListener("keydown", function (event) {

        if (event.key === "Escape") {

            document.querySelectorAll(".modal-overlay.active")
                .forEach(function (modal) {

                    closeModal(modal);

                });

        }

    });

});

