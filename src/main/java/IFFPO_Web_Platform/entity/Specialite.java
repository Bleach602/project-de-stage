package IFFPO_Web_Platform.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "specialite")
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal fraisPreInscription;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tranche1;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tranche2;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tranche3;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @OneToMany(mappedBy = "specialite")
    private List<Candidature> candidatures = new ArrayList<>();

    //GETTERS & SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getFraisPreInscription() {
        return fraisPreInscription;
    }

    public void setFraisPreInscription(BigDecimal fraisPreInscription) {
        this.fraisPreInscription = fraisPreInscription;
    }

    public BigDecimal getTranche1() {
        return tranche1;
    }

    public void setTranche1(BigDecimal tranche1) {
        this.tranche1 = tranche1;
    }

    public BigDecimal getTranche2() {
        return tranche2;
    }

    public void setTranche2(BigDecimal tranche2) {
        this.tranche2 = tranche2;
    }

    public BigDecimal getTranche3() {
        return tranche3;
    }

    public void setTranche3(BigDecimal tranche3) {
        this.tranche3 = tranche3;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public List<Candidature> getCandidatures() {
        return candidatures;
    }

    public void setCandidatures(List<Candidature> candidatures) {
        this.candidatures = candidatures;
    }


}
