package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByIntitule(String intitule); //For DataInitializer...

    List<Role> findByIntituleNot(String intitule); //Lister les roles administratifs...
}
