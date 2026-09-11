package IFFPO_Web_Platform.dto.cloudinary;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contient les informations utiles renvoyées
 * par Cloudinary après un upload.

 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryResponse {

    /**
     * Identifiant unique du fichier chez Cloudinary.
     */
    private String publicId;

    /**
     * URL HTTPS permettant d'accéder au document.
     */
    private String secureUrl;

    /**
     * Type de ressource Cloudinary.
     *
     * image
     * raw
     * video
     */
    private String resourceType;

    /**
     * Nom original du fichier envoyé.
     */
    private String originalFilename;

    /**
     * Extension ou format du fichier.
     *
     * jpg
     * png
     * pdf
     */
    private String format;

}