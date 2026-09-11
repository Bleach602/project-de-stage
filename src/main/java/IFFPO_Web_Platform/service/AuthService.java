package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.InscriptionDTO;
import IFFPO_Web_Platform.entity.Utilisateur;

public interface AuthService {

    Utilisateur inscrire(InscriptionDTO inscriptionDTO);
    Utilisateur UpdateProfile(InscriptionDTO inscriptionDTO, String email);

}
