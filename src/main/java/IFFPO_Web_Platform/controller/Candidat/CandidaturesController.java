package IFFPO_Web_Platform.controller.Candidat;


import IFFPO_Web_Platform.service.CandidatureService;
import IFFPO_Web_Platform.service.DocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/candidat")
public class CandidaturesController {

    private final CandidatureService candidatureService;
    private final DocumentService documentService;


    /**
     * Dépôt d'une candidature.
     *
     * Le candidat envoie uniquement ses pièces
     * justificatives. Toutes les autres informations
     * (spécialité, fiche, utilisateur...) sont
     * récupérées automatiquement.
     */
    @PostMapping("/save")
    public String deposerCandidature(

            Authentication authentication,

            @RequestParam("cni") MultipartFile cni,

            @RequestParam(value = "diplome",
                    required = false)
            MultipartFile diplome,

            @RequestParam("acte") MultipartFile acte,

            RedirectAttributes redirectAttributes) {

        try {

            candidatureService.deposerCandidature(

                    authentication.getName(),

                    cni,

                    diplome,

                    acte);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Votre candidature a été enregistrée avec succès.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/candidat/dashboard";
    }




    @PostMapping("/document/remplacer")
    public String remplacerDocument(

            @RequestParam Long documentId,

            @RequestParam MultipartFile fichier,

            RedirectAttributes redirectAttributes) {

        try {

            documentService.remplacerDocument(
                    documentId,
                    fichier);

            redirectAttributes.addFlashAttribute(
                    "successMessage",

                    "Document modifié avec succès.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",

                    e.getMessage());

        }

        return "redirect:/candidat/dashboard";
    }

    @GetMapping("/document/{id}/voir")
    public ResponseEntity<Resource> voirDocument(
            @PathVariable Long id) {

        return documentService.voirDocument(id);

    }
}
