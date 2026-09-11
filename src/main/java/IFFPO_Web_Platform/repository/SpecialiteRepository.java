package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {

    List<Specialite> findByFiliereId(Long filiereId);

    //Verifie s'il existe des candidatures...
//    boolean existsByIdAndCandidaturesNoEmpty(Long id);


}
