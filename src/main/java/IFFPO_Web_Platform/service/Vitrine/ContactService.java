package IFFPO_Web_Platform.service.Vitrine;

import IFFPO_Web_Platform.dto.Vitrine.ContactRequest;
import IFFPO_Web_Platform.dto.Vitrine.WhatsAppContactResponse;

import java.util.List;

public interface ContactService {

    List<WhatsAppContactResponse> preparerMessagesWhatsApp(
            ContactRequest request
    );
}
