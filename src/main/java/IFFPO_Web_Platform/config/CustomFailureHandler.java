package IFFPO_Web_Platform.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        String code;

        if (exception instanceof LockedException) {

            code = "compte_bloque";

        } else if (exception instanceof DisabledException) {

            code = "compte_inactif";

        } else {

            code = "identifiants_invalides";
        }


        response.sendRedirect("/login?error=" + code);
    }
}