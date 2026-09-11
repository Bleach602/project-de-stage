package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatRepository extends JpaRepository<Utilisateur,Long> {

    Utilisateur findByEmail(String  email);
    boolean existsByEmail(String email);
    List<Utilisateur> findAll();

}
