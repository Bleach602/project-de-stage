package IFFPO_Web_Platform.service.implementation;


import IFFPO_Web_Platform.dto.AdminNotificationDTO;
import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.entity.enums.TypeNotification;
import IFFPO_Web_Platform.repository.CandidatureRepository;
import IFFPO_Web_Platform.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final CandidatureRepository candidatureRepository;

    @Override
    public List<AdminNotificationDTO> getNotifications() {

        List<AdminNotificationDTO> notifications = new ArrayList<>();

        // Récupère toutes les candidatures triées par date desc
        List<Candidature> candidatures = candidatureRepository.findAll();

        for (Candidature c : candidatures) {

            String candidat = c.getUtilisateur().getPrenom()
                    + " " + c.getUtilisateur().getNom();

            String specialite = c.getSpecialite() != null
                    ? c.getSpecialite().getNom()
                    : "—";

            String lien = "/dashboard/candidatures/" + c.getId();

            // ============================================
            // 1. Nouvelle candidature (jamais corrigée)
            // ============================================
            if (c.getStatutCandidature() == StatutCandidature.EN_ATTENTE
                    && c.getNombreTentativesCorrection() == 0) {

                AdminNotificationDTO n = new AdminNotificationDTO();
                n.setId(c.getId());
                n.setTitre("Nouvelle candidature");
                n.setMessage(candidat + " a soumis une candidature en " + specialite);
                n.setDate(c.getDateCandidature().atStartOfDay());
                n.setType(TypeNotification.INFO);
                n.setIcon("bx-file");
                n.setNouvelle(true);
                n.setLien(lien);
                n.setCandidatNom(candidat);
                notifications.add(n);
            }

            // ============================================
            // 2. Correction reçue (candidat a resoumis)
            // ============================================
            else if (c.getStatutCandidature() == StatutCandidature.EN_ATTENTE
                    && c.getNombreTentativesCorrection() > 0) {

                AdminNotificationDTO n = new AdminNotificationDTO();
                n.setId(c.getId());
                n.setTitre("Correction reçue");
                n.setMessage(candidat + " a corrigé son dossier (tentative "
                        + c.getNombreTentativesCorrection() + "/2) — " + specialite);
                n.setDate(LocalDateTime.now());
                n.setType(TypeNotification.SUCCESS);
                n.setIcon("bx-check-circle");
                n.setNouvelle(true);
                n.setLien(lien);
                n.setCandidatNom(candidat);
                notifications.add(n);
            }

            // ============================================
            // 3. Correction demandée (en attente du candidat)
            // ============================================
            else if (c.getStatutCandidature() == StatutCandidature.EN_COURS_DE_CORRECTION) {

                AdminNotificationDTO n = new AdminNotificationDTO();
                n.setId(c.getId());
                n.setTitre("En attente de correction");
                n.setMessage(candidat + " doit corriger : "
                        + (c.getDocumentsACorriger() != null
                        ? c.getDocumentsACorriger().toString()
                          .replace("[", "").replace("]", "")
                        : "—"));
                n.setDate(LocalDateTime.now());
                n.setType(TypeNotification.WARNING);
                n.setIcon("bx-time-five");
                n.setNouvelle(false); // c'est toi qui l'as demandée
                n.setLien(lien);
                n.setCandidatNom(candidat);
                notifications.add(n);
            }
        }

        // Tri par date desc
        notifications.sort(
                Comparator.comparing(AdminNotificationDTO::getDate).reversed());

        return notifications;
    }

    @Override
    public long countNouvelles() {
        return getNotifications().stream()
                .filter(AdminNotificationDTO::isNouvelle)
                .count();
    }
}