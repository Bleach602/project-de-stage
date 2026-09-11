package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.ActualiteDTO;
import IFFPO_Web_Platform.service.ActualiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/dashboard/actualites")
@PreAuthorize("hasAuthority('PERM_GESTION_CONTENU')")
@RequiredArgsConstructor
public class ActualitesController {

    private final ActualiteService actualiteService;

@GetMapping
public String afficherActualites(@RequestParam(defaultValue = "0") int pageActualites,
                                 @RequestParam(defaultValue = "0") int pageArchives,
                                 @RequestParam(defaultValue = "3") int size,
                                 @RequestParam(required = false) String motCle,
                                 @RequestParam(defaultValue = "toutes") String filtre,
                                 Model model) {

    Page<ActualiteDTO> actualites = actualiteService.trouverActualites(pageActualites, size, motCle);

    Page<ActualiteDTO> archives = actualiteService.trouverArchives(pageArchives, size, motCle);

    model.addAttribute("actualites", actualites);
    model.addAttribute("archives", archives);

    model.addAttribute("motCle", motCle);
    model.addAttribute("size", size);
    model.addAttribute("filtre", filtre);

    model.addAttribute("createContenuDTO", new ActualiteDTO());

    return "admin/contenus/news-archives";
}

    @PostMapping("/create")
    public String createContenu(@Valid @ModelAttribute("createContenuDTO")ActualiteDTO actualiteDTO,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Veuillez vérifier les informations saisies.");

            return "redirect:/dashboard/actualites";
        }

        try {
            actualiteService.createActualite(actualiteDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "L'actualité a été créée avec succès.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Une erreur est survenue lors de la creation de l'actualité.");
        }

        return "redirect:/dashboard/actualites";
    }

    /*MODAL DE MODIFICATION :
     le formulaire est affiché, Le JS determine l'ID de l'actualité
        et construit L'URL  */
    @PostMapping("/modifier/{id}")
    public String updateContenu(@PathVariable Long id, @Valid @ModelAttribute("actualiteDTO") ActualiteDTO actualiteDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Veuillez vérifier les informations saisies.");

            return "redirect:/dashboard/actualites";
        }

        try {
            actualiteService.updateActualite(id, actualiteDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "L'actualité a été modifier avec succès.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Une erreur est survenue lors de la creation de l'actualité.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/dashboard/actualites";
    }

    @PostMapping("/{id}/supprimer")
    public String deleteActuality(@PathVariable Long id, RedirectAttributes redirectAttributes){

        try {
            actualiteService.deleteActualite(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ce contenu a été supprimer.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard/actualites";
    }

}
