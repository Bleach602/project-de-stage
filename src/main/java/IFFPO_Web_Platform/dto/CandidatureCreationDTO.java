package IFFPO_Web_Platform.dto;

import org.springframework.web.multipart.MultipartFile;

public class CandidatureCreationDTO {

    private MultipartFile cni;
    private MultipartFile diplome;
    private MultipartFile acte;

    //GETTERS & SETTERS
    public MultipartFile getCni() {
        return cni;
    }

    public void setCni(MultipartFile cni) {
        this.cni = cni;
    }

    public MultipartFile getDiplome() {
        return diplome;
    }

    public void setDiplome(MultipartFile diplome) {
        this.diplome = diplome;
    }

    public MultipartFile getActe() {
        return acte;
    }

    public void setActe(MultipartFile acte) {
        this.acte = acte;
    }
}
