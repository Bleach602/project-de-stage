package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.StatistiqueDTO;
import IFFPO_Web_Platform.dto.StatistiqueGraphiquesDTO;

public interface StatistiqueService {

    StatistiqueDTO calculerStatistiques();
    StatistiqueGraphiquesDTO calculerGraphiques(String periode);

}
