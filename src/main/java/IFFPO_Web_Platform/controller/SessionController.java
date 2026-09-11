package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.SessionCandidatureDTO;
import IFFPO_Web_Platform.service.FiliereService;
import IFFPO_Web_Platform.service.SessionCandidatureService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard/sessions")
public class SessionController {

    private final SessionCandidatureService sessionCandidatureService;
    private final FiliereService filiereService;

    public SessionController(SessionCandidatureService sessionCandidatureService, FiliereService filiereService) {
        this.sessionCandidatureService = sessionCandidatureService;
        this.filiereService = filiereService;
    }

    @GetMapping
    public String listeSessions(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "3") int size,
                                Model model) {

        Page<SessionCandidatureDTO> sessionPage = sessionCandidatureService.findSessionPagined(page, size);

        model.addAttribute("sessionPage", sessionPage);
        model.addAttribute("sessions", sessionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sessionPage.getTotalPages());

        // DTOs séparés pour la création et la modification
        if (!model.containsAttribute("createSessionDTO")) {
            model.addAttribute("createSessionDTO", new SessionCandidatureDTO());
        }
        if (!model.containsAttribute("editSessionDTO")) {
            model.addAttribute("editSessionDTO", new SessionCandidatureDTO());
        }

        model.addAttribute("toutesLesFilieres", filiereService.toutesFilieres());

        return "admin/session/list";
    }

    @PostMapping("/creer")
    public String saveSession(@Valid @ModelAttribute("createSessionDTO") SessionCandidatureDTO dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.createSessionDTO", bindingResult);
            redirectAttributes.addFlashAttribute("createSessionDTO", dto);
            redirectAttributes.addFlashAttribute("showCreateModal", true);
            return "redirect:/dashboard/sessions";
        }

        try {
            sessionCandidatureService.createSessionCandidature(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Une nouvelle session a été ajoutée.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("createSessionDTO", dto);
            redirectAttributes.addFlashAttribute("showCreateModal", true);
        }
        return "redirect:/dashboard/sessions";
    }

    @GetMapping("/modifier/{id}")
    public String showUpdateForm(@PathVariable Long id,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "3") int size,
                                 Model model){

        Page<SessionCandidatureDTO> sessionPage = sessionCandidatureService.findSessionPagined(page, size);
        model.addAttribute("sessionPage", sessionPage);
        model.addAttribute("sessions", sessionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sessionPage.getTotalPages());

        //CHARGER DTO
        SessionCandidatureDTO sessionDTO = sessionCandidatureService.trouverParId(id);

        model.addAttribute("editSessionDTO", sessionDTO);
        model.addAttribute("createSessionDTO", new SessionCandidatureDTO());
        model.addAttribute("showEditModal", true);
        model.addAttribute("toutesLesFilieres", filiereService.toutesFilieres());

        return "admin/session/list";
    }

    @PostMapping("/modifier/{id}")
    public String updateSession(@PathVariable Long id,
                                @Valid @ModelAttribute("editSessionDTO") SessionCandidatureDTO dto,
                                BindingResult bindingResult,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "3") int size,
                                Model model,
                                RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){

            Page<SessionCandidatureDTO> sessionPage = sessionCandidatureService.findSessionPagined(page, size);

            model.addAttribute("sessionPage", sessionPage);
            model.addAttribute("sessions", sessionPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sessionPage.getTotalPages());

            model.addAttribute("createSessionDTO", new SessionCandidatureDTO());
            model.addAttribute("toutesLesFilieres", filiereService.toutesFilieres());
            model.addAttribute("showEditModal", true);
            return "admin/session/list";
        }

        try {
            sessionCandidatureService.updateSessionCandidature(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Session modifiée avec succès !");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/sessions";
        }
        return "redirect:/dashboard/sessions";
    }

    @GetMapping("/open/{id}")
    public String openSession(@PathVariable Long id){
        sessionCandidatureService.openSessionCandidature(id);
        return "redirect:/dashboard/sessions";
    }

    @GetMapping("/close/{id}")
    public String closeSession(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            sessionCandidatureService.closeSessionCandidature(id);
            redirectAttributes.addFlashAttribute("successMessage", "La session a été fermée avec succès.");
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/sessions";
    }
}