package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.entity.SessionCandidature;
import IFFPO_Web_Platform.repository.CandidatureRepository;
import org.springframework.stereotype.Service;

@Service
public class ReferenceGeneratorCandidature {
    private final CandidatureRepository candidatureRepository;
    private static final int MAX_TENTATIVES = 5;

    public ReferenceGeneratorCandidature(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public String generateReference(SessionCandidature session){

        String anneeFin = extraireAnneeFin(session.getAnneeAcademique());

        for (int tentative = 0; tentative < MAX_TENTATIVES; tentative++){
            long count = candidatureRepository
                    .countBySessionCandidature_AnneeAcademique(session.getAnneeAcademique());

            long prochainNumero = count + 1 + tentative;

            String reference = "CND" + anneeFin + "-" + String.format("%04d", prochainNumero);

            if (candidatureRepository.findByReference(reference).isEmpty()){
                return reference;
            }
        }
        throw new IllegalStateException("Impossible de generer plusieurs references" +
                "après plusieurs tentatives.");
    }

    private String extraireAnneeFin(String anneeAcademique){
        String [] parties = anneeAcademique.split("-");
        return parties[parties.length - 1].trim();
    }
}
