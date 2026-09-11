package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.NotificationDTO;
import IFFPO_Web_Platform.entity.Candidature;

import java.util.List;

public interface NotificationService {

    /**
     * Construit les notifications
     * du candidat connecté.

     */
    List<NotificationDTO> getNotifications(String email);

}
