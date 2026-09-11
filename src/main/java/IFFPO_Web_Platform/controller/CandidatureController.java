package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.SessionCandidature;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.repository.FiliereRepository;
import IFFPO_Web_Platform.repository.SessionRepository;
import IFFPO_Web_Platform.repository.SpecialiteRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.CandidatureService;
import IFFPO_Web_Platform.service.DocumentService;
import IFFPO_Web_Platform.service.PdfGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/candidatures")
@PreAuthorize("hasAuthority('PERM_GESTION_CANDIDATURE')")
public class CandidatureController {
    private static final int TAILLE_PAGE = 10;

    private final CandidatureService candidatureService;
    private final SessionRepository sessionRepository;
    private final SpecialiteRepository specialiteRepository;
    private final FiliereRepository filiereRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PdfGeneratorService pdfGeneratorService;

    public CandidatureController(CandidatureService candidatureService, SessionRepository sessionRepository,
                                 SpecialiteRepository specialiteRepository, FiliereRepository filiereRepository, UtilisateurRepository utilisateurRepository, PdfGeneratorService pdfGeneratorService) {
        this.candidatureService = candidatureService;
        this.sessionRepository = sessionRepository;
        this.specialiteRepository = specialiteRepository;
        this.filiereRepository = filiereRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) Long sessionId,
                        @RequestParam(required = false) StatutCandidature statut,
                        @RequestParam(required = false) Long filiereId,
                        @RequestParam(required = false) Long specialiteId,
                        @RequestParam(defaultValue = "0") int page,
                        Model model){

        Pageable pageable = PageRequest.of(page, TAILLE_PAGE);
        Page<Candidature> resultat = candidatureService.listerAvecFiltres(sessionId, statut,
                filiereId, specialiteId ,pageable);

        model.addAttribute("candidatures", resultat);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("statut", statut);
        model.addAttribute("filiereId", filiereId);
        model.addAttribute("specialiteId", specialiteId);
        model.addAttribute("toutesLesSessions", sessionRepository.findAll());
        model.addAttribute("tousLesStatuts", StatutCandidature.values());
        model.addAttribute("toutesLesFilieres", filiereRepository.findAll());
        model.addAttribute("toutesLesSpecialites", specialiteRepository.findAll());

        return "admin/candidatures/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){
        model.addAttribute("candidature", candidatureService.trouverParId(id));

        return "admin/candidatures/detail";
    }


    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id, RedirectAttributes redirectAttributes){

        try {
            String whatsappUrl  =   candidatureService.valider(id);

            redirectAttributes.addFlashAttribute("succes",
                    "Cette candidature a été validée avec succès.");

            return "redirect:" + whatsappUrl;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    e.getMessage());
        }

        return "redirect:/dashboard/candidatures/" + id;

    }

    @PostMapping("/{id}/rejeter")
    public String rejeter(@PathVariable Long id, @RequestParam String motifRejet,
                          RedirectAttributes redirectAttributes){

        try {

            String whatsappUrl = candidatureService.rejeter(id, motifRejet);

            redirectAttributes.addFlashAttribute("succes",
                    "Candidature rejetée.");

            return "redirect:" + whatsappUrl;

        } catch (Exception e){
            redirectAttributes.addFlashAttribute("erreur",
                    e.getMessage());
        }
        return "redirect:/dashboard/candidatures/" + id;

    }

    @PostMapping("/{id}/demander-correction")
    public String demanderCorrection(@PathVariable Long id, @RequestParam String messageCorrection,
                                     RedirectAttributes redirectAttributes){

        try {
            String whatsappUrl  = candidatureService.demanderCorrection(id, messageCorrection);

            redirectAttributes.addFlashAttribute("succes",
                    "Demande de correction envoyée au candidat");

            return "redirect:" + whatsappUrl;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    e.getMessage());
        }

        return "redirect:/dashboard/candidatures/" + id;
    }



    // telecharger la fiche du candiadt
    @GetMapping("{id}/fiche/pdf")
    public void maFiche( @PathVariable Long id,
                        HttpServletResponse response, RedirectAttributes redirectAttributes) {

        try{

            Candidature candidature = candidatureService.trouverParId(id);

            FicheInscription fiche = candidature.getFicheInscription();

            if (fiche == null){
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Pas de fiche associée à cette candidature");
                return;
            }

            pdfGeneratorService.telechargerPdf(candidature.getUtilisateur(), response);


        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Vous n'avez pas encore generer votre fiche ! veuillez " +
                            " cliquer sur le menu ma fiche ");
        }

    }

}
