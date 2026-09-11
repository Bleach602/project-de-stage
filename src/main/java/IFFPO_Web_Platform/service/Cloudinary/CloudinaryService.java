package IFFPO_Web_Platform.service.Cloudinary;

import IFFPO_Web_Platform.dto.cloudinary.CloudinaryResponse;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {

    /**
     * Upload d'un fichier MultipartFile vers Cloudinary.
     */
    CloudinaryResponse uploadFile(
            MultipartFile file,
            TypeDocument typeDocument
    ) throws IOException;


    /**
     * Upload d'un fichier byte[] vers Cloudinary.
     *
     * pour nos pdf génré via  OpenPDF.
     */
    CloudinaryResponse uploadFile(
            byte[] content,
            String filename,
            TypeDocument typeDocument
    ) throws IOException;


    /**
     * Télécharge le contenu d'un fichier depuis Cloudinary.
     */
    byte[] downloadFile(
            String publicId,
            String resourceType
    ) throws IOException;


    /**
     * Supprime un fichier Cloudinary.
     */
    void deleteFile(
            String publicId,
            String resourceType
    ) throws IOException;


    /**
     * Génère une URL signée Cloudinary.
     */
    String generateSignedUrl(
            String publicId,
            String resourceType
    );
}