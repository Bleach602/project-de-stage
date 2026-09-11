package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.SpecialiteDTO;
import IFFPO_Web_Platform.entity.Filiere;
import IFFPO_Web_Platform.entity.Specialite;
import IFFPO_Web_Platform.repository.FiliereRepository;
import IFFPO_Web_Platform.repository.SpecialiteRepository;
import IFFPO_Web_Platform.service.SpecialiteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialiteServiceImpl implements SpecialiteService {

    private final SpecialiteRepository specialiteRepository;
    private final FiliereRepository filiereRepository;

    public SpecialiteServiceImpl(SpecialiteRepository specialiteRepository, FiliereRepository filiereRepository) {
        this.specialiteRepository = specialiteRepository;
        this.filiereRepository = filiereRepository;
    }

    //Entity EN DTO (CONVERTION)
    private SpecialiteDTO toDTO(Specialite entity){

        SpecialiteDTO dto = new SpecialiteDTO();

        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setFraisPreInscription(entity.getFraisPreInscription());
        dto.setTranche1(entity.getTranche1());
        dto.setTranche2(entity.getTranche2());
        dto.setTranche3(entity.getTranche3());

        if (entity.getFiliere() != null){
            dto.setFiliereId(entity.getFiliere().getId());
        }

        return dto;
    }

    @Override
    public List<SpecialiteDTO> recupererParFiliere(Long filiereId) {

        List<Specialite> specialites = specialiteRepository.findByFiliereId(filiereId);

        return specialites.stream()
                .sorted((s1, s2) -> s2.getId().compareTo(s1.getId())) //Tri DESC
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void creerSpecialite(Long filiereId, SpecialiteDTO specialiteDTO) {

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(()-> new RuntimeException("Aucune filière avec l'Id" + filiereId));

        Specialite specialite = new Specialite();

        specialite.setNom(specialiteDTO.getNom());
        specialite.setDescription(specialiteDTO.getDescription());
        specialite.setFraisPreInscription(specialiteDTO.getFraisPreInscription());
        specialite.setTranche1(specialiteDTO.getTranche1());
        specialite.setTranche2(specialiteDTO.getTranche2());
        specialite.setTranche3(specialiteDTO.getTranche3());
        specialite.setFiliere(filiere); //LIEN DE LA SPECIALITE AVEC SA FILIERE PARENTE

        specialiteRepository.save(specialite);
    }

    @Override
    public void modifierSpecialite(Long id, SpecialiteDTO dto) {
        Specialite specialite = specialiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spécialité introuvable"));

        specialite.setNom(dto.getNom());
        specialite.setDescription(dto.getDescription());
        specialite.setFraisPreInscription(dto.getFraisPreInscription());
        specialite.setTranche1(dto.getTranche1());
        specialite.setTranche2(dto.getTranche2());
        specialite.setTranche3(dto.getTranche3());

        specialiteRepository.save(specialite);
    }

    @Override
    public void supprimer(Long id) {
        if (!specialiteRepository.existsById(id)) {
            throw new RuntimeException("Spécialité introuvable");
        }
        specialiteRepository.deleteById(id);
    }



    @Override
    public List<Specialite> GetSpecialiteParFiliere(Long id) {
        return  specialiteRepository.findByFiliereId(id);
    }

    @Override
    public List<Specialite> getAll() {
        return specialiteRepository.findAll();
    }

}
