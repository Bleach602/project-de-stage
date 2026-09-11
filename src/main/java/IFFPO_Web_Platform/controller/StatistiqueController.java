package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.StatistiqueGraphiquesDTO;
import IFFPO_Web_Platform.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dashboard/statistiques")
@PreAuthorize("hasAuthority('PERM_VOIR_STATISTIQUES')")
@RequiredArgsConstructor
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    @GetMapping
    public String afficherStatistiques(Model model) {

        model.addAttribute("statistiques", statistiqueService.calculerStatistiques());
        return "admin/statistiques/statistiques";
    }

    @GetMapping("/data")
    @ResponseBody
    public StatistiqueGraphiquesDTO obtenirDonneesGraphiques(@RequestParam(value = "periode", defaultValue = "annee") String periode) {

        return statistiqueService.calculerGraphiques(periode);
    }
}
