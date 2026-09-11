package IFFPO_Web_Platform.entity;


import IFFPO_Web_Platform.entity.enums.StatutFiliere;
import IFFPO_Web_Platform.entity.enums.StatutSession;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "session")
public class SessionCandidature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String anneeAcademique;

    @Column(nullable = false)
    private LocalDate dateOuverture;

    @Column(nullable = false)
    private LocalDate dateFermeture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSession statutSession;

    @OneToMany(mappedBy = "sessionCandidature", cascade = CascadeType.ALL,
    orphanRemoval = false)
    private List<Candidature> candidatures = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "session_filiere",
    joinColumns = @JoinColumn(name = "session_id"),
    inverseJoinColumns = @JoinColumn(name = "filiere_id"))
    private Set<Filiere> filieres = new HashSet<>();

    //METHODES UTILITAIRES...
    public void addCandidature(Candidature candidature){
        candidatures.add(candidature);
        candidature.setSessionCandidature(this);
    }

    public void removeCandidature(Candidature candidature){
        candidatures.remove(candidature);
        candidature.setSessionCandidature(null);
    }

    //FILIERE FERMEE
    public void addFiliere(Filiere filiere){
        if (filiere.getStatutFiliere() == StatutFiliere.FERMEE){
            throw new IllegalArgumentException("Impossible " +
                    "d'ajouter une filière fermée à une session");
        }
        this.filieres.add(filiere);
    }

    //GETTERS & SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnneeAcademique() {
        return anneeAcademique;
    }

    public void setAnneeAcademique(String anneeAcademique) {
        this.anneeAcademique = anneeAcademique;
    }

    public LocalDate getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDate dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public LocalDate getDateFermeture() {
        return dateFermeture;
    }

    public void setDateFermeture(LocalDate dateFermeture) {
        this.dateFermeture = dateFermeture;
    }

    public StatutSession getStatutSession() {
        return statutSession;
    }

    public void setStatutSession(StatutSession statutSession) {
        this.statutSession = statutSession;
    }

    public List<Candidature> getCandidatures() {
        return candidatures;
    }

    public void setCandidatures(List<Candidature> candidatures) {
        this.candidatures = candidatures;
    }

    public Set<Filiere> getFilieres() {
        return filieres;
    }

    public void setFilieres(Set<Filiere> filieres) {
        this.filieres = filieres;
    }
}
