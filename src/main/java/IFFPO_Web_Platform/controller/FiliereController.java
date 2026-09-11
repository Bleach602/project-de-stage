package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.FiliereDTO;
import IFFPO_Web_Platform.dto.SpecialiteDTO;
import IFFPO_Web_Platform.service.FiliereService;
import IFFPO_Web_Platform.service.SpecialiteService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard/filieres")
public class FiliereController {

    private final FiliereService filiereService;
    private final SpecialiteService specialiteService;

    public FiliereController(FiliereService filiereService, SpecialiteService specialiteService) {
        this.filiereService = filiereService;
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public String listeFiliere(@RequestParam(value = "filiereId", required = false)
                                   Long filiereId, Model model){

        List<FiliereDTO> filiereDTOS = filiereService.toutesFilieres();
        model.addAttribute("filieres", filiereDTOS);

        //DTOs VIDE POUR LES DEUX MODALS
        if (!model.containsAttribute("filiereDTO")){
            model.addAttribute("filiereDTO", new FiliereDTO());
        }

        if (!model.containsAttribute("specialiteDTO")){
            model.addAttribute("specialiteDTO", new SpecialiteDTO());
        }

        //RECHARGE DU PANNEAU DE DROITE SI ID PRESENT DANS L'URL
        if (filiereId != null){
            try {

                FiliereDTO filiereSelect = filiereService.trouverParId(filiereId);
                model.addAttribute("filiereSelectionnee", filiereSelect);

                List<SpecialiteDTO> specialites = specialiteService.recupererParFiliere(filiereId);
                model.addAttribute("specialites", specialites);

            } catch (RuntimeException e) {
                //Si l'Id est invalide, panneau vide sans faire planter la page
            }
        }

        return "admin/filiere/liste";
    }


    @PostMapping("/creer")
    public String creerFiliere(@Valid @ModelAttribute("filiereDTO") FiliereDTO filiereDTO,
                               BindingResult bindingResult, Model model,
                               RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){
            model.addAttribute("filieres", filiereService.toutesFilieres());
            return "admin/filiere/liste";
        }

        try {
            filiereService.creerFiliere(filiereDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Filière créée avec succès !");
        } catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard/filieres";
    }

    @GetMapping("/modifier/{id}")
    public String showFormUpdate(@PathVariable("id") Long id, Model model){
        try {
            FiliereDTO filiereDTO = filiereService.trouverParId(id);
            model.addAttribute("filiereDTO", filiereDTO);
            model.addAttribute("filiereId", id);
        } catch (Exception e) {
            return "redirect:/dashboard/filieres";
        }

        return "admin/filiere/updateFiliere";
    }

    @PostMapping("/modifier/{id}")
    public String formUpdate(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("filiereDTO") FiliereDTO filiereDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()){
            return "admin/filiere/updateFiliere";
        }

        try {
            filiereService.modifierFiliere(id, filiereDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Filière modifiée avec succès !");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard/filieres";
    }

    //BASCULER LE STATUT DE LA FILIERE
    @PostMapping("/{id}/changer-statut")
    public String changeStatut(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes){

        try {
            filiereService.changerStatut(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Le statut de la filière a été mis à jour !");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage());
        }

        return "redirect:/dashboard/filieres";
    }

    //DELETE FILIERE
    @PostMapping("/{id}/supprimer")
    public String deleteFiliere(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes){

        try {
            filiereService.supprimer(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cette filière et ses spécialités ont été supprimées.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Impossible de supprimer cette filière car elle est " +
                            " associée à une ou plusieurs sessions de candidature.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage());
        }

        return "redirect:/dashboard/filieres";
    }
}
