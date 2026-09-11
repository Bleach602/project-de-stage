package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Long, Notifications> {

    boolean DeletById(Long id);
    boolean existsById(Long id);
}
