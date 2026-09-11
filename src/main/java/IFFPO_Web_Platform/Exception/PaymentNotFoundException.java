package IFFPO_Web_Platform.Exception;

public class PaymentNotFoundException extends RuntimeException{

    /**
     * Exception levée lorsqu'un paiement est introuvable.
     */
    public PaymentNotFoundException(String reference) {

        super("Aucun paiement trouvé avec la référence : " + reference);

    }
}
