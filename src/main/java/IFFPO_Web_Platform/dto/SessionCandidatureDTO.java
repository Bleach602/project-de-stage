package IFFPO_Web_Platform.dto;

import IFFPO_Web_Platform.entity.enums.StatutSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public class SessionCandidatureDTO {

    private Long id;

    @NotBlank(message = "Veuillez renseigner l'année académique de cette session")
    private String anneeAcademique;

    @NotNull(message = "Date d'ouverture obligatoire")
    private LocalDate dateOuverture;

    @NotNull(message = "Date d'ouverture obligatoire")
    private LocalDate dateFermeture;

    private StatutSession statutSession;

    private Set<Long> filiereIds;

    //CONSTRUCTORS
    public SessionCandidatureDTO() {}

    public SessionCandidatureDTO(Long id, String anneeAcademique, LocalDate dateOuverture,
                                 LocalDate dateFermeture, StatutSession statutSession, Set<Long> filiereIds) {
        super();
        this.id = id;
        this.anneeAcademique = anneeAcademique;
        this.dateOuverture = dateOuverture;
        this.dateFermeture = dateFermeture;
        this.statutSession = statutSession;
        this.filiereIds = filiereIds;
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

    public Set<Long> getFiliereIds() {
        return filiereIds;
    }

    public void setFiliereIds(Set<Long> filiereIds) {
        this.filiereIds = filiereIds;
    }
}
