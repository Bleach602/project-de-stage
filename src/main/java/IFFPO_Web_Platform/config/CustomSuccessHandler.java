package IFFPO_Web_Platform.config;

import IFFPO_Web_Platform.entity.Utilisateur;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Utilisateur utilisateur = customUserDetails.getUtilisateur();

        if ("ROLE_CANDIDAT".equals(utilisateur.getRole().getIntitule())){
            response.sendRedirect("/candidat/dashboard");
        }

//        else if ("ROLE_ADMIN".equals(utilisateur.getRole().getIntitule())){
//            response.sendRedirect("/admin/dashboard");
//        }

        else {
            response.sendRedirect("/admin/dashboard");
        }
    }
}
