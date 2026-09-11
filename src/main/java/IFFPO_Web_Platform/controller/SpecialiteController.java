package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.FiliereDTO;
import IFFPO_Web_Platform.dto.SpecialiteDTO;
import IFFPO_Web_Platform.service.FiliereService;
import IFFPO_Web_Platform.service.SpecialiteService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard/filieres/{filiereId}/specialites")
@PreAuthorize("hasAuthority('PERM_GESTION_FILIERE')")
public class SpecialiteController {

    private final SpecialiteService specialiteService;
    private final FiliereService filiereService;

    public SpecialiteController(SpecialiteService specialiteService, FiliereService filiereService) {
        this.specialiteService = specialiteService;
        this.filiereService = filiereService;
    }

    @GetMapping
    public String listSpecialite(@PathVariable("filiereId") Long filiereId, Model model){

        try {
            FiliereDTO filiereDTO = filiereService.trouverParId(filiereId);
            model.addAttribute("filiere", filiereDTO);

            List<SpecialiteDTO> specialites = specialiteService.recupererParFiliere(filiereId);
            model.addAttribute("specialites", specialites);

            //DTO vide POUR LE FORMULAIRE D'AJOUT
            if (!model.containsAttribute("specialiteDTO")){
                model.addAttribute("specialiteDTO", new SpecialiteDTO());
            }

            return "admin/specialite/liste";
        } catch (RuntimeException e) {
            return "redirect:/dashboard/filieres";
        }
    }

    @GetMapping("/creer")
    public String showFormCreate(@PathVariable("filiereId") Long filiereId,
                                 Model model){
        try {
            FiliereDTO filiereDTO = filiereService.trouverParId(filiereId);
            model.addAttribute("filiere", filiereDTO);
            model.addAttribute("specialiteDTO", new SpecialiteDTO());
            return "admin/specialite/create";

        } catch (RuntimeException e) {
            return "redirect:/dashboard/filieres?filiereId=" + filiereId;
        }
    }

    @PostMapping("/creer")
    public String createSpecialite(@PathVariable("filiereId") Long filiereId,
                                   @Valid @ModelAttribute("specialiteDTO") SpecialiteDTO specialiteDTO,
                                   BindingResult bindingResult, Model model,
                                   RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){
            model.addAttribute("filiere", filiereService.trouverParId(filiereId));
            model.addAttribute("specialites", specialiteService.recupererParFiliere(filiereId));

            return "admin/specialite/liste";
        }

        try {
            specialiteService.creerSpecialite(filiereId, specialiteDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Spécialité ajoutée avec succès !");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible d'enregistrer : un des montants est trop élevé ou invalide.");
        }

        return "redirect:/dashboard/filieres?filiereId=" + filiereId;
    }

    @PostMapping("/modifier")
    public String modifierSpecialite(@PathVariable("filiereId") Long filiereId,
                                     @Valid @ModelAttribute("specialiteDTO") SpecialiteDTO specialiteDTO,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la modification de la spécialité.");
            return "redirect:/dashboard/filieres?filiereId=" + filiereId;
        }

        specialiteService.modifierSpecialite(specialiteDTO.getId(), specialiteDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Spécialité modifiée avec succès !");
        return "redirect:/dashboard/filieres?filiereId=" + filiereId;
    }


    @PostMapping("/{id}/supprimer")
    public String deleteSpecialite(@PathVariable("filiereId") Long filiereId,
                                   @PathVariable("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            specialiteService.supprimer(id);
            redirectAttributes.addFlashAttribute("successMessage", "La spécialité a été supprimée.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Impossible de supprimer cette specialité car des candidatures y sont déjà associées.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard/filieres?filiereId=" + filiereId;
    }

}
