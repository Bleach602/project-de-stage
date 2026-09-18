package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.NotificationDTO;
import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import IFFPO_Web_Platform.entity.enums.TypeNotification;
import IFFPO_Web_Platform.repository.CandidatureRepository;
import IFFPO_Web_Platform.repository.FicheInscriptionRepository;
import IFFPO_Web_Platform.repository.PaiementRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static IFFPO_Web_Platform.entity.enums.PaymentStatus.EXPIRED;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UtilisateurRepository utilisateurRepository;

    private final FicheInscriptionRepository ficheRepository;

    private final CandidatureRepository candidatureRepository;

    private final PaiementRepository paiementRepository;


    @Override
    public List<NotificationDTO> getNotifications(String email) {

        /*
         * Liste des notifications à retourner.
         */
        List<NotificationDTO> notifications = new ArrayList<>();

        /*
         * Recherche du candidat connecté.
         */
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable."));

        /*
         * Recherche de la dernière candidature.
         */
        candidatureRepository
                .findFirstByUtilisateurOrderByDateCandidatureDesc(utilisateur)
                .ifPresent(candidature -> {

                    /*
                     * ==============================
                     * Notification : candidature déposée
                     * ==============================
                     */
                    NotificationDTO depot = new NotificationDTO();

                    depot.setTitre("Candidature déposée");

                    depot.setMessage(
                            "Votre candidature en "
                                    + candidature.getSpecialite().getNom()
                                    + " a été enregistrée avec succès.");

                    depot.setDate(
                            candidature.getDateCandidature().atStartOfDay());

                    depot.setType(TypeNotification.INFO);

                    depot.setNouvelle(false);

                    notifications.add(depot);

                    /*
                     * ==============================
                     * Notification selon le statut
                     * ==============================
                     */

                    switch (candidature.getStatutCandidature()) {



                        case VALIDER -> {

                            NotificationDTO validation = new NotificationDTO();

                            validation.setTitre(
                                    "Candidature validée");

                            validation.setMessage(
                                    "Félicitations ! Votre candidature a été validée. Vous pouvez maintenant effectuer votre paiement.");

                            validation.setDate(
                                    candidature.getDateValidation().atStartOfDay());

                            validation.setType(TypeNotification.SUCCESS);

                            validation.setNouvelle(true);

                            notifications.add(validation);
                        }

                        case REJETEE -> {

                            NotificationDTO rejet = new NotificationDTO();

                            rejet.setTitre(
                                    "Candidature rejetée");

                            rejet.setMessage(
                                    "Motif : "
                                            + candidature.getMotifRejet());

                            rejet.setDate(
                                    candidature.getDateValidation().atStartOfDay());

                            rejet.setType(TypeNotification.ERROR);

                            rejet.setNouvelle(true);

                            notifications.add(rejet);
                        }

                        case EN_COURS_DE_CORRECTION -> {

                            NotificationDTO correction = new NotificationDTO();

                            correction.setTitre("Correction demandée");

                            String docs = (candidature.getDocumentsACorriger() == null
                                    || candidature.getDocumentsACorriger().isEmpty())
                                    ? "certains documents"
                                    : candidature.getDocumentsACorriger().stream()
                                      .map(Enum::name)
                                      .collect(Collectors.joining(", "));

                            correction.setMessage(
                                    "Votre candidature nécessite une correction sur : " + docs
                                            + ". Motif : " + candidature.getMessageCorrection()
                                            + ". Tentative " + (candidature.getNombreTentativesCorrection() + 1)
                                            + "/2."
                            );

                            correction.setDate(LocalDateTime.now());
                            correction.setType(TypeNotification.WARNING);
                            correction.setIcon("fa-exclamation-triangle");
                            correction.setNouvelle(true);

                            notifications.add(correction);
                        }

                        case EN_ATTENTE -> {

                            NotificationDTO attente = new NotificationDTO();

                            if (candidature.getNombreTentativesCorrection() > 0) {
                                // Retour après correction
                                attente.setTitre("Correction reçue");
                                attente.setMessage(
                                        "Vos documents corrigés ont bien été reçus. "
                                                + "Votre dossier est de nouveau en cours d'analyse.");
                                attente.setType(TypeNotification.INFO);
                                attente.setIcon("fa-check-circle");
                            } else {
                                attente.setTitre("Dossier en cours d'étude");
                                attente.setMessage(
                                        "Votre candidature est en cours d'analyse par l'administration.");
                                attente.setType(TypeNotification.WARNING);
                            }

                            attente.setDate(candidature.getDateCandidature().atStartOfDay());
                            attente.setNouvelle(candidature.getNombreTentativesCorrection() > 0);
                            notifications.add(attente);
                        }
                    }

                });


        /*
         * ==============================
         * Notification : paiement
         * ==============================
         */
        paiementRepository
                .findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(utilisateur)
                .ifPresent(paiement -> {

                    NotificationDTO paiementNotif = new NotificationDTO();

                    paiementNotif.setDate(paiement.getUpdatedAt());

                    paiementNotif.setNouvelle(true);

                    switch (paiement.getStatus()) {

                        case SUCCESS -> {

                            paiementNotif.setTitre("Paiement validé");

                            paiementNotif.setMessage(
                                    "Votre paiement de "
                                            + paiement.getMontant()
                                            + " FCFA a été validé avec succès. "
                                            + "Votre reçu est disponible.");

                            paiementNotif.setType(TypeNotification.SUCCESS);
                            paiementNotif.setNouvelle(true);
                            paiementNotif.setIcon("fa-check-circle");


                            if (paiement.getRecu() != null) {

                                NotificationDTO notif = new NotificationDTO();
                                notif.setDate(paiement.getUpdatedAt());

                                notif.setNouvelle(true);

                                notif.setTitre("Reçu disponible");

                                notif.setMessage(
                                        "Votre reçu de paiement est disponible au téléchargement.");

                                notif.setDate(
                                        paiement.getUpdatedAt());

                                notif.setType(TypeNotification.SUCCESS);

                                notif.setIcon("fa-file-pdf");

                                notifications.add(notif);

                            }
                            notifications.add(paiementNotif);
                        }

                        case PENDING -> {

                            paiementNotif.setTitre("Paiement en attente");

                            paiementNotif.setMessage(
                                    "Votre paiement est en cours de validation.");

                            paiementNotif.setType(TypeNotification.WARNING);

                            paiementNotif.setIcon("fa-clock");
                            notifications.add(paiementNotif);

                        }

                        case FAILED -> {

                            paiementNotif.setTitre("Paiement échoué");

                            paiementNotif.setMessage(
                                    "Votre paiement n'a pas pu être validé.");

                            paiementNotif.setType(TypeNotification.ERROR);

                            paiementNotif.setIcon("fa-times-circle");
                            notifications.add(paiementNotif);

                        }

                        case CANCELLED -> {

                            paiementNotif.setTitre("Paiement annulé");

                            paiementNotif.setMessage(
                                    "Votre paiement a été annulé.");

                            paiementNotif.setType(TypeNotification.ERROR);

                            paiementNotif.setIcon("fa-ban");
                            notifications.add(paiementNotif);

                        }

                        case EXPIRED -> {

                            paiementNotif.setTitre("Paiement expiré");

                            paiementNotif.setMessage(
                                    "Votre demande de paiement a expiré.");

                            paiementNotif.setType(TypeNotification.WARNING);

                            paiementNotif.setIcon("fa-hourglass-end");

                        }

                    }

                    notifications.add(paiementNotif);

                });



        /*
         * Tri des notifications
         * de la plus récente à la plus ancienne.
         */
        notifications.sort(
                Comparator.comparing(NotificationDTO::getDate)
                        .reversed());

        return notifications;

    }





    }

