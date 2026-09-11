package IFFPO_Web_Platform.client;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Réponse renvoyée par CamPay
 * après l'initialisation d'un paiement.
 */
@Getter
@Setter
@Data
public class CollectResponse {

    /**
     * Référence de la transaction chez CamPay.
     */
    private String reference;

    /**
     * Statut retourné.
     */
    private String status;

    /**
     * Message retourné par CamPay.
     */
    private String message;

    /**
     * Référence externe.
     */
    @JsonProperty("external_reference")
    private String externalReference;

}