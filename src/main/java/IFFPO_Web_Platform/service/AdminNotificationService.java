package IFFPO_Web_Platform.service;



import IFFPO_Web_Platform.dto.AdminNotificationDTO;

import java.util.List;

public interface AdminNotificationService {

    /**
     * Construit la liste des notifications admin :
     * - nouvelles candidatures (EN_ATTENTE, tentatives = 0)
     * - candidatures corrigées (EN_ATTENTE, tentatives > 0)
     * - candidatures en cours de correction
     */
    List<AdminNotificationDTO> getNotifications();

    /**
     * Compte les notifications "nouvelles" (non lues).
     */
    long countNouvelles();
}