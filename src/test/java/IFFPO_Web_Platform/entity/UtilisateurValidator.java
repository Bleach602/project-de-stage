package IFFPO_Web_Platform.entity;

public class UtilisateurValidator {

    public Boolean emailValide(String email){

        return email != null && email.contains("@");
    }
}
