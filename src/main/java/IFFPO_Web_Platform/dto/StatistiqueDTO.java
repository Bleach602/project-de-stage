package IFFPO_Web_Platform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatistiqueDTO {

    private long totalCandidatures;
    private long candidaturesEnAttente;
    private long candidaturesValidees;
    private long candidaturesRejetees;
    private long candidaturesEnCorrection;

    private long totalCandidats;

    private long totalFilieres;
    private long totalSpecialites;

    private long filieresOuvertes;
    private long filieresFermees;

    private double tauxValidation;
    private double tauxRejet;
    private double tauxAttente;

}
