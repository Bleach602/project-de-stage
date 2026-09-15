package IFFPO_Web_Platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebPlatformApplication.class, args);
	}

}
