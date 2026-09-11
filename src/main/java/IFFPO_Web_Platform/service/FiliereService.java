package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.FiliereDTO;
import IFFPO_Web_Platform.entity.Filiere;

import java.util.List;

public interface FiliereService {

    FiliereDTO trouverParId(Long id);
    List<FiliereDTO> toutesFilieres();
    FiliereDTO creerFiliere(FiliereDTO filiereDTO);
    FiliereDTO modifierFiliere(Long id, FiliereDTO filiereDTO);
    void changerStatut(Long id);
    void supprimer(Long id);

    List<Filiere> getAllFiliere();
}
