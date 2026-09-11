package IFFPO_Web_Platform.util;




import IFFPO_Web_Platform.entity.enums.TypeDocument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Génère des noms uniques pour les fichiers.
 *
 * Objectif :
 * éviter les collisions de noms
 * et faciliter l'identification des documents.
 */
public final class FileNameGenerator {


    private FileNameGenerator() {
        // Classe utilitaire
    }


    /**
     * Génère un nom unique.
     *
     * Exemple :
     *
     * PHOTO_IDENTITE_20260802_143025_a82f31.jpg
     *
     */
    public static String generate(
            String originalFilename,
            TypeDocument typeDocument
    ) {


        // Récupération de l'extension
        String extension = getExtension(originalFilename);


        // Date actuelle
        String date =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMdd_HHmmss"
                                )
                        );


        // Identifiant unique
        String uuid =
                UUID.randomUUID()
                        .toString()
                        .substring(0,6);


        return typeDocument.name()
                + "_"
                + date
                + "_"
                + uuid
                + extension;

    }



    /**
     * Récupère l'extension du fichier.
     */
    private static String getExtension(String filename) {


        if(filename == null || !filename.contains(".")){
            return "";
        }


        return filename.substring(
                filename.lastIndexOf(".")
        );

    }

}