package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import org.springframework.web.multipart.MultipartFile;


  // elle créer l'entité Document
public interface FileStorageService {

    /**
     * Enregistre une photo d'identité.

     */

    Document savePhoto(MultipartFile file);

      /**
       * Enregistre un document sur le disque et en base de données.
       */
      Document saveDocument(
              MultipartFile file,
              TypeDocument typeDocument,
              String dossier);
}
