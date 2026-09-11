package IFFPO_Web_Platform.Exception;


import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Gère toutes les exceptions de l'application.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final UtilisateurRepository utilisateurRepository;

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("currentUser")
    public Utilisateur currentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return utilisateurRepository.findByEmail(authentication.getName()).orElse(null);
    }



    /**
     * Erreurs métier liées aux paiements.
     */
    @ExceptionHandler(PaymentException.class)
    public String handlePaymentException(PaymentException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";

    }

    /**
     * Paiement introuvable.
     */
    @ExceptionHandler(PaymentNotFoundException.class)
    public String handleNotFound(PaymentNotFoundException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";

    }

    /**
     * Erreurs CamPay.
     */
    @ExceptionHandler(CampayException.class)
    public String handleCampay(CampayException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";

    }



    @ExceptionHandler(EntityNotFoundException.class)
    public String handlerEntityNotFound(EntityNotFoundException ex, Model model){
        model.addAttribute("errorMessage",
                "La resource demandée n'existe pas ou a été supprimée");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handlerGlobalException(Exception ex, Model model){
        model.addAttribute("errorMessage",
                "Une erreur inattendue est survenue.");
        return "error/500";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handlerGlobalForbiden(Exception ex, Model model){
        model.addAttribute("errorMessage",
                "Vous n'avez pas le droit d'accéder à cette resource.");
        return "error/403";
    }

}
