package IFFPO_Web_Platform.service;


import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.Utilisateur;
import jakarta.servlet.http.HttpServletResponse;

public interface PdfGeneratorService {

    /**
     * Génère le PDF officiel de la fiche
     * et l'enregistre sur le disque.
     */
    void genererPdf(FicheInscription fiche);

    /**
     * Télécharge le PDF déjà généré.
     */
    void telechargerPdf(Utilisateur utilisateur,
                        HttpServletResponse response);

}
