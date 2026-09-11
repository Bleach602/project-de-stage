package IFFPO_Web_Platform.entity;

import IFFPO_Web_Platform.entity.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument typeDocument;



    /**
     * Nom original du fichier envoyé.
     */
    private String nomFichier;


    /**
     * URL sécurisée fournie par Cloudinary.
     */
    @Column(length = 1000)
    private String url;


    /**
     * Identifiant unique du fichier sur Cloudinary.
     *
     * Permet notamment de supprimer ou remplacer
     * le fichier.
     */
    @Column(length = 500)
    private String publicId;


    /**
     * Type de ressource Cloudinary.
     *
     * Exemple :
     *
     * image
     * raw
     * video
     */
    @Column(length = 50)
    private String resourceType;


    @OneToOne(mappedBy = "photoIdentite")
    private FicheInscription ficheInscription;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidature_id")
    private Candidature candidature;

    //pour  les formateurs
    @OneToOne(mappedBy = "photo")  //  mappedBy pointe vers le champ "photo" dans Formateur
    private Formateur formateur;

    //pour les actu
    @OneToOne(mappedBy = "photo")  // mappedBy pointe vers le champ "photo" dans Actualites
    private Actualites actualites;


    private LocalDateTime dateModification;

    private LocalDateTime dateCreation;


    // =========================
    // CONSTRUCTEUR PAR DEFAUT
    // =========================

    public Document() {
    }


    // =========================
    // CONSTRUCTEUR
    // =========================

    public Document(
            TypeDocument typeDocument,
            String nomFichier,
            String url,
            String publicId,
            String resourceType,
            Candidature candidature
    ) {

        this.typeDocument = typeDocument;

        this.nomFichier = nomFichier;

        this.url = url;

        this.publicId = publicId;

        this.resourceType = resourceType;

        this.candidature = candidature;
    }



    // =========================
    // CREATION
    // =========================

    @PrePersist
    public void prePersist() {

        this.dateCreation = LocalDateTime.now();

        this.dateModification = LocalDateTime.now();
    }


    // =========================
    // MODIFICATION
    // =========================

    @PreUpdate
    public void preUpdate() {

        this.dateModification = LocalDateTime.now();
    }
}