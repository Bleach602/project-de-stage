package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.SpecialiteDTO;
import IFFPO_Web_Platform.entity.Specialite;

import java.util.List;

public interface SpecialiteService {
    List<SpecialiteDTO> recupererParFiliere(Long filiereId);
    void creerSpecialite(Long filiereId, SpecialiteDTO specialiteDTO);
    void modifierSpecialite(Long id, SpecialiteDTO specialiteDTO);
    void supprimer(Long id);


    public List<Specialite> GetSpecialiteParFiliere(Long id);

    public List<Specialite> getAll();
}


