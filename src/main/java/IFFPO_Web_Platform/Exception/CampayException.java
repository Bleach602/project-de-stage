package IFFPO_Web_Platform.Exception;

/**
 * Exception levée lorsqu'une erreur provient de CamPay.
 */
public class CampayException extends RuntimeException {

    public CampayException(String message) {
        super(message);
    }
}