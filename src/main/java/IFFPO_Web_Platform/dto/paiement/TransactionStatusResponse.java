package IFFPO_Web_Platform.dto.paiement;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionStatusResponse {

    private String reference;

    @JsonProperty("external_reference")
    private String externalReference;

    private String status;

    private BigDecimal amount;

    private String currency;

    private String operator;

    private String code;

    @JsonProperty("operator_reference")
    private String operatorReference;

}