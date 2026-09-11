package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.FiliereDTO;
import IFFPO_Web_Platform.dto.SpecialiteDTO;
import IFFPO_Web_Platform.entity.Filiere;
import IFFPO_Web_Platform.entity.Specialite;
import IFFPO_Web_Platform.entity.enums.StatutFiliere;
import IFFPO_Web_Platform.repository.FiliereRepository;
import IFFPO_Web_Platform.service.FiliereService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FiliereServiceImpl implements FiliereService {

    private final FiliereRepository filiereRepository;

    public FiliereServiceImpl(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    /* Convertir FiliereDTO en entite Filiere (sans id et statut pour la creation) */
    private Filiere toEntity(FiliereDTO dto){

        Filiere filiere = new Filiere();
        filiere.setNom(dto.getNom());
        filiere.setSigle(dto.getSigle());
        filiere.setLimitePlace(dto.getLimitePlaces());

        return filiere;
    }

    /* Convertir l'entité Filière en FiliereDTO */
    private FiliereDTO toDTO(Filiere entity){

        FiliereDTO filiereDTO = new FiliereDTO();

        filiereDTO.setId(entity.getId());
        filiereDTO.setNom(entity.getNom());
        filiereDTO.setSigle(entity.getSigle());
        filiereDTO.setLimitePlaces(entity.getLimitePlace());
        filiereDTO.setStatutFiliere(entity.getStatutFiliere());

        if (entity.getSpecialites() != null){

            List<SpecialiteDTO> specialiteDTOS = new ArrayList<>();

            for (Specialite spec : entity.getSpecialites()){

                SpecialiteDTO specDTO = new SpecialiteDTO();

                specDTO.setId(spec.getId());
                specDTO.setNom(spec.getNom());
                specDTO.setDescription(spec.getDescription());
                specDTO.setFraisPreInscription(spec.getFraisPreInscription());
                specDTO.setTranche1(spec.getTranche1());
                specDTO.setTranche2(spec.getTranche2());
                specDTO.setTranche3(spec.getTranche3());

                specialiteDTOS.add(specDTO);
            }
            filiereDTO.setSpecialites(specialiteDTOS);
        }

        return filiereDTO;

//        return new FiliereDTO(
//                entity.getId(),
//                entity.getNom(),
//                entity.getSigle(),
//                entity.getLimitePlace(),
//                entity.getStatutFiliere()
//        );
    }


    @Override
    @Transactional
    public FiliereDTO trouverParId(Long id) {

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Pas de filière avec l'Id" + id));

        return toDTO(filiere);
    }

    @Override
    public List<FiliereDTO> toutesFilieres() {
        return filiereRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream() //Tri DESC
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FiliereDTO creerFiliere(FiliereDTO filiereDTO) {

        if (filiereRepository.existsByNom(filiereDTO.getNom())){
            throw new RuntimeException("Cette filière existe déjà.");
        }

        if (filiereRepository.existsBySigle(filiereDTO.getSigle())){
            throw new RuntimeException("Ce sigle existe déjà.");
        }

        //CONVERTION DTO en Entity
        Filiere filiere = toEntity(filiereDTO);

        filiere.setStatutFiliere(StatutFiliere.OUVERTE); //Règle metier...

        //Save en BD
        Filiere saveFiliere = filiereRepository.save(filiere);

        //Retour sous forme de DTO
        return toDTO(saveFiliere);
    }

    @Override
    @Transactional
    public FiliereDTO modifierFiliere(Long id, FiliereDTO filiereDTO) {

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Pas de filière."));

        filiere.setNom(filiereDTO.getNom());
        filiere.setSigle(filiereDTO.getSigle());
        filiere.setLimitePlace(filiereDTO.getLimitePlaces());

        Filiere updateFiliere = filiereRepository.save(filiere);
        return toDTO(updateFiliere);
    }

    @Override
    @Transactional
    public void changerStatut(Long id) {

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Pas de filière."));

        if (filiere.getStatutFiliere() == StatutFiliere.OUVERTE){
            filiere.setStatutFiliere(StatutFiliere.FERMEE);
        } else {
            filiere.setStatutFiliere(StatutFiliere.OUVERTE);
        }

        filiereRepository.save(filiere);
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        try {
            Filiere filiere = filiereRepository.findById(id)
                    .orElseThrow(()-> new RuntimeException("Pas de filière avec Id" + id));

            filiereRepository.delete(filiere);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Impossible de supprimer cette filière car elle est actuellement liée" +
                    " à une ou plusieurs sessions de candidature.");
        }

    }








    @Override
    public List<Filiere> getAllFiliere() {
        return filiereRepository.findAll();
    }
}
