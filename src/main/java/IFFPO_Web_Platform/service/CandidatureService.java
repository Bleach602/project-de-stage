package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CandidatureService {

    Page<Candidature> listerAvecFiltres(Long sessionId, StatutCandidature statut,
                                        Long filiereId, Long specialiteId, Pageable pageable);

    Candidature trouverParId(Long id);

    String valider(Long id);
    String rejeter(Long id, String motifRejet);
    String demanderCorrection(Long id, List<TypeDocument> documents, String messageCorrection);

    void verifierTransitionDepuisEnAttente(Candidature candidature);


    // pour les candiadts
    /**
     * Dépose une nouvelle candidature.
     *
     * @param utilisateurEmail email de l'utilisateur connecté
     * @param cni fichier CNI
     * @param diplome fichier diplôme
     * @param acte fichier acte de naissance
     */
    void deposerCandidature(
            String utilisateurEmail,
            MultipartFile cni,
            MultipartFile diplome,
            MultipartFile acte
    ) throws IOException;


    /**
     * Indique si le candidat possède
     * une candidature validée.
     */
    boolean candidatureValidee(String email);

    Optional<Candidature> findDerniereCandidature(String email);

}
