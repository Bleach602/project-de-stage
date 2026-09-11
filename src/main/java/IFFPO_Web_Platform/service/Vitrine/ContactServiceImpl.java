package IFFPO_Web_Platform.service.Vitrine;

import IFFPO_Web_Platform.dto.Vitrine.ContactRequest;
import IFFPO_Web_Platform.dto.Vitrine.WhatsAppContactResponse;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl  implements ContactService{

    private static final String PERMISSION_GESTION_CONTENU =
            "PERM_GESTION_CONTENU";


    private final UtilisateurRepository utilisateurRepository;

    @Override
    public List<WhatsAppContactResponse> preparerMessagesWhatsApp(
            ContactRequest request
    ) {

        List<Utilisateur> administrateurs =
                utilisateurRepository
                        .findUtilisateursAvecPermission(
                                PERMISSION_GESTION_CONTENU
                        )
                        .stream()
                        .limit(2)
                        .toList();

        String message = construireMessage(request);

        return administrateurs.stream()
                .map(admin -> {

                    String telephone =
                            normaliserNumero(admin.getTelephone());

                    if (telephone == null) {
                        return null;
                    }

                    String url =
                            "https://wa.me/"
                                    + telephone
                                    + "?text="
                                    + URLEncoder.encode(
                                    message,
                                    StandardCharsets.UTF_8
                            );

                    return new WhatsAppContactResponse(
                            admin.getNom(),
                            admin.getPrenom(),
                            admin.getTelephone(),
                            url
                    );
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private String construireMessage(ContactRequest request) {

        return """
                Bonjour IFP-PERLE D'OR 

                Vous avez reçu un nouveau message depuis la vitrine du site.

                Nom : %s
                Prénom : %s

                 Email : %s

                 Objet : %s

                 Filière visée :
                %s

                Message :
                %s

                ---
                Message envoyé depuis la vitrine IFP-PO
                Les Perles d'Or.
                """.formatted(
                valeur(request.getNom()),
                valeur(request.getPrenom()),
                valeur(request.getEmail()),
                valeur(request.getObjet()),
                valeur(request.getFiliere()),
                valeur(request.getMessage())
        );
    }

    private String normaliserNumero(String telephone) {

        if (telephone == null || telephone.isBlank()) {
            return null;
        }

        String numero =
                telephone.replaceAll("[^0-9+]", "");

        if (numero.startsWith("+237")) {
            return numero.substring(1);
        }

        if (!numero.startsWith("237")) {
            return "237" + numero;
        }


        return numero;
    }

    private String valeur(String valeur) {

        return valeur == null || valeur.isBlank()
                ? "Non renseigné"
                : valeur.trim();
    }
}
