package IFFPO_Web_Platform.dto;


import IFFPO_Web_Platform.entity.enums.TypeNotification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDTO {

    /**
     * Titre de la notification.
     */
    private String titre;

    /**
     * Description détaillée.
     */
    private String message;

    /**
     * Date de l'évènement.
     */
    private LocalDateTime date;

    /**
     * Type d'affichage.
     *
     * success
     * error
     * warning
     * info
     */
    private TypeNotification type;

    /**
     * Permet d'afficher un badge
     * lorsqu'une notification est nouvelle.
     */
    private boolean nouvelle;

//    private IconNotification icon;
    private String icon;
}
