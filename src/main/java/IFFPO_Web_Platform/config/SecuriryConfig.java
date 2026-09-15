package IFFPO_Web_Platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecuriryConfig {

    private final CustomUserDetailService customUserDetailService;
    private final CustomFailureHandler customFailureHandler;
    private final CustomSuccessHandler successHandler;

    public SecuriryConfig(CustomUserDetailService customUserDetailService, CustomFailureHandler customFailureHandler, CustomSuccessHandler successHandler) {
        this.customUserDetailService = customUserDetailService;
        this.customFailureHandler = customFailureHandler;
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/contact/whatsapp"))

                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/","/offres", "/inscription", "/inscription/etape1",
                                "/img/**", "/css/**", "/js/**","/archive","/robots.txt","/orientation",
                                "/login","/api/contact/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/candidat/**").hasRole("CANDIDAT")

                        .anyRequest().authenticated())
                .formLogin(form-> form
                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureHandler(customFailureHandler).permitAll())
                .logout(logout->logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?deconnexion=true").permitAll()
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())

                .userDetailsService(customUserDetailService);

        return httpSecurity.build();

    }
}
