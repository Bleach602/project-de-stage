package IFFPO_Web_Platform.controller.Candidat;


import IFFPO_Web_Platform.dto.FicheInscriptionDTO;
import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.repository.FicheInscriptionRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.FicheInscriptionService;
import IFFPO_Web_Platform.service.PdfGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("candidat")
public class FicheInscriptionController {

    private final FicheInscriptionService ficheService;
    private final UtilisateurRepository utilisateurRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private  final FicheInscriptionRepository ficheRepository;



    /**
     * Enregistrement de la fiche
     * d'inscription.
     */
   @PostMapping("/fiche/save")
   public String enregistrer(
           @Valid @ModelAttribute("fiche") FicheInscriptionDTO dto,
           BindingResult bindingResult,
           Authentication authentication,
           RedirectAttributes redirectAttributes) {

       try{

           ficheService.enregistrer(
                    dto,
                  authentication.getName());

           redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Votre fiche d'inscription a été enregistrée avec succès."
                            + "Vous pouvez désormais la télécharger et l'utiliser pour déposer votre candidature.");


       }

       catch(Exception e){
           redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
       }

       return "redirect:/candidat/dashboard";
   }



    @GetMapping("/attestation/maFiche")
    public void maFiche(Authentication authentication,
                        HttpServletResponse response,  RedirectAttributes redirectAttributes) {

       try{
           Utilisateur utilisateur = utilisateurRepository
                   .findByEmail(authentication.getName())
                   .orElseThrow(() ->
                           new RuntimeException("Utilisateur introuvable."));

           System.out.println("====== TELECHARGEMENT ======");

           pdfGeneratorService.telechargerPdf(utilisateur, response);


       }
       catch(Exception e){
           redirectAttributes.addFlashAttribute(
                   "errorMessage",
                   "Vous n'avez pas encore generer votre fiche ! veuillez " +
                           " cliquer sur le menu ma fiche ");
       }

    }




}

