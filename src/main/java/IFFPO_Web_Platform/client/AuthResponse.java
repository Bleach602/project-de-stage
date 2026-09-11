package IFFPO_Web_Platform.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class AuthResponse {

    @JsonProperty("token")
    private String token;
}
