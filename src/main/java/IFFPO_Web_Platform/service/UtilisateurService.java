package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.UserCreateDTO;
import IFFPO_Web_Platform.dto.UserUpdateDTO;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCompte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UtilisateurService {

    Page<Utilisateur> listeAvecFiltre(String categorie, Long roleId,
                                      StatutCompte statutCompte, String recherche,
                                      Pageable pageable);

    Utilisateur trouverParId(Long id);
    Utilisateur creerUserAdministratif(UserCreateDTO dto);
    Utilisateur modifier(Long id, UserUpdateDTO dto);
    void changerStatut(Long id, StatutCompte nouveauStatut);



    List<Utilisateur> trouverPermisions_Gest_cont_LimitDEUX ();
    List<Utilisateur> trouverPermisions_Gest_Candidat_LimitDEUX ();
    long compterUtilisateurs();

}
