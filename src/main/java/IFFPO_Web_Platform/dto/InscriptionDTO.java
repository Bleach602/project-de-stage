package IFFPO_Web_Platform.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InscriptionDTO {

    @NotBlank(message = "Nom obligatoire")
    private String nom;
    @NotBlank(message = "Prénom obligatoire")
    private String prenom;
    @Email
    private String email;
    @NotBlank(message = "Numéro de téléphone obligatoire")
    private String telephone;
    @NotBlank(message = "Mot de passe obligatoire")
    private String mdp;

    //CONSTRUCTORS
    public InscriptionDTO() {}

    public InscriptionDTO(String nom, String prenom, String email,
                          String telephone, String mdp) {
        super();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.mdp = mdp;
    }

    //GETTERS & SETTERS
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
}
