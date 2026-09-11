package IFFPO_Web_Platform.dto.paiement;


import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentStatusResponse

{
    private String reference;

    private PaymentStatus status;
}
