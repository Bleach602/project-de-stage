package IFFPO_Web_Platform.Exception;

public class PaymentException extends  RuntimeException{

    /**
     * Exception générique liée au module de paiement.
     */
    public PaymentException(String message) {
        super(message);
    }
}
