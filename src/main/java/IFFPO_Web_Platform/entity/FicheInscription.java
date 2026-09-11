package IFFPO_Web_Platform.entity;

import IFFPO_Web_Platform.entity.enums.OuiNon;
import IFFPO_Web_Platform.entity.enums.Sexe;
import IFFPO_Web_Platform.entity.enums.StatutResponsable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "fiche_inscription")
public class FicheInscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @Column(nullable = false, length = 70)
    private String lieuNaissance;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    @Column(nullable = false, length = 50)
    private String adresse;

    @Column(nullable = false, length = 30)
    private String diplomePlusEleve;

    @Column(nullable = false)
    private String anneeObtention;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OuiNon handicap;

    @Column(length = 50)
    private String precisionHandicap;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OuiNon maladie;

    @Column(length = 50)
    private String precisionMaladie;

    @Column(nullable = false, length = 20)
    private String nomResponsable;

    @Column(nullable = false, length = 20)
    private String prenomResponsable;

    @Column(nullable = false, length = 20)
    private String telephoneResponsable;

    @Column(nullable = false, length = 50)
    private String adresseResponsable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutResponsable statutResponsable;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Document photoIdentite;

    /**
     * Propriétaire de la fiche.
     */
    @OneToOne
    @JoinColumn(name="utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    /**
     * PDF officiel généré après
     * l'enregistrement de la fiche.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pdf_id")
    private Document fichePdf;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id", nullable = false)
    private Specialite specialite;

}
