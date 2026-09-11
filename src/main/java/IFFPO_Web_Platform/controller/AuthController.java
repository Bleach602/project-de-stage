package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.InscriptionDTO;
import IFFPO_Web_Platform.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/inscription")
    public String showLoginRegister(Model model){
        model.addAttribute("inscrireDTO", new InscriptionDTO());
        return "auth/login_register";
    }

    @PostMapping("/inscription/etape1")
    public String register(@Valid @ModelAttribute("inscrireDTO") InscriptionDTO dto,
                           BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()){
            return "auth/login_register";
        }

        try {

            authService.inscrire(dto);
            return "successPage";

        } catch (Exception e) {
            model.addAttribute("Erreur", e.getMessage());
            return "auth/login_register";
        }
    }

    @GetMapping("/login")
    public String backLoginRegister(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {

        model.addAttribute("inscrireDTO", new InscriptionDTO());

        return "auth/login_register";
    }


}
