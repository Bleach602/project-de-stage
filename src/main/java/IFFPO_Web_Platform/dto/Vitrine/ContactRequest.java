package IFFPO_Web_Platform.dto.Vitrine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

//pour les données de la vitrine section nous contactez
public class ContactRequest {

    private String nom;
    private String prenom;
    private String email;
    private String objet;
    private String filiere;
    private String message;
}