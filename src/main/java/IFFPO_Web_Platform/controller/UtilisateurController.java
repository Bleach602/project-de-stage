package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.UserCreateDTO;
import IFFPO_Web_Platform.dto.UserUpdateDTO;
import IFFPO_Web_Platform.entity.Role;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCompte;
import IFFPO_Web_Platform.repository.RoleRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("dashboard/utilisateurs")
@PreAuthorize("hasAuthority('PERM_GESTION_UTILISATEURS')")
public class UtilisateurController {

    private static final String ROLE_CANDIDAT = "ROLE_CANDIDAT";
    private static final int TAILLE_PAGE = 3;

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;
    private final RoleRepository roleRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String liste(@RequestParam(defaultValue = "tous") String categorie,
                        @RequestParam(required = false) Long roleId,
                        @RequestParam(required = false) StatutCompte statutCompte,
                        @RequestParam(required = false) String recherche,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {

        Pageable pageable = PageRequest.of(page, TAILLE_PAGE, Sort.by(Sort.Direction.DESC, "id"));
        Page<Utilisateur> resultat = utilisateurService.listeAvecFiltre(
                categorie, roleId, statutCompte, recherche, pageable);

        model.addAttribute("utilisateurs", resultat);
        model.addAttribute("categorie", categorie);
        model.addAttribute("roleId", roleId);
        model.addAttribute("statut", statutCompte);
        model.addAttribute("recherche", recherche);
        model.addAttribute("tousLesRoles", roleRepository.findAll());
        model.addAttribute("tousLesStatuts", StatutCompte.values());

        // Ajouts indispensables pour alimenter les modales de la liste
        if (!model.containsAttribute("userCreateDTO")) {
            model.addAttribute("userCreateDTO", new UserCreateDTO());
        }
        if (!model.containsAttribute("userUpdateDTO")) {
            model.addAttribute("userUpdateDTO", new UserUpdateDTO());
        }
        model.addAttribute("rolesAdministratifs", roleRepository.findByIntituleNot(ROLE_CANDIDAT));

        return "admin/utilisateurs/liste";

    }

    @GetMapping("/nouveau")
    public String formCreate(Model model){
        model.addAttribute("userCreateDTO", new UserCreateDTO());
        model.addAttribute("rolesAdministratifs", roleRepository.findByIntituleNot(ROLE_CANDIDAT));

        return "admin/utilisateurs/formulaire-creation";
    }

    @PostMapping("/nouveau")
    public String formCreate(@Valid @ModelAttribute("userCreateDTO") UserCreateDTO dto, Model model,
                             RedirectAttributes redirectAttributes) {

        try {
            Utilisateur utilisateur = utilisateurService.creerUserAdministratif(dto);

            redirectAttributes.addFlashAttribute("succes", "Utilisateur "
                    + utilisateur.getNom() + " " + utilisateur.getPrenom() + " crée avec succès");
            return "redirect:/dashboard/utilisateurs?categorie=administratifs";

        } catch (Exception e){
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("rolesAdministratifs", roleRepository.findByIntituleNot(ROLE_CANDIDAT));
            return "admin/utilisateurs/formulaire-creation";
        }
    }

    @GetMapping("/{id}/modifier")
    public String showFormUpdate(@PathVariable Long id, Model model){

        Utilisateur utilisateur = utilisateurService.trouverParId(id);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setEmail(utilisateur.getEmail());
        dto.setRoleId(utilisateur.getRole().getId());
        dto.setStatutCompte(utilisateur.getStatutCompte());

        //Metier : On ne propose que les roles de la meme categorie
        boolean estCandidat = ROLE_CANDIDAT.equals(utilisateur.getRole().getIntitule());
        List<Role> rolesDisponibles = estCandidat
                ? List.of(utilisateur.getRole()) //Un candidat ne voit que son propre role sans modif
                : roleRepository.findByIntituleNot(ROLE_CANDIDAT);

        model.addAttribute("userUpdateDTO", dto);
        model.addAttribute("rolesDisponibles", rolesDisponibles);
        model.addAttribute("tousLesStatuts", StatutCompte.values());
        model.addAttribute("estCandidat", estCandidat);

        return "admin/utilisateurs/formulaire-modification";

    }

    @PostMapping("/{id}/modifier")
    public String updateFormUser(@PathVariable Long id,
                                 @Valid @ModelAttribute("userUpdateDTO") UserUpdateDTO dto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("erreur", "Formulaire invalide, vérifiez les champs.");
            return "redirect:/dashboard/utilisateurs";
        }

        try {
            utilisateurService.modifier(id, dto);
            redirectAttributes.addFlashAttribute("succes", "Utilisateur modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/dashboard/utilisateurs";


    }


    @PostMapping("/{id}/statut")
    public String changeStatut(@PathVariable Long id, @RequestParam StatutCompte newStatut,
                               RedirectAttributes redirectAttributes){
        try {
            utilisateurService.changerStatut(id, newStatut);

            String action = newStatut == StatutCompte.ACTIF ? "active" : "bloque";
            redirectAttributes.addFlashAttribute("succes",
                    "Le compte a été " + action + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur" + e.getMessage());
        }

        return "redirect:/dashboard/utilisateurs";
    }





}
