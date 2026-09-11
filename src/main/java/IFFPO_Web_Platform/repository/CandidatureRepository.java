package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.Specialite;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CandidatureRepository extends JpaRepository<Candidature, Long>,
        JpaSpecificationExecutor<Candidature> {

    Optional<Candidature> findByReference(String reference);

    long countBySessionCandidature_AnneeAcademique(String anneeAcademique);

    /**
     * Compte le nombre de candidatures déposées par un utilisateur.
     *
     */
    long countByUtilisateur(Utilisateur utilisateur);

    /**
     * Vérifie si un candidat a déjà postulé
     * pour une spécialité donnée.
     */
    boolean existsByUtilisateurAndSpecialite(
            Utilisateur utilisateur,
            Specialite specialite);

    Optional<Candidature>
    findFirstByUtilisateurOrderByDateCandidatureDesc(
            Utilisateur utilisateur);


    // --------  fadil --------------

    long countByUtilisateurAndStatutCandidatureIn(Utilisateur utilisateur, List<StatutCandidature> statutCandidatures);

    long countByStatutCandidatureAndNombreTentativesCorrectionGreaterThan(StatutCandidature statut, int seuil);


    //METHODES POUR LES STATS(04)
    long countByStatutCandidature(StatutCandidature statutCandidature);

    long countByStatutCandidatureAndDateCandidatureBetween(StatutCandidature statutCandidature, LocalDate debut, LocalDate fin);

    @Query("SELECT c.dateCandidature FROM Candidature c WHERE c.dateCandidature BETWEEN :debut AND :fin")
    List<LocalDate> findDatesCandidatureEntrePeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT f.nom, COUNT(c) FROM Candidature c JOIN c.specialite s JOIN s.filiere f " +
            "WHERE c.dateCandidature BETWEEN :debut AND :fin " +
            "GROUP BY f.nom ORDER BY COUNT(c) DESC")
    List<Object[]> countCandidaturesParFiliere(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);


}
