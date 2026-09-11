package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.SessionCandidatureDTO;
import IFFPO_Web_Platform.entity.SessionCandidature;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SessionCandidatureService {

    Page<SessionCandidatureDTO> findSessionPagined(int page, int size);
    SessionCandidatureDTO trouverParId(Long id);
    List<SessionCandidatureDTO> findAllsSessionCandidature();
    SessionCandidatureDTO createSessionCandidature(SessionCandidatureDTO sessionCandidatureDTO);
    SessionCandidatureDTO updateSessionCandidature(Long id, SessionCandidatureDTO sessionCandidatureDTO);
    void openSessionCandidature(Long id);
    void closeSessionCandidature(Long id);

    //open et close : Avant d'ouvrir une session, il faudra verifier :
    //qu'il y a au moins une filière, qu'aucune autre session n'est déja ouverte... d'ou les
    //deux méthodes génériques.




    /**
     * Retourne la session ouverte.
     */
   // SessionCandidature getSessionActive();

    SessionCandidatureDTO getSessionActive();

    /**
     * Vérifie si les candidatures sont ouvertes.
     */
    boolean isSessionOuverte();
}
