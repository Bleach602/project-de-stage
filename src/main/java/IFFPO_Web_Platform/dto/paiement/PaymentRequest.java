package IFFPO_Web_Platform.dto.paiement;



import IFFPO_Web_Platform.entity.enums.MobileMoneyOperator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class PaymentRequest {



        @NotBlank(message = "Le numéro est obligatoire")
        private String phoneNumber;

        @NotNull(message = "Veuillez choisir un opérateur")
        private MobileMoneyOperator operator;

        @NotNull(message = "Le montant est obligatoire")
//        @DecimalMin(value = "100", message = "Le montant minimum est de 100 FCFA")
        private BigDecimal amount;


}
