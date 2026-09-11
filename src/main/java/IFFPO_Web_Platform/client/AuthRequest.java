package IFFPO_Web_Platform.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class AuthRequest {
    private String username;
    private String password;
}
