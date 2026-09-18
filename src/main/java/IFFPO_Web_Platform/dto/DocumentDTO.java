package IFFPO_Web_Platform.dto;


import IFFPO_Web_Platform.entity.enums.TypeDocument;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentDTO {

    /**
     * Identifiant du document.
     */
    private Long id;

    /**
     * Type du document.
     */
    private TypeDocument typeDocument;

    /**
     * Nom du fichier.
     */
    private String nomFichier;

    private LocalDateTime dateModification;

    private boolean aCorriger;  // + getter/setter Lombok
}
