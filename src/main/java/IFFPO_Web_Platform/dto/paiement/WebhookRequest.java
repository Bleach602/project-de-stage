package IFFPO_Web_Platform.dto.paiement;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WebhookRequest {

    /**
     * Référence CamPay
     */
    private String reference;

    /**
     * Notre référence interne
     */
    @JsonProperty("external_reference")
    private String externalReference;

    /**
     * SUCCESSFUL
     * FAILED
     * PENDING
     */
    private String status;

    /**
     * Montant
     */
    private String amount;

    /**
     * Devise
     */
    private String currency;

    /**
     * Code opérateur
     */
    private String code;

    /**
     * Référence Orange/MTN
     */
    @JsonProperty("operator_reference")
    private String operatorReference;
}