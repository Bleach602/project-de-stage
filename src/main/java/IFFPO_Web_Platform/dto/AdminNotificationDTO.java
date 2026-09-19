package IFFPO_Web_Platform.dto;



import IFFPO_Web_Platform.entity.enums.TypeNotification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminNotificationDTO {

    private Long id;              // id de la candidature concernée
    private String titre;
    private String message;
    private LocalDateTime date;
    private TypeNotification type;
    private String icon;
    private boolean nouvelle;
    private String lien;          // URL vers la candidature
    private String candidatNom;
}