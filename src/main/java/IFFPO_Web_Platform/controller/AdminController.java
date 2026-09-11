package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.SessionCandidatureDTO;
import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.repository.CandidatureRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.SessionCandidatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminController {

    private final CandidatureRepository candidatureRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SessionCandidatureService sessionCandidatureService;

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "0") int page, Model model) {

        // 1. Statistiques globales des cartes
        model.addAttribute("totalCandidatures", candidatureRepository.count());
        model.addAttribute("candidaturesEnAttente", candidatureRepository.countByStatutCandidature(StatutCandidature.EN_ATTENTE));
        model.addAttribute("totalUtilisateurs", utilisateurRepository.count());

        // 2. Session Active
        SessionCandidatureDTO activeSession = sessionCandidatureService.getSessionActive();
        model.addAttribute("activeSession", activeSession);

        // 3. Liste Paginée des candidatures récents (5 par page)
        Page<Candidature> candidaturesPage = candidatureRepository.findAll(
            PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id")));

        model.addAttribute("candidatures", candidaturesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", candidaturesPage.getTotalPages());

        return "admin/dashboard";
    }

}
