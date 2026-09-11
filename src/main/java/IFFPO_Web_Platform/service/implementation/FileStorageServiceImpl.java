package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.cloudinary.CloudinaryResponse;
import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.DocumentRepository;
import IFFPO_Web_Platform.service.Cloudinary.CloudinaryService;
import IFFPO_Web_Platform.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implémentation du service de stockage des documents.
 *
 * IMPORTANT :
 *
 * Cette classe ne sauvegarde plus les fichiers sur le disque local.
 *
 *
 * Nouvelle architecture :
 *
 * MultipartFile
 *      ↓
 * CloudinaryService
 *      ↓
 * Cloudinary
 *      ↓
 * CloudinaryResponse
 *      ↓
 * Document
 *      ↓
 * PostgreSQL
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl
        implements FileStorageService {


    /**
     * Repository permettant de sauvegarder les métadonnées
     * du document dans PostgreSQL.
     */
    private final DocumentRepository documentRepository;


    /**
     * Service responsable de la communication avec Cloudinary.
     */
    private final CloudinaryService cloudinaryService;

    @Override
    public Document savePhoto(
            MultipartFile file
    ) {

        /*
         * Vérification du fichier.
         */
        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Aucune photo d'identité sélectionnée."
            );
        }


        try {

            log.info(
                    "Upload de la photo d'identité vers Cloudinary : {}",
                    file.getOriginalFilename()
            );


            /*
             * =====================================================
             * 1. UPLOAD CLOUDINARY
             * =====================================================
             */
            CloudinaryResponse response =
                    cloudinaryService.uploadFile(
                            file,
                            TypeDocument.PHOTO_IDENTITE
                    );


            /*
             * =====================================================
             * 2. CRÉATION DU DOCUMENT
             * =====================================================
             */
            Document document =
                    new Document();


            document.setTypeDocument(
                    TypeDocument.PHOTO_IDENTITE
            );


            /*
             * Nom original du fichier.
             */
            document.setNomFichier(
                    file.getOriginalFilename()
            );


            /*
             * URL sécurisée Cloudinary.
             */
            document.setUrl(
                    response.getSecureUrl()
            );


            /*
             * Identifiant unique Cloudinary.
             *
             * Il sera notamment utilisé pour :
             *
             * - supprimer le fichier ;
             * - télécharger le fichier ;
             * - récupérer la photo pour le PDF.
             */
            document.setPublicId(
                    response.getPublicId()
            );


            /*
             * Type de ressource Cloudinary.
             *
             * Pour une photo :
             *
             * image
             */
            document.setResourceType(
                    response.getResourceType()
            );


            /*
             * =====================================================
             * 3. SAUVEGARDE EN BASE
             * =====================================================
             */
            Document documentSauvegarde =
                    documentRepository.save(document);


            log.info(
                    "Photo enregistrée avec succès. publicId={}",
                    documentSauvegarde.getPublicId()
            );


            return documentSauvegarde;


        } catch (Exception e) {

            log.error(
                    "Erreur lors de l'upload de la photo d'identité",
                    e
            );


            throw new RuntimeException(
                    "Erreur lors de l'enregistrement de la photo d'identité.",
                    e
            );
        }
    }


    // ============================================================
    // DOCUMENT
    // ============================================================

    /**
     * Enregistre un document envoyé par le candidat.
     *
     * Les fichiers sont envoyés directement vers Cloudinary.
     */
    @Override
    public Document saveDocument(
            MultipartFile file,
            TypeDocument typeDocument,
            String dossier
    ) {


        /*
         * Un fichier vide n'est pas enregistré.
         */
        if (file == null || file.isEmpty()) {

            return null;
        }


        /*
         * Le type du document est obligatoire.
         */
        if (typeDocument == null) {

            throw new IllegalArgumentException(
                    "Le type du document est obligatoire."
            );
        }


        try {

            log.info(
                    "Upload du document vers Cloudinary : fichier={}, type={}",
                    file.getOriginalFilename(),
                    typeDocument
            );


            /*
             * =====================================================
             * 1. UPLOAD CLOUDINARY
             * =====================================================
             *
             * Le CloudinaryService détermine lui-même
             * le dossier à partir du TypeDocument.
             *
             * Exemple :
             *
             * CNI
             *    → documents/cni
             *
             * DIPLOME
             *    → documents/diplomes
             *
             * ACTE_NAISSANCE
             *    → documents/actes
             */
            CloudinaryResponse response =
                    cloudinaryService.uploadFile(
                            file,
                            typeDocument
                    );


            /*
             * =====================================================
             * 2. CRÉATION DE L'ENTITÉ DOCUMENT
             * =====================================================
             */
            Document document =
                    new Document();


            document.setTypeDocument(
                    typeDocument
            );


            /*
             * Nom original envoyé par le candidat.
             */
            document.setNomFichier(
                    file.getOriginalFilename()
            );


            /*
             * URL sécurisée Cloudinary.
             */
            document.setUrl(
                    response.getSecureUrl()
            );


            /*
             * Public ID Cloudinary.
             */
            document.setPublicId(
                    response.getPublicId()
            );


            /*
             * Type de ressource :
             *
             * image
             * raw
             * etc.
             */
            document.setResourceType(
                    response.getResourceType()
            );


            /*
             * =====================================================
             * 3. SAUVEGARDE POSTGRESQL
             * =====================================================
             */
            Document documentSauvegarde =
                    documentRepository.save(document);


            log.info(
                    "Document enregistré avec succès : type={}, publicId={}",
                    typeDocument,
                    documentSauvegarde.getPublicId()
            );


            return documentSauvegarde;


        } catch (Exception e) {

            log.error(
                    "Erreur lors de l'enregistrement du document : {}",
                    file.getOriginalFilename(),
                    e
            );


            throw new RuntimeException(
                    "Erreur lors de l'enregistrement du document.",
                    e
            );
        }
    }
}