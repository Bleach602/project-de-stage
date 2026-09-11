package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Filiere;
import IFFPO_Web_Platform.entity.enums.StatutFiliere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FiliereRepository extends JpaRepository<Filiere, Long> {

    boolean existsByNom(String nom);
    boolean existsBySigle(String nom);

    List<Filiere> findByStatutFiliere(StatutFiliere statutFiliere);

    //METHODES POUR LES STATS
    long countByStatutFiliere(StatutFiliere statutFiliere);
}
