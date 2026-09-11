package IFFPO_Web_Platform.dto.Vitrine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

// notre reponse à envyer par whatsapp
public class WhatsAppContactResponse {

    private String nom;
    private String prenom;
    private String telephone;
    private String whatsappUrl;

}