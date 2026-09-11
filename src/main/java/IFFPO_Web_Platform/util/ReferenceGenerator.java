package IFFPO_Web_Platform.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Génère des références uniques pour les paiements.
 */
public class ReferenceGenerator {

    private ReferenceGenerator() {
    }

    /**
     * Génère une référence unique.
     *
     * Exemple :
     * PAY-20260627-153015
     */
    public static String generateReference() {

        return "PAY-" +
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

    }

}