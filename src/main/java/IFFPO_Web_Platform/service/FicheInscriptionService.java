package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.FicheInscriptionDTO;
import IFFPO_Web_Platform.entity.FicheInscription;
import org.springframework.web.multipart.MultipartFile;

public interface FicheInscriptionService {


    // on eregistre la fiche d'un utilisateur
//    FicheInscription enregistrer(
//            FicheInscription fiche,
//            MultipartFile photo,
//            String emailUtilisateur
//    );

    FicheInscription enregistrer(
            FicheInscriptionDTO fiche,
            String emailUtilisateur
    );


    /**
     * Vérifie si un utilisateur possède
     * déjà une fiche d'inscription.
     *
     *
     * - désactiver le formulaire
     * - activer le téléchargement
     *
     * @param email email du candidat connecté
     * @return true si la fiche existe
     */
    boolean ficheExiste(String email);
}
