package IFFPO_Web_Platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    private String telephone;

    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = " Le mot de passe doit avoir au moins 8 carractères!")
    private String motDePasse;

    private Long roleId;


}
