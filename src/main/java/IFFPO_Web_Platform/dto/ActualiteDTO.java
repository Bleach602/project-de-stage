package IFFPO_Web_Platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualiteDTO {

    private Long id;

    private MultipartFile image;

    private String imagePath;

    @NotBlank(message = "Veuillez ajouter un titre")
    private String titre;

    @NotBlank(message = "Veuillez ajouter une description")
    private String description;

    private LocalDateTime datePublication;

    private LocalDateTime dateEvenement;

    private LocalDateTime dateEXpiration;

    // Pour l'affichage
    private String photoUrl;  //  U
}
