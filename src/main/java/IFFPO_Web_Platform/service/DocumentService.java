package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.DocumentDTO;
import IFFPO_Web_Platform.entity.Document;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    /**
     * Retourne tous les documents de la candidature
     * du candidat connecté.
     */
    List<DocumentDTO> getDocuments(String email);

    /**
     * Remplace un document existant par un nouveau fichier.
     *
     * L'ancien fichier est supprimé de Cloudinary
     * puis le nouveau fichier est téléversé.
     */
    void remplacerDocument(
            Long documentId,
            MultipartFile nouveauFichier
    ) throws IOException;

    /**
     * Affiche un document dans le navigateur.
     *
     * Le document est récupéré depuis Cloudinary.
     */
    ResponseEntity<Resource> voirDocument(Long id);


    //pour ce  qui est de la gestion des images pour les formateurs et actulités


    @Transactional
    Document remplacerPhotoFormateur_and_Actualite(
            Long documentId,
            MultipartFile nouveauFichier
    ) throws IOException;

    @Transactional
    Document remplacerPhoto_Actualite(
            Long documentId,
            MultipartFile nouveauFichier
    ) throws IOException;

}