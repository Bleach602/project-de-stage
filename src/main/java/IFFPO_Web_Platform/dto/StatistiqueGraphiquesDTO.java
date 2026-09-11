package IFFPO_Web_Platform.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatistiqueGraphiquesDTO {

    private List<PointStatistiqueDTO> evolutionCandidatures;
    private List<PointStatistiqueDTO> repartitionParStatut;
    private List<PointStatistiqueDTO> candidaturesParFiliere;
    private List<PointStatistiqueDTO> evolutionInscriptions;

}
