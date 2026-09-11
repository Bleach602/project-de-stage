package IFFPO_Web_Platform.client;



import IFFPO_Web_Platform.entity.enums.MobileMoneyOperator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Représente la requête envoyée à CamPay
 * pour initier un paiement Mobile Money.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectRequest {

    /**
     * Montant à payer.
     */
    private String amount;

    /**
     * Devise utilisée.
     * Pour le Cameroun : XAF
     */
    private String currency;

    /**
     * Numéro Mobile Money du client.
     */
    @JsonProperty("from")
    private String phoneNumber;

    /**
     * Description visible dans le Dashboard CamPay.
     */
    private String description;

    /**
     * Référence générée par notre application.
     */
    @JsonProperty("external_reference")
    private String externalReference;

    /**
     * Opérateur Mobile Money.
     * MTN ou ORANGE
     */

    private MobileMoneyOperator operator;
}