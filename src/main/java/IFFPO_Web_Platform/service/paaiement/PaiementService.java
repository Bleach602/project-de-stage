package IFFPO_Web_Platform.service.paaiement;

import IFFPO_Web_Platform.dto.paiement.PaymentRequest;
import IFFPO_Web_Platform.dto.paiement.WebhookRequest;
import IFFPO_Web_Platform.entity.Paiement;
import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import org.springframework.security.core.Authentication;

public interface PaiementService {

    /**
     * Initialise un paiement.
     */
    Paiement initiatePayment(PaymentRequest request, Authentication authentication);

    /**
     * Met à jour le statut d'un paiement
     * en interrogeant l'API CamPay.
     *
     //@param campayReference référence retournée par CamPay
     * @return paiement mis à jour
     */
    Paiement updatePaymentStatus(String reference);


    /**
     * Traite une notification (webhook) envoyée par CamPay.
     */

    void processWebhook(WebhookRequest request);

    PaymentStatus getPaymentStatus(String reference);



    /**
     * Retourne le dernier paiement
     * du candidat connecté.
     */
    Paiement getDernierPaiement(String email);

}
