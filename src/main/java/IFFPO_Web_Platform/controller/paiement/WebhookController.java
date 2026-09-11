package IFFPO_Web_Platform.controller.paiement;

import IFFPO_Web_Platform.dto.paiement.WebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campay")
@RequiredArgsConstructor
public class WebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<Void> receive(
            @RequestBody WebhookRequest request){

        System.out.println(request);

        return ResponseEntity.ok().build();
    }



}