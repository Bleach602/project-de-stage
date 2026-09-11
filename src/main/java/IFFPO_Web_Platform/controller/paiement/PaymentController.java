package IFFPO_Web_Platform.controller.paiement;


import IFFPO_Web_Platform.client.CampayClient;
import IFFPO_Web_Platform.dto.paiement.PaymentRequest;
import IFFPO_Web_Platform.dto.paiement.PaymentStatusResponse;
import IFFPO_Web_Platform.dto.paiement.TransactionStatusResponse;
import IFFPO_Web_Platform.entity.Paiement;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import IFFPO_Web_Platform.service.CandidatService;
import IFFPO_Web_Platform.service.paaiement.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("Paiement")
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger logger =
            LoggerFactory.getLogger(PaymentController.class);

    private final PaiementService paymentService;
    private final CampayClient campayClient;
    private final CandidatService candidatService;

    @PostMapping("/pay")
    public String pay(@Valid @ModelAttribute("payment") PaymentRequest request,
                      BindingResult result,
                      Model model, Authentication authentication) {

        if (result.hasErrors()) {
            return "Dashboard/Candidat";
        }


        logger.info(
                "Nouvelle demande de paiement de {} pour {} XAF",
                request.getPhoneNumber(),
                request.getAmount()
        );

        String email = authentication.getName();
        Utilisateur candidat = candidatService.findByEmail(email);

        Paiement payment = paymentService.initiatePayment(request,authentication);

        model.addAttribute("paymentResult", payment);
        model.addAttribute("Candidat", candidat);

        return "Dashboard/success";
    }


    @GetMapping("/status")
    @ResponseBody
    public String status() {

        TransactionStatusResponse response =
                campayClient.checkTransactionStatus(
                        "76d4a8e5-adbd-49de-96b8-37da537995d7"
                );

        return response.toString();
    }

    @GetMapping("/update-status")
    @ResponseBody
    public String updateStatus() {

        Paiement paiement = paymentService.updatePaymentStatus(
                "76d4a8e5-adbd-49de-96b8-37da537995d7"
        );

        return paiement.toString();
    }

    @GetMapping("/status/{reference}")
    @ResponseBody
    public PaymentStatusResponse getStatus(
            @PathVariable String reference) {

        PaymentStatus status =
                paymentService.getPaymentStatus(reference);

        System.out.println("Status envoyé au navigateur : " + status);

        return new PaymentStatusResponse(reference, status);

    }
}


