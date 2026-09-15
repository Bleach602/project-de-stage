package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>,
        JpaSpecificationExecutor<Utilisateur> {

    @Query("SELECT u FROM Utilisateur u " +
    "LEFT JOIN FETCH u.role r " +
            "LEFT JOIN FETCH r.permissions " +
    "WHERE u.email = :email")
    Optional<Utilisateur> findByEmail(@Param("email") String email); //For DataInitializer...

    //JpaSpecificationExecutor : Permet de combiner filtres optionnels sans ecrire une requete par
    //combinaison possible.

    //METHODES POUR LES STATS(02)
    long countByRole_Intitule(String intitule);

    @Query("SELECT u.dateInscription FROM Utilisateur u " +
            "WHERE u.role.intitule = :intitule AND u.dateInscription BETWEEN :debut AND :fin")
    List<LocalDateTime> findDatesInscriptionParRoleEntrePeriode(@Param("intitule") String intitule,
                                                                @Param("debut") LocalDateTime debut,
                                                                @Param("fin") LocalDateTime fin);

    //BLOQUER COMPTE APRES 03 JOURS SANS SOUMISSION DE CANDIDATURE
    List<Utilisateur> findByRole_IntituleAndStatutCompteAndDateInscriptionBefore(String intitule,
                                                                                 StatutCompte statut, LocalDateTime avant);

    // pour recupérer les permissions adéquoit
    @Query("""
    SELECT DISTINCT u
    FROM Utilisateur u
    JOIN u.role r
    JOIN r.permissions p
    WHERE p.nom = :permission""")
    List<Utilisateur> findUtilisateursAvecPermission(@Param("permission") String permission);


    //filtrage des utilisaturs éligible à la suppression de compte
    @Modifying
    @Query("""
        Delete  FROM Utilisateur u
           WHERE u.dateInscription < :limit
               AND NOT EXISTS (
                   SELECT 1 FROM FicheInscription f
                       WHERE f.utilisateur.id= u.id
                   )
        AND NOT EXISTS (
            SELECT 1 FROM Candidature c
                WHERE c.utilisateur.id = u.id
            )
                And u.role.id =1
    """)
    int findUserEligibleForDeletion(@Param("limit") LocalDateTime limit);

}
