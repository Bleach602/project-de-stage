package IFFPO_Web_Platform.client;



import IFFPO_Web_Platform.Exception.CampayException;
import IFFPO_Web_Platform.dto.paiement.TransactionStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service

public class CampayClient {

    private final RestClient restClient;

    private final CampayProperties properties;

    public CampayClient(RestClient restClient, CampayProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public String authenticate() {

        AuthRequest request = new AuthRequest(
                properties.getUsername(),
                properties.getPassword()
        );

        AuthResponse response = restClient.post()

                .uri(properties.getBaseUrl() + "/token/")

                .body(request)

                .retrieve()

                .body(AuthResponse.class);

        if (response == null || response.getToken() == null) {

            throw new CampayException("Impossible d'obtenir le token CamPay.");

        }

        System.out.println("========== TOKEN ==========");
        System.out.println(response.getToken());

        return response.getToken();

    }

    /**
     * Initialise une demande de paiement Mobile Money.
     */



    public CollectResponse collectPayment(CollectRequest request) {

        // Récupération du token
        String token = authenticate();

        System.out.println("Authorization: Token " + token);

        String json = restClient.post()
                .uri(properties.getBaseUrl() + "/collect/")
                .header("Authorization", "Token " + token)
                .body(request)
                .retrieve()
                .body(String.class);

        System.out.println("========== JSON CAMPAY ==========");
        System.out.println(json);





        return restClient.post()

                .uri(properties.getBaseUrl() + "/collect/")

                // Authentification Bearer
                .header("Authorization", "Token " + token)

                .body(request)

                .retrieve()

                .body(CollectResponse.class);

    }

    /**
     * Vérifie le statut d'une transaction CamPay.
     *
     * @param reference référence CamPay
     * @return état de la transaction
     */
    public TransactionStatusResponse checkTransactionStatus(String reference) {

        String token = authenticate();

        return restClient.get()

                .uri(properties.getBaseUrl() + "/transaction/" + reference + "/")

                .header("Authorization", "Token " + token)

                .retrieve()

                .body(TransactionStatusResponse.class);

    }




}
