package IFFPO_Web_Platform.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "campay")
public class CampayProperties {

    private String baseUrl;
    private String username;
    private String password;

}
