package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.ActualiteDTO;
import IFFPO_Web_Platform.dto.FiliereDTO;
import IFFPO_Web_Platform.dto.FormateurDTO;
import IFFPO_Web_Platform.dto.Vitrine.ContactRequest;
import IFFPO_Web_Platform.dto.Vitrine.WhatsAppContactResponse;
import IFFPO_Web_Platform.service.ActualiteService;
import IFFPO_Web_Platform.service.FiliereService;
import IFFPO_Web_Platform.service.FormateurService;
import IFFPO_Web_Platform.service.Vitrine.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final FiliereService filiereService;
    private final ContactService contactService;
    private final FormateurService formateurService;
    private final ActualiteService actualiteService;

    public HomeController(FiliereService filiereService, ContactService contactService, FormateurService formateurService, ActualiteService actualiteService) {
        this.filiereService = filiereService;
        this.contactService = contactService;
        this.formateurService = formateurService;
        this.actualiteService = actualiteService;
    }

    @GetMapping("/")
    public String dashboard(Model model){

        List<FiliereDTO> filieres = filiereService.toutesFilieres();
        model.addAttribute("filieres", filieres);

        List<FormateurDTO> formateurs = formateurService.getFormateurs(null, 0, 4).getContent();
        model.addAttribute("formateurs", formateurs);

        List<ActualiteDTO> actualites = actualiteService.trouverActualites(0, 3, null).getContent();
        model.addAttribute("actualites", actualites);


        return "Home/index";
    }

    @GetMapping("/offres")
    public String offres(@RequestParam(required = false) Long filiereId, Model model){
        List<FiliereDTO> filieres = filiereService.toutesFilieres();

        if (filiereId != null) {
            filieres = filieres.stream()
                    .filter(f -> f.getId().equals(filiereId))
                    .collect(Collectors.toList());
            model.addAttribute("filiereFiltreeId", filiereId);
        }

        model.addAttribute("filieres", filieres);
        return "Home/offres";
    }

    @GetMapping("/archive")
    public String archives(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "3") int size,
                           @RequestParam(required = false) String motCle,
                           Model model){

        Page<ActualiteDTO> archives = actualiteService.trouverArchives(page, size, motCle);

        model.addAttribute("archives", archives);
        model.addAttribute("motCle", motCle);

        return "Home/archive";
    }

    @PostMapping("/api/contact/whatsapp")
    @ResponseBody
    public ResponseEntity<List<WhatsAppContactResponse>> envoyerWhatsApp(
            @RequestBody ContactRequest request
    ) {
        List<WhatsAppContactResponse> contacts =
                contactService.preparerMessagesWhatsApp(request);
        return ResponseEntity.ok(contacts);
    }
}