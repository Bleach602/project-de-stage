package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.FormateurDTO;
import IFFPO_Web_Platform.service.FormateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard/formateurs")
@PreAuthorize("hasAuthority('PERM_GESTION_CONTENU')")
@RequiredArgsConstructor
public class FormateurController {

    private final FormateurService formateurService;

    @GetMapping
    public String showFormateurs(@RequestParam(defaultValue = "") String keyWord,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model){

        Page<FormateurDTO> formateursPage = formateurService.getFormateurs(keyWord, page, size);

        model.addAttribute("formateursPage", formateursPage);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", formateursPage.getTotalPages());


        //Si Pas De DTO Dans Le Modele (En cas de retour d'erreur de validation)
        if (!model.containsAttribute("formateurDTO")){
            model.addAttribute("formateurDTO", new FormateurDTO());
        }

        return "admin/formateur/trainer";
    }

    @PostMapping("/creer")
    public String creerFormateur(@Valid @ModelAttribute("formateurDTO") FormateurDTO formateurDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()){
            // 1. Sauvegarde l'ensemble des messages d'erreurs de validation détectés par Spring/Jakarta
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formateurDTO", bindingResult);

            // 2. Renvoie l'objet DTO complété par l'utilisateur pour ne pas effacer les champs qu'il a déjà saisis
            redirectAttributes.addFlashAttribute("formateurDTO", formateurDTO);

            // 3. Transmet une variable pour indiquer au JavaScript de la vue de rouvrir automatiquement le Modal
            redirectAttributes.addFlashAttribute("openModal", "creerModal");

            // 4. Message d'alerte global à afficher en haut de la page
            redirectAttributes.addFlashAttribute("errorMessage", "Veuillez corriger les erreurs dans le formulaire.");

            return "redirect:/dashboard/formateurs";
        }

        try {
            formateurService.createFormateur(formateurDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Formateur créé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la creation :" + e.getMessage());
        }

        return "redirect:/dashboard/formateurs";
    }

    @PostMapping("/modifier/{id}")
    public String modifierFormateur(@PathVariable Long id,
                                    @Valid @ModelAttribute("formateurDTO") FormateurDTO formateurDTO,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formateurDTO", bindingResult);
            redirectAttributes.addFlashAttribute("formateurDTO", formateurDTO);
            redirectAttributes.addFlashAttribute("openModal", "modifierModal");
            redirectAttributes.addFlashAttribute("activeFormateurId", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Veuillez corriger les erreurs dans le formulaire.");
            return "redirect:/dashboard/formateurs";
        }

        try {
            formateurService.updateFormateur(id, formateurDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Formateur mis à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la modification : " + e.getMessage());
        }

        return "redirect:/dashboard/formateurs";
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerFormateur(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {

        try {
            formateurService.deleteFormateur(id);
            redirectAttributes.addFlashAttribute("successMessage", "Formateur supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/dashboard/formateurs";
    }


}
