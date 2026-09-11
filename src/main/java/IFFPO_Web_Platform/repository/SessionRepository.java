package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.SessionCandidature;
import IFFPO_Web_Platform.entity.enums.StatutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionCandidature, Long> {

    boolean existsByAnneeAcademique(String anneeAcademique);
    boolean existsByStatutSession(StatutSession statutSession);
    //RECHERCHER UNE SESSION PAR ANNEE
    //VERIFIER S'IL EXISTE DEJA UNE SESSION OUVERTE

    Optional<SessionCandidature> findByStatutSession(StatutSession statutSession);

}
