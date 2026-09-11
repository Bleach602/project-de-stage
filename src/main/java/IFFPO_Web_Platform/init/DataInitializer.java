package IFFPO_Web_Platform.init;

import IFFPO_Web_Platform.entity.Permissions;
import IFFPO_Web_Platform.entity.Role;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCompte;
import IFFPO_Web_Platform.repository.PermissionRepository;
import IFFPO_Web_Platform.repository.RoleRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository, UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        initPermissions();
        Role roleCandidat = initRoleCandidat();
        initRoleAdmin();
        initCompteAdmin();

    }

    private void initPermissions(){
        for (String nom : new String[]{
                "PERM_GESTION_UTILISATEURS", "PERM_GESTION_CONTENU",
                "PERM_GESTION_CANDIDATURE", "PERM_GESTION_SESSIONS",
                "PERM_GESTION_FILIERE", "PERM_VOIR_STATISTIQUES",
                "PERM_GESTION_ROLES","PERM_GESTION_SPECIALITE",
                "PERM_GESTION_ACTUALITE"
        }) {
            if (permissionRepository.findByNom(nom).isEmpty()){
                Permissions p = new Permissions();
                p.setNom(nom);
                permissionRepository.save(p);
            }
        }
    }

    private Role initRoleCandidat(){

        return roleRepository.findByIntitule("ROLE_CANDIDAT")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setIntitule("ROLE_CANDIDAT");
                    return roleRepository.save(r);
                });
    }

    private void initRoleAdmin(){
        if (roleRepository.findByIntitule("ROLE_ADMIN").isEmpty()){
            Role admin = new Role();
            admin.setIntitule("ROLE_ADMIN");
            admin.setPermissions(Set.copyOf(permissionRepository.findAll()));
            roleRepository.save(admin);
        }
    }

    private void initCompteAdmin(){
        if (utilisateurRepository.findByEmail("ad@gmail.com").isEmpty()){
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("IFFPO");
            admin.setEmail("ad@gmail.com");
            admin.setTelephone("OOOOO");
            admin.setMotDePasse(passwordEncoder.encode("ad123"));
            admin.setStatutCompte(StatutCompte.ACTIF);
            admin.setRole(roleRepository.findByIntitule("ROLE_ADMIN").get());

            utilisateurRepository.save(admin);
        }
    }
}
