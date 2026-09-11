package IFFPO_Web_Platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FormateurDTO {

    private Long id; //UTILE POUR LA MODIFICATION

    private MultipartFile photo;
    private String existingPhotoUrl;

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @NotBlank(message = "Prénom obligatoire")
    private String prenom;

    @NotBlank(message = "Poste/Role obligatoire")
    private String poste;

    private String bio;

    @NotBlank(message = "Numéro whatsApp obligatoire")
    private String whatsAppNumber;

    @Email(message = "Format d'email invalide")
    private String adresseMail;


    // Pour l'affichage
    private String photoUrl;  //  URL Cloudinary

}
