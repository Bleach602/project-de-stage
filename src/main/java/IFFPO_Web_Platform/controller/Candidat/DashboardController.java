package IFFPO_Web_Platform.controller.Candidat;

import IFFPO_Web_Platform.client.CampayClient;
import IFFPO_Web_Platform.dto.InscriptionDTO;
import IFFPO_Web_Platform.dto.NotificationDTO;
import IFFPO_Web_Platform.dto.paiement.PaymentRequest;
import IFFPO_Web_Platform.entity.Recu;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.service.*;
import IFFPO_Web_Platform.service.Cloudinary.CloudinaryService;
import IFFPO_Web_Platform.service.paaiement.PaiementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("candidat")
@Controller
public class DashboardController {

    private final PaiementService paymentService;
    private final RecuGeneratorService recuGeneratorService;
    private final CampayClient campayClient;
    private final CandidatService candidatService;
    private final AuthService authService;
    private final SessionCandidatureService sessionCandidatureService;
    private final FicheInscriptionService ficheInscriptionService;
    private final SpecialiteService specialiteService;
    private final FiliereService filiereService;
    private final DocumentService documentService;
    private final CandidatureService candidatureService;
    private final NotificationService notificationService;
    private final UtilisateurService utilisateurService;
    private CloudinaryService cloudinaryService;



    @GetMapping ("/dashboard")
    public String dashboard(Model model, Principal princial,
                                Authentication authentication){

        String email = authentication.getName();
        Utilisateur candidat = candidatService.findByEmail(email);

        //on vérifie si la sesion est diponible
        if(!sessionCandidatureService.isSessionOuverte()){
            return "Dashboard/session-fermee";
        }

        //on vérifie si le user a dejà créer une fiche ou pas
        boolean ficheExiste = ficheInscriptionService.ficheExiste(authentication.getName());


        model.addAttribute("ficheExiste", ficheExiste);
        model.addAttribute("Candidat", candidat);
        model.addAttribute("Filiere",filiereService.getAllFiliere());
        model.addAttribute("specialites",specialiteService.getAll());
        model.addAttribute(
                "documents",
                documentService.getDocuments(authentication.getName()));

        model.addAttribute(
                "candidatureValidee",
                candidatureService.candidatureValidee(authentication.getName()));

        model.addAttribute(
                "notifications",
                notificationService.getNotifications(authentication.getName()));

        long nombreNotifications =
                notificationService
                        .getNotifications(authentication.getName())
                        .stream()
                        .filter(NotificationDTO::isNouvelle)
                        .count();

        model.addAttribute(
                "nombreNotifications",
                nombreNotifications);


        candidatureService.findDerniereCandidature(email)
                        .ifPresent(c -> model.addAttribute("candidature", c));


        model.addAttribute("paiement", paymentService.getDernierPaiement(authentication.getName()));

        model.addAttribute("payment", new PaymentRequest());

        return "Dashboard/Candidat";
        }

        @PostMapping("/profile/update")
        public String UpdateProfil( @ModelAttribute InscriptionDTO inscriptionDTO,
                                   BindingResult bindingResult, Model model,
                                   RedirectAttributes redirectAttributes,Authentication authentication){
            if (bindingResult.hasErrors()){
                return "auth/login_register";
            }

            try {
                String email = authentication.getName();
               authService.UpdateProfile(inscriptionDTO, email);

               redirectAttributes.addFlashAttribute("successMessage","Votre profil vient d'être" +
                       " mis à jour! Merci");
                return "redirect:/candidat/dashboard";

            } catch (Exception e) {
                model.addAttribute("Erreur", e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage","Votre profil n'a pas pu être" +
                        " mis à jour!");
                return "auth/login_register";
            }



        }



    /**
     * Redirection vers WhatsApp administration.
     */
   @PostMapping("/contact-whatsapp")
    public String contacterWhatsapp(

            @RequestParam String sujet,

            @RequestParam String message,

            Authentication authentication

    ){


        /*
         * Récupération du candidat connecté.
         */
        Utilisateur utilisateur =
                candidatService
                        .findByEmail(authentication.getName());

        /*
         * Numéro WhatsApp administration.
         *
         * Format international sans +
         */

       List<Utilisateur> gest = utilisateurService.trouverPermisions_Gest_Candidat_LimitDEUX();

        String numeroAdmin = gest.get(0).getTelephone();

       if (!numeroAdmin.startsWith("237")) {
           numeroAdmin = "237" + numeroAdmin;
       }


       /*
         * Construction du message.
         */
        String texte =

                "Bonjour Administration IFP-PERLE D'OR\n\n"

                        +"Nom : "
                        + utilisateur.getNom()
                        +" "
                        + utilisateur.getPrenom()
                        +"\n"

                        +"Email : "
                        + utilisateur.getEmail()
                        +"\n\n"

                        +"Sujet : "
                        + sujet
                        +"\n\n"

                        +"Message : "
                        + message;



        /*
         * Encodage URL.
         */
        String url =

                "https://wa.me/"

                        + numeroAdmin

                        +"?text="

                        + URLEncoder.encode(
                        texte,
                        StandardCharsets.UTF_8);



        return "redirect:"+url;

    }

    @GetMapping("/recu/{id}")
    public ResponseEntity<byte[]> telecharger(
            @PathVariable Long id
    ) {

        Recu recu =
                recuGeneratorService.GetById(id);


        byte[] contenu =
                recuGeneratorService
                        .telechargerRecuParId(id);


        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + recu.getDocument().getNomFichier()
                                + "\""
                )

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .contentLength(
                        contenu.length
                )

                .body(
                        contenu
                );
    }
}
