package IFFPO_Web_Platform.entity;

import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "candidature")
public class Candidature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reference;

    @Column(length = 1000)
    private String messageCorrection;

    private LocalDate dateCandidature = LocalDate.now();
    private LocalDate dateValidation ;

    @Column(columnDefinition = "TEXT")
    private String motifRejet;

    @Enumerated(EnumType.STRING)
    private StatutCandidature statutCandidature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id", nullable = false)
    private Specialite specialite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_candidature_id", nullable = false)
    private SessionCandidature sessionCandidature;

    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL,
    orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @Column(nullable = false)
    private int nombreTentativesCorrection = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiche_inscription_id", nullable = false)
    private FicheInscription ficheInscription;

    //METHODE UTILITAIRE POUR AJOUTER DES DOCUMENTS
    /**
     * Ajoute un document à la candidature.
     *
     * Le fichier est déjà envoyé vers Cloudinary.
     */
    public void addDocument(
            TypeDocument typeDocument,
            String nomFichier,
            String url,
            String publicId,
            String resourceType
    ) {

        Document document =
                new Document(
                        typeDocument,
                        nomFichier,
                        url,
                        publicId,
                        resourceType,
                        this
                );


        this.documents.add(document);
    }

    //     Associe un document existant à la candidature.

    public void addDocument(Document document) {

        document.setCandidature(this);

        this.documents.add(document);
    }




    @ElementCollection(targetClass = TypeDocument.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "candidature_documents_a_corriger",
            joinColumns = @JoinColumn(name = "candidature_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "type_document")
    private Set<TypeDocument> documentsACorriger = new HashSet<>();

    // Méthodes utilitaires
    public boolean peutEncoreCorriger() {
        return this.nombreTentativesCorrection < 2;
    }

    public boolean estEnCorrection() {
        return this.statutCandidature == StatutCandidature.EN_COURS_DE_CORRECTION;
    }

    public boolean estModifiable() {
        return this.statutCandidature == StatutCandidature.EN_ATTENTE
                || this.statutCandidature == StatutCandidature.EN_COURS_DE_CORRECTION;
    }

}
