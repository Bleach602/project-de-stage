package IFFPO_Web_Platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SpecialiteDTO {

    private Long id;
    private Long filiereId;

    @NotBlank
    private String nom;

    private String description;

    @NotNull
    private BigDecimal fraisPreInscription;

    @NotNull
    private BigDecimal tranche1;

    @NotNull
    private BigDecimal tranche2;

    @NotNull
    private BigDecimal tranche3;

    //CALCUL DU TOTAL
    public BigDecimal getTotalScolarite() {

        BigDecimal total = BigDecimal.ZERO;
        if (fraisPreInscription != null){
            total = total.add(fraisPreInscription);
        }
        if (tranche1 != null){
            total = total.add(tranche1);
        }
        if (tranche2 != null){
            total = total.add(tranche2);
        }
        if (tranche3 != null){
            total = total.add(tranche3);
        }

        return total;
    }



    //CONSTRUCTORS
    public SpecialiteDTO() {}

    public SpecialiteDTO(Long id, Long filiereId, String nom, String description,
                         BigDecimal fraisPreInscription,
                         BigDecimal tranche1, BigDecimal tranche2, BigDecimal tranche3) {
        super();
        this.id = id;
        this.filiereId = filiereId;
        this.nom = nom;
        this.description = description;
        this.fraisPreInscription = fraisPreInscription;
        this.tranche1 = tranche1;
        this.tranche2 = tranche2;
        this.tranche3 = tranche3;
    }


    //GETTERS & SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Long filiereId) {
        this.filiereId = filiereId;
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

}
