package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FicheInscriptionRepository extends JpaRepository<FicheInscription, Long> {
    /**
     * Recherche la fiche d'inscription
     * associée à un utilisateur. ici Cette méthode est utilisée au chargement
     *      * du tableau de bord afin de savoir si le
     *      * candidat possède déjà une fiche.
     * @param utilisateur utilisateur connecté
     * @return fiche éventuelle
     */
    Optional<FicheInscription> findByUtilisateur(Utilisateur utilisateur);
}