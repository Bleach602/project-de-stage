package IFFPO_Web_Platform.dto;

import IFFPO_Web_Platform.entity.Specialite;
import IFFPO_Web_Platform.entity.enums.OuiNon;
import IFFPO_Web_Platform.entity.enums.Sexe;
import IFFPO_Web_Platform.entity.enums.StatutResponsable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class FicheInscriptionDTO {

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être valide")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le lieu de naissance est obligatoire")
    @Size(
            max = 70,
            message = "Le lieu de naissance ne doit pas dépasser 70 caractères"
    )
    private String lieuNaissance;

    @NotNull(message = "Le sexe est obligatoire")
    private Sexe sexe;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(
            max = 50,
            message = "L'adresse ne doit pas dépasser 50 caractères"
    )
    private String adresse;

    @NotBlank(message = "Le diplôme est obligatoire")
    @Size(
            max = 30,
            message = "Le diplôme ne doit pas dépasser 30 caractères"
    )
    private String diplomePlusEleve;

    @NotBlank(message = "L'année d'obtention est obligatoire")
    @Pattern(
            regexp = "^(19|20)\\d{2}$",
            message = "L'année d'obtention est invalide"
    )
    private String anneeObtention;

    @NotNull(message = "Veuillez préciser si vous avez un handicap")
    private OuiNon handicap;

    @Size(
            max = 50,
            message = "La précision du handicap ne doit pas dépasser 50 caractères"
    )
    private String precisionHandicap;

    @NotNull(message = "Veuillez préciser si vous avez une maladie chronique")
    private OuiNon maladie;

    @Size(
            max = 50,
            message = "La précision de la maladie ne doit pas dépasser 50 caractères"
    )
    private String precisionMaladie;

    @NotBlank(message = "Le nom du responsable est obligatoire")
    @Size(max = 20)
    private String nomResponsable;

    @NotBlank(message = "Le prénom du responsable est obligatoire")
    @Size(max = 20)
    private String prenomResponsable;

    @NotBlank(message = "Le téléphone du responsable est obligatoire")
    @Pattern(
            regexp = "^\\+?[0-9 ]{8,20}$",
            message = "Le numéro de téléphone est invalide"
    )
    private String telephoneResponsable;

    @NotBlank(message = "L'adresse du responsable est obligatoire")
    @Size(max = 50)
    private String adresseResponsable;

    @NotNull(message = "Le statut du responsable est obligatoire")
    private StatutResponsable statutResponsable;


    @NotNull(message = "La spécialité est obligatoire")
    private Long specialiteId;


    private MultipartFile photo;
}