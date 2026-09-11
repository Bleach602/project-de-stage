(function () {

    "use strict";


    /* =========================================================
       1. ÉLÉMENTS PRINCIPAUX
       ========================================================= */

    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById("sidebarOverlay");
    const mobileBtn = document.getElementById("mobileMenuBtn");
    const sidebarClose = document.getElementById("sidebarClose");


    /* =========================================================
       2. MENU MOBILE
       ========================================================= */

    function openSidebar() {

        if (!sidebar || !overlay) {
            return;
        }

        sidebar.classList.add("open");
        overlay.classList.add("active");

        document.body.style.overflow = "hidden";
    }


    function closeSidebar() {

        if (!sidebar || !overlay) {
            return;
        }

        sidebar.classList.remove("open");
        overlay.classList.remove("active");

        document.body.style.overflow = "";
    }


    /* Toggle */

    if (mobileBtn) {

        mobileBtn.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            if (sidebar && sidebar.classList.contains("open")) {

                closeSidebar();

            } else {

                openSidebar();

            }

        });

    }


    /* Bouton X */

    if (sidebarClose) {

        sidebarClose.addEventListener("click", function (event) {

            event.preventDefault();

            closeSidebar();

        });

    }


    /* Clic sur overlay */

    if (overlay) {

        overlay.addEventListener("click", function () {

            closeSidebar();

        });

    }


    /* =========================================================
       3. FERMETURE AVEC ESCAPE
       ========================================================= */

    document.addEventListener("keydown", function (event) {

        if (event.key !== "Escape") {
            return;
        }


        /* Sidebar */

        if (
            sidebar &&
            sidebar.classList.contains("open")
        ) {

            closeSidebar();

        }


        /* Modal profil */

        if (
            editProfilModal &&
            editProfilModal.classList.contains("active")
        ) {

            closeEditProfil();

        }


        /* Modal contact */

        if (
            contactModal &&
            contactModal.classList.contains("active")
        ) {

            closeContact();

        }


        /* Popup document */

        if (
            documentPopup &&
            documentPopup.style.display === "flex"
        ) {

            closeDocumentPopup();

        }

    });


    /* =========================================================
       4. REDIMENSIONNEMENT
       ========================================================= */

    window.addEventListener("resize", function () {

        /*
         * Lorsque l'écran repasse en desktop,
         * on ferme le drawer mobile.
         */

        if (
            window.innerWidth > 768 &&
            sidebar &&
            sidebar.classList.contains("open")
        ) {

            closeSidebar();

        }

    });


    /* =========================================================
       5. NAVIGATION PAR ONGLETS
       ========================================================= */

    const navLinks =
        document.querySelectorAll(
            '.sidebar nav ul li a[data-tab]'
        );


    const tabs = {

        profil:
            document.getElementById("profilTab"),

        fiche:
            document.getElementById("ficheTab"),

        candidater:
            document.getElementById("candidaterTab"),

        paiement:
            document.getElementById("paiementTab"),

        documents:
            document.getElementById("documentsTab"),

        notifications:
            document.getElementById("notificationsTab"),

        contact:
            document.getElementById("contactTab")

    };


    /* =========================================================
       6. TITRES
       ========================================================= */

    const pageTitles = {

        profil:
            "Mon profil",

        fiche:
            "Ma fiche d'inscription",

        candidater:
            "Candidater",

        paiement:
            "Paiement",

        documents:
            "Mes documents",

        notifications:
            "Notifications",

        contact:
            "Contacter l'administration"

    };


    /* =========================================================
       7. CHANGEMENT D'ONGLET
       ========================================================= */

    function switchTab(tabId) {


        /* -----------------------------------------
           Vérification
           ----------------------------------------- */

        if (!tabId) {
            return;
        }


        /* -----------------------------------------
           Cacher tous les onglets
           ----------------------------------------- */

        Object.keys(tabs).forEach(function (key) {

            const tab = tabs[key];

            if (tab) {

                tab.classList.remove("active-tab");

            }

        });


        /* -----------------------------------------
           Afficher l'onglet sélectionné
           ----------------------------------------- */

        if (tabs[tabId]) {

            tabs[tabId].classList.add("active-tab");

        }


        /* -----------------------------------------
           Mettre à jour le lien actif
           ----------------------------------------- */

        navLinks.forEach(function (link) {

            link.classList.remove("active");

            if (link.dataset.tab === tabId) {

                link.classList.add("active");

            }

        });


        /* -----------------------------------------
           Mettre à jour le titre
           ----------------------------------------- */

        const titleEl =
            document.getElementById("pageTitle");

        if (
            titleEl &&
            pageTitles[tabId]
        ) {

            titleEl.textContent =
                pageTitles[tabId];

        }


        /* -----------------------------------------
           Fermer le menu sur mobile
           ----------------------------------------- */

        if (window.innerWidth <= 768) {

            closeSidebar();

        }

    }


    /* =========================================================
       8. CLICS SUR LES LIENS
       ========================================================= */

    navLinks.forEach(function (link) {

        link.addEventListener("click", function (event) {

            event.preventDefault();

            const tab =
                this.dataset.tab;

            if (tab) {

                switchTab(tab);

            }

        });

    });


    /* =========================================================
       9. STEPPER CANDIDATURE
       ========================================================= */

    const step1 =
        document.getElementById("candStep1");

    const step2 =
        document.getElementById("candStep2");

    const step3 =
        document.getElementById("candStep3");

    const stepIndicators =
        document.querySelectorAll(".step");

    const toStep2 =
        document.getElementById("candToStep2");

    const backStep1 =
        document.getElementById("candBackStep1");

    const toStep3 =
        document.getElementById("candToStep3");

    const backStep2 =
        document.getElementById("candBackStep2");


    /* ---------------------------------------------------------
       Fonction générique pour le stepper
       --------------------------------------------------------- */

    function showStep(stepNumber) {

        const steps = [
            step1,
            step2,
            step3
        ];


        steps.forEach(function (step) {

            if (step) {

                step.classList.remove("active");

            }

        });


        const selectedStep =
            steps[stepNumber - 1];

        if (selectedStep) {

            selectedStep.classList.add("active");

        }


        /* Indicateurs */

        stepIndicators.forEach(function (indicator, index) {

            indicator.classList.toggle(
                "active",
                index === stepNumber - 1
            );

        });

    }


    /* Étape 1 → 2 */

    if (toStep2) {

        toStep2.addEventListener("click", function () {

            showStep(2);

        });

    }


    /* Étape 2 → 1 */

    if (backStep1) {

        backStep1.addEventListener("click", function () {

            showStep(1);

        });

    }


    /* Étape 2 → 3 */

    if (toStep3) {

        toStep3.addEventListener("click", function () {

            showStep(3);

        });

    }


    /* Étape 3 → 2 */

    if (backStep2) {

        backStep2.addEventListener("click", function () {

            showStep(2);

        });

    }


    /* =========================================================
       10. MODALE PROFIL
       ========================================================= */

    const editProfilModal =
        document.getElementById("editProfilModal");

    const openEditProfilBtn =
        document.getElementById("openEditProfilModal");

    const closeEditProfilBtn =
        document.getElementById("closeEditProfilModal");

    const cancelEditProfilBtn =
        document.getElementById("cancelEditProfil");


    /* Remplissage */

    function fillEditModal() {

        const editNom =
            document.getElementById("editNom");

        const editPrenom =
            document.getElementById("editPrenom");

        const editTelephone =
            document.getElementById("editTelephone");

        const editEmail =
            document.getElementById("editEmail");


        const displayNom =
            document.getElementById("displayNom");

        const displayPrenom =
            document.getElementById("displayPrenom");

        const displayTelephone =
            document.getElementById("displayTelephone");

        const displayEmail =
            document.getElementById("displayEmail");


        if (editNom && displayNom) {

            editNom.value =
                displayNom.textContent.trim();

        }


        if (editPrenom && displayPrenom) {

            editPrenom.value =
                displayPrenom.textContent.trim();

        }


        if (editTelephone && displayTelephone) {

            editTelephone.value =
                displayTelephone.textContent.trim();

        }


        if (editEmail && displayEmail) {

            editEmail.value =
                displayEmail.textContent.trim();

        }

    }


    /* Ouvrir */

    function openEditProfil() {

        if (!editProfilModal) {
            return;
        }

        fillEditModal();

        editProfilModal.classList.add("active");

        document.body.style.overflow = "hidden";

    }


    /* Fermer */

    function closeEditProfil() {

        if (!editProfilModal) {
            return;
        }

        editProfilModal.classList.remove("active");

        document.body.style.overflow = "";

    }


    if (openEditProfilBtn) {

        openEditProfilBtn.addEventListener(
            "click",
            openEditProfil
        );

    }


    if (closeEditProfilBtn) {

        closeEditProfilBtn.addEventListener(
            "click",
            closeEditProfil
        );

    }


    if (cancelEditProfilBtn) {

        cancelEditProfilBtn.addEventListener(
            "click",
            closeEditProfil
        );

    }


    /* Clic extérieur */

    if (editProfilModal) {

        editProfilModal.addEventListener(
            "click",
            function (event) {

                if (event.target === this) {

                    closeEditProfil();

                }

            }
        );

    }


    /* =========================================================
       11. MODALE CONTACT ADMIN
       ========================================================= */

    const contactModal =
        document.getElementById("contactModal");

    const openContactBtn =
        document.getElementById("openContactModal");

    const closeContactBtn =
        document.getElementById("closeContactModal");

    const cancelContactBtn =
        document.getElementById("cancelContactModal");


    /* Ouvrir */

    function openContact() {

        if (!contactModal) {
            return;
        }

        contactModal.classList.add("active");

        document.body.style.overflow = "hidden";

    }


    /* Fermer */

    function closeContact() {

        if (!contactModal) {
            return;
        }

        contactModal.classList.remove("active");

        document.body.style.overflow = "";

    }


    if (openContactBtn) {

        openContactBtn.addEventListener(
            "click",
            openContact
        );

    }


    if (closeContactBtn) {

        closeContactBtn.addEventListener(
            "click",
            closeContact
        );

    }


    if (cancelContactBtn) {

        cancelContactBtn.addEventListener(
            "click",
            closeContact
        );

    }


    /* Clic extérieur */

    if (contactModal) {

        contactModal.addEventListener(
            "click",
            function (event) {

                if (event.target === this) {

                    closeContact();

                }

            }
        );

    }


    /* =========================================================
       12. ACTIONS DOCUMENTS / PDF
       ========================================================= */

    const downloadFicheBtn =
        document.getElementById("downloadFicheBtn");

    const downloadReceiptBtn =
        document.getElementById("downloadReceiptBtn");

    const addDocumentBtn =
        document.getElementById("addDocumentBtn");


    if (downloadFicheBtn) {

        downloadFicheBtn.addEventListener(
            "click",
            function () {

                alert(
                    "📄 Téléchargement de votre fiche d'inscription (PDF) en cours..."
                );

            }
        );

    }


    if (downloadReceiptBtn) {

        downloadReceiptBtn.addEventListener(
            "click",
            function () {

                alert(
                    "🧾 Téléchargement du reçu de paiement en cours..."
                );

            }
        );

    }



        const fileInputs = [
    {
        input: "candCni",
        list: "cniFileList"
    },
    {
        input: "candDiplome",
        list: "diplomeFileList"
    },
    {
        input: "candActe",
        list: "acteFileList"
    }
        ];

        fileInputs.forEach(item => {

        const input = document.getElementById(item.input);
        const fileList = document.getElementById(item.list);

        if (!input || !fileList) return;

        input.addEventListener("change", function () {

        if (this.files.length > 0) {

        const file = this.files[0];

        fileList.innerHTML = `
                    <span class="file-tag file-selected">
                        <i class="fas fa-check-circle"></i>
                        ${file.name}
                    </span>
                `;

    } else {

        fileList.innerHTML = `
                    JPEG, JPG, PNG (max 25 Mo)
                `;
    }
    });
    });



    /* Boutons modifier documents */

    document
        .querySelectorAll(".edit-doc-btn")
        .forEach(function (btn) {

            btn.addEventListener(
                "click",
                function () {

                    alert(
                        "✏️ Modifier les documents de cette ligne (à implémenter)"
                    );

                }
            );

        });


    /* =========================================================
       13. POPUP DOCUMENT
       ========================================================= */

    const documentPopup =
        document.getElementById("documentPopup");

    const closeDocumentPopupBtn =
        document.getElementById("closeDocumentPopup");

    const documentId =
        document.getElementById("documentId");


    /* Boutons Modifier */

    document
        .querySelectorAll(".btnModifier")
        .forEach(function (btn) {

            btn.addEventListener(
                "click",
                function () {

                    if (documentId) {

                        documentId.value =
                            this.dataset.id || "";

                    }


                    if (documentPopup) {

                        documentPopup.style.display =
                            "flex";

                    }

                }
            );

        });


    /* Fermer popup */

    function closeDocumentPopup() {

        if (documentPopup) {

            documentPopup.style.display =
                "none";

        }

    }


    if (closeDocumentPopupBtn) {

        closeDocumentPopupBtn.addEventListener(
            "click",
            closeDocumentPopup
        );

    }


    /* Clic extérieur */

    if (documentPopup) {

        documentPopup.addEventListener(
            "click",
            function (event) {

                if (event.target === documentPopup) {

                    closeDocumentPopup();

                }

            }
        );

    }


    /* =========================================================
       14. DÉCONNEXION
       ========================================================= */

    // const logoutBtn =
    //     document.getElementById("logoutBtn");
    //
    //
    // if (logoutBtn) {
    //
    //     logoutBtn.addEventListener(
    //         "click",
    //         function (event) {
    //
    //             event.preventDefault();
    //
    //
    //             const confirmation =
    //                 confirm(
    //                     "Voulez-vous vraiment vous déconnecter ?"
    //                 );
    //
    //
    //             if (confirmation) {
    //
    //                 window.location.href =
    //                     "/logout";
    //
    //             }
    //
    //         }
    //     );
    //
    // }


    /* =========================================================
       15. NOTIFICATIONS
       ========================================================= */

    document
        .querySelectorAll(".notification")
        .forEach(function (notification) {


            const closeBtn =
                notification.querySelector(
                    ".notification-close"
                );


            /* Fermeture manuelle */

            if (closeBtn) {

                closeBtn.addEventListener(
                    "click",
                    function () {

                        notification.remove();

                    }
                );

            }


            /* Disparition automatique */

            setTimeout(function () {

                if (!notification.isConnected) {
                    return;
                }


                notification.style.opacity = "0";

                notification.style.transform =
                    "translateX(100px)";

                notification.style.transition =
                    "opacity .45s ease, transform .45s ease";


                setTimeout(function () {

                    if (notification.isConnected) {

                        notification.remove();

                    }

                }, 450);

            }, 5000);

        });


    /* =========================================================
       16. INITIALISATION
       ========================================================= */

    /*
     * On active le profil uniquement si le conteneur existe.
     */

    if (tabs.profil) {

        switchTab("profil");

    }


    /* =========================================================
       17. LOG
       ========================================================= */

    console.log(
        "🚀 Dashboard IFP-PO chargé avec succès !"
    );

})();