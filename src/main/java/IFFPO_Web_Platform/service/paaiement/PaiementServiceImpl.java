package IFFPO_Web_Platform.service.paaiement;

import IFFPO_Web_Platform.Exception.CampayException;
import IFFPO_Web_Platform.client.CampayClient;
import IFFPO_Web_Platform.client.CollectRequest;
import IFFPO_Web_Platform.client.CollectResponse;
import IFFPO_Web_Platform.dto.paiement.PaymentRequest;
import IFFPO_Web_Platform.dto.paiement.TransactionStatusResponse;
import IFFPO_Web_Platform.dto.paiement.WebhookRequest;
import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.Paiement;
import IFFPO_Web_Platform.entity.Recu;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.MobileMoneyOperator;
import IFFPO_Web_Platform.entity.enums.ModePaiement;
import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.repository.*;
import IFFPO_Web_Platform.service.NotificationService;
import IFFPO_Web_Platform.service.RecuGeneratorService;
import IFFPO_Web_Platform.util.ReferenceGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaiementServiceImpl implements  PaiementService{

    private static final Logger logger =
            LoggerFactory.getLogger(PaiementService.class);
    private final PaiementRepository repository;
    private final UtilisateurRepository utilisateurRepository;
    private final CampayClient campayClient;
    private final CandidatureRepository candidatureRepository;
    private final RecuRepository recuRepository;
    private final NotificationService notificationService;


    private final RecuGeneratorService recuGeneratorService;

    @Override
    public Paiement initiatePayment(PaymentRequest request, Authentication authentication) {
        /*
         * 1) Création de l'entité Payment
         */

        String email = authentication.getName();

        Utilisateur utilisateur =
                utilisateurRepository
                        .findByEmail(email)
                        .orElseThrow();

        Candidature candidature =
                candidatureRepository
                        .findFirstByUtilisateurOrderByDateCandidatureDesc(
                                utilisateur)
                        .orElseThrow();

        if(candidature.getStatutCandidature()
                != StatutCandidature.VALIDER){

            throw new RuntimeException(
                    "Votre candidature n'a pas encore été validée.");
        }

        Paiement payment = new Paiement();

        payment.setCandidature(candidature);

        payment.setNomCandidat( utilisateur.getNom()+" "+
                utilisateur.getPrenom());

        payment.setPhoneNumber(request.getPhoneNumber());

        payment.setMontant(request.getAmount());

        payment.setModePaiement(ModePaiement.valueOf(request.getOperator().name()));


        payment.setStatus(PaymentStatus.PENDING);

        payment.setReference(
                ReferenceGenerator.generateReference()
        );

        /*
         * 2) Sauvegarde dans PostgreSQL
         */

        logger.info("========== DEBUT DU PAIEMENT ==========");


        repository.save(payment);

        logger.info("Paiement enregistré en base.");

        /*
         * 3) Construction de la requête CamPay
         */
        logger.info("Envoi de la requête à CamPay...");

        String phone = request.getPhoneNumber();

        if (!phone.startsWith("237")) {
            phone = "237" + phone;


            CollectRequest collectRequest = CollectRequest.builder()

                    .amount(request.getAmount().toString())

                    .currency("XAF")

                    .phoneNumber(phone)

                    .description("Paiement effectué depuis Spring Boot")

                    .externalReference(payment.getReference())

                    .operator(MobileMoneyOperator.valueOf(request.getOperator().name()))

                    .build();

            /*
             * 4) Appel de CamPay
             */

            try {
                CollectResponse response = campayClient.collectPayment(collectRequest);
                logger.info("CollectRequest = {}", collectRequest);
                logger.info("Réponse CamPay : {}", response);

                payment.setExternalReference(
                        response.getReference()
                );
            } catch (Exception e) {
                logger.error("Erreur lors de l'appel à CamPay", e);

                throw new CampayException(
                        "Impossible d'initier le paiement. Veuillez réessayer.");
            }
            /*
             * 5) Sauvegarde de la référence CamPay
             */
        }
        repository.save(payment);

        return payment;
    }

    @Override
    public Paiement updatePaymentStatus(String reference) {
        System.out.println("========== VERIFICATION DU STATUT ==========");

        // Recherche par référence interne
        Paiement payment = repository
                .findByReference(reference)
                .orElseThrow(() ->
                        new RuntimeException("Paiement introuvable."));

        // Référence CamPay
        String campayReference = payment.getExternalReference();

        // Interrogation CamPay
        TransactionStatusResponse response =
                campayClient.checkTransactionStatus(campayReference);

        System.out.println("Réponse CamPay : " + response);

        switch (response.getStatus()) {

            case "SUCCESSFUL":
                payment.setStatus(PaymentStatus.SUCCESS);

                /*
                 * Générer le reçu uniquement
                 * s'il n'existe pas encore.
                 */

                if (payment.getRecu() == null) {

                    Recu recu = recuGeneratorService.genererRecu(payment);

                    payment.setRecu(recu);

                }
                    break;

            case "FAILED":
                payment.setStatus(PaymentStatus.FAILED);
                break;

            case "EXPIRED":
                payment.setStatus(PaymentStatus.EXPIRED);
                break;

            case "CANCELLED":
                payment.setStatus(PaymentStatus.CANCELLED);
                break;

            default:
                payment.setStatus(PaymentStatus.PENDING);
        }

        repository.save(payment);

        System.out.println("Nouveau statut : " + payment.getStatus());

        return payment;
    }

    @Override
    public void processWebhook(WebhookRequest request) {

        System.out.println("========== WEBHOOK CAM PAY ==========");
        System.out.println(request);

        // Recherche du paiement grâce à la référence CamPay
        Paiement payment = repository
                .findByExternalReference(request.getReference())
                .orElseThrow(() ->
                        new RuntimeException("Paiement introuvable."));

        // Conversion du statut CamPay -> Statut interne
        switch (request.getStatus()) {

            case "SUCCESSFUL":
                payment.setStatus(PaymentStatus.SUCCESS);


//                if (payment.getRecu() == null) {
//
//                    Recu recu = recuGeneratorService.genererRecu(payment);
//
//                    payment.setRecu(recu);
//
//                }
                break;

            case "FAILED":
                payment.setStatus(PaymentStatus.FAILED);
                break;

            case "EXPIRED":
                payment.setStatus(PaymentStatus.EXPIRED);
                break;

            default:
                payment.setStatus(PaymentStatus.PENDING);
        }

        repository.save(payment);


        System.out.println("Paiement mis à jour : " + payment.getStatus());
    }

    @Override
    public PaymentStatus getPaymentStatus(String reference) {
        updatePaymentStatus(reference);
        Paiement payment = repository
                .findByReference(reference)
                .orElseThrow(() ->
                        new RuntimeException("Paiement introuvable."));

        return payment.getStatus();
    }



    @Override
    @Transactional
    public Paiement getDernierPaiement(String email) {

        /*
         * Recherche de l'utilisateur.
         */
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable."));

        /*
         * Recherche de la dernière candidature.
         */
        Candidature candidature = candidatureRepository
                .findFirstByUtilisateurOrderByDateCandidatureDesc(utilisateur)
                .orElse(null);

        if (candidature == null) {
            return null;
        }

        /*
         * Recherche du dernier paiement.
         */
        return repository.findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(candidature.getUtilisateur())
                .orElse(null);
    }
}
