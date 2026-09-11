package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permissions, Long> {

    Optional<Permissions> findByNom(String nom); //For DataInitializer...
}
