package IFFPO_Web_Platform.dto;

import IFFPO_Web_Platform.entity.enums.StatutCompte;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    private Long id;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    private String telephone;

    @Email
    private String email;


    private String motDePasse;

    private Long roleId;

    private StatutCompte statutCompte;


}
