package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.InscriptionDTO;
import IFFPO_Web_Platform.entity.Role;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCompte;
import IFFPO_Web_Platform.repository.RoleRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(RoleRepository roleRepository, UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Utilisateur inscrire(InscriptionDTO inscriptionDTO) {

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setNom(inscriptionDTO.getNom());
        utilisateur.setPrenom(inscriptionDTO.getPrenom());
        utilisateur.setEmail(inscriptionDTO.getEmail());
        utilisateur.setTelephone(inscriptionDTO.getTelephone());
        utilisateur.setMotDePasse(passwordEncoder.encode(inscriptionDTO.getMdp()));
        utilisateur.setStatutCompte(StatutCompte.ACTIF);

        Role roleUser = roleRepository.findByIntitule("ROLE_CANDIDAT")
                .orElseThrow(()-> new RuntimeException("Role non trouvé"));

        utilisateur.setRole(roleUser);

        return utilisateurRepository.save(utilisateur);
    }


    @Override
    public Utilisateur UpdateProfile(InscriptionDTO inscriptionDTO,String email) {


        Utilisateur user = utilisateurRepository.findByEmail(email).orElseThrow(
                ()->new RuntimeException("Utilisateur non trouvé!!")
        );

        user.setNom(inscriptionDTO.getNom());
        user.setPrenom(inscriptionDTO.getPrenom());
        user.setTelephone(inscriptionDTO.getTelephone());


        return utilisateurRepository.save(user);
    }

}
