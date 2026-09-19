package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.UserCreateDTO;
import IFFPO_Web_Platform.dto.UserUpdateDTO;
import IFFPO_Web_Platform.entity.Role;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCompte;
import IFFPO_Web_Platform.repository.RoleRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.UtilisateurService;
import IFFPO_Web_Platform.specification.UtilisateurSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private static final String ROLE_CANDIDAT = "ROLE_CANDIDAT";

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<Utilisateur> listeAvecFiltre(String categorie, Long roleId,
                                             StatutCompte statutCompte, String recherche,
                                             Pageable pageable) {
        return utilisateurRepository.findAll(
                UtilisateurSpecification.avecFiltres(categorie, roleId, statutCompte, recherche),
                pageable
        );
    }

    @Override
    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User non trouvé."));
    }

    @Override
    public Utilisateur creerUserAdministratif(UserCreateDTO dto) {

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(()-> new RuntimeException("Role non trouvé."));

        //Ce Formulaire ne cree jamais de ROLE_CANDIDAT...
        //Les candidats s'inscrivent eux memes via /inscription...
        if (ROLE_CANDIDAT.equals(role.getIntitule())){
            throw new IllegalStateException("Impossible d'attribuer ROLE_CANDIDAT depuis ce formulaire.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        utilisateur.setStatutCompte(StatutCompte.ACTIF);
        utilisateur.setRole(role);

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur modifier(Long id, UserUpdateDTO dto) {

        Utilisateur utilisateur = trouverParId(id);
        Role newRole = roleRepository.findById(dto.getRoleId())
                .orElseThrow(()-> new RuntimeException("User non trouvé."));

        boolean ancienEstCandidat = ROLE_CANDIDAT.equals(utilisateur.getRole().getIntitule());
        boolean nouveauEstCandidat = ROLE_CANDIDAT.equals(newRole.getIntitule());

        //REGLE METIER : Impossible de changer de categorie (candidat <--> administratifs)
        if (ancienEstCandidat != nouveauEstCandidat){
            throw new IllegalStateException(
                    "Impossible de changer la categorie de role d'un user" +
                            "(candidat et administratif sont etanches)"
            );
        }

        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setRole(newRole);
        utilisateur.setStatutCompte(dto.getStatutCompte());

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public void changerStatut(Long id, StatutCompte nouveauStatut) {

        Utilisateur utilisateur = trouverParId(id);
        utilisateur.setStatutCompte(nouveauStatut);
        utilisateurRepository.save(utilisateur);
    }


    @Override
    public List<Utilisateur> trouverPermisions_Gest_cont_LimitDEUX() {
        return  utilisateurRepository
                .findUtilisateursAvecPermission("PERM_GESTION_CONTENU")
                .stream()
                .limit(3)
                .toList();
    }

    @Override
    public List<Utilisateur> trouverPermisions_Gest_Candidat_LimitDEUX() {
        return  utilisateurRepository
                .findUtilisateursAvecPermission("PERM_GESTION_CANDIDATURE")
                .stream()
                .limit(1)
                .toList();
    }

    @Override
    public long compterUtilisateurs() {
        return utilisateurRepository.count();
    }


}
