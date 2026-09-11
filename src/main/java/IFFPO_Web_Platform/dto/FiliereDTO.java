package IFFPO_Web_Platform.dto;

import IFFPO_Web_Platform.entity.enums.StatutFiliere;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class FiliereDTO {

    private Long id;

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @NotBlank(message = "Sigle obligatoire")
    @Size(max = 15, message = "Le sigle est trop long (15 caractères maximum)")
    private String sigle;

    @NotNull(message = "Limites de places obligatoires")
    @Min(value = 1, message = "La limite de places doit etre supérieure à zéro.")
    private Integer limitePlaces;

    private StatutFiliere statutFiliere;

    //Liste des specialités pour la vitrine
    private List<SpecialiteDTO> specialites = new ArrayList<>();

    //CONSTRUCTORS
    public FiliereDTO() {}

    public FiliereDTO(Long id, String nom, String sigle,
                      Integer limitePlaces, StatutFiliere statutFiliere,
                      List<SpecialiteDTO> specialites) {
        super();
        this.id = id;
        this.nom = nom;
        this.sigle = sigle;
        this.limitePlaces = limitePlaces;
        this.statutFiliere = statutFiliere;
        this.specialites = specialites;
    }

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

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }

    public Integer getLimitePlaces() {
        return limitePlaces;
    }

    public void setLimitePlaces(Integer limitePlaces) {
        this.limitePlaces = limitePlaces;
    }

    public StatutFiliere getStatutFiliere() {
        return statutFiliere;
    }

    public void setStatutFiliere(StatutFiliere statutFiliere) {
        this.statutFiliere = statutFiliere;
    }

    public List<SpecialiteDTO> getSpecialites() {
        return specialites;
    }

    public void setSpecialites(List<SpecialiteDTO> specialites) {
        this.specialites = specialites;
    }
}
