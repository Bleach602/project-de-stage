package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.DocumentDTO;
import IFFPO_Web_Platform.dto.cloudinary.CloudinaryResponse;
import IFFPO_Web_Platform.entity.*;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.*;
import IFFPO_Web_Platform.service.Cloudinary.CloudinaryService;
import IFFPO_Web_Platform.service.DocumentService;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DocumentServiceImpl
        implements DocumentService {


    private final UtilisateurRepository utilisateurRepository;

    private final CandidatureRepository candidatureRepository;

    private final DocumentRepository documentRepository;

    private final ActualiteRepository actualiteRepository;

    private final FormateurRepository formateurRepository;

    /**
     * Service responsable de Cloudinary.
     */
    private final CloudinaryService cloudinaryService;


    // ============================================================
    // TÉLÉCHARGER UN DOCUMENT
    // ============================================================


    public Resource telecharger(
            Long documentId
    ) {

        Document document =
                documentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document introuvable."
                                )
                        );


        if (document.getUrl() == null ||
                document.getUrl().isBlank()) {

            throw new RuntimeException(
                    "Ce document ne possède pas d'URL Cloudinary."
            );
        }


        try {

            java.net.URL url =
                    new java.net.URL(
                            document.getUrl()
                    );


            java.net.HttpURLConnection connection =
                    (java.net.HttpURLConnection)
                            url.openConnection();


            connection.setRequestMethod(
                    "GET"
            );


            connection.setConnectTimeout(
                    10_000
            );


            connection.setReadTimeout(
                    30_000
            );


            connection.connect();


            int status =
                    connection.getResponseCode();


            if (status !=
                    java.net.HttpURLConnection.HTTP_OK) {

                throw new IOException(
                        "Cloudinary a retourné HTTP "
                                + status
                );
            }


            byte[] contenu;

            try (
                    java.io.InputStream inputStream =
                            connection.getInputStream()
            ) {

                contenu =
                        inputStream.readAllBytes();
            }


            if (contenu.length == 0) {

                throw new IOException(
                        "Le document Cloudinary est vide."
                );
            }


            return new ByteArrayResource(
                    contenu
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    "Impossible de télécharger le document depuis Cloudinary.",
                    e
            );
        }
    }


    // ============================================================
    // REMPLACER UN DOCUMENT
    // ============================================================

//    @Override
//    @Transactional
//    public void remplacerDocument(
//            Long documentId,
//            MultipartFile nouveauFichier
//    ) throws IOException {
//
//
//        if (nouveauFichier == null ||
//                nouveauFichier.isEmpty()) {
//
//            throw new IllegalArgumentException(
//                    "Aucun nouveau fichier sélectionné."
//            );
//        }
//
//
//        /*
//         * Recherche du document.
//         */
//        Document document =
//                documentRepository
//                        .findById(documentId)
//                        .orElseThrow(() ->
//                                new RuntimeException(
//                                        "Document introuvable."
//                                )
//                        );
//
//
//        /*
//         * Vérification de la candidature.
//         */
//        Candidature candidature =
//                document.getCandidature();
//
//
//        if (candidature == null) {
//
//            throw new RuntimeException(
//                    "Ce document n'est associé à aucune candidature."
//            );
//        }
//
//
//        /*
//         * Le document ne peut être modifié
//         * que lorsque la candidature est EN_ATTENTE.
//         */
//        if (candidature.getStatutCandidature()
//                != StatutCandidature.EN_ATTENTE) {
//
//            throw new RuntimeException(
//                    "Votre dossier est déjà traité. " +
//                            "Les documents ne peuvent plus être modifiés."
//            );
//        }
//
//
//        /*
//         * Sauvegarde des anciennes informations
//         * au cas où nous aurions besoin de supprimer
//         * l'ancien fichier Cloudinary après le nouvel upload.
//         */
//        String ancienPublicId =
//                document.getPublicId();
//
//        String ancienResourceType =
//                document.getResourceType();
//
//
//        /*
//         * Upload du nouveau document.
//         */
//        CloudinaryResponse response =
//                cloudinaryService.uploadFile(
//                        nouveauFichier,
//                        document.getTypeDocument()
//                );
//
//
//        /*
//         * Mise à jour des informations.
//         */
//        document.setNomFichier(
//                nouveauFichier.getOriginalFilename()
//        );
//
//
//        document.setUrl(
//                response.getSecureUrl()
//        );
//
//
//        document.setPublicId(
//                response.getPublicId()
//        );
//
//
//        document.setResourceType(
//                response.getResourceType()
//        );
//
//
//        documentRepository.save(document);
//
//
//        /*
//         * Suppression de l'ancien fichier
//         * seulement après que le nouveau fichier
//         * a été correctement envoyé.
//         */
//        if (ancienPublicId != null &&
//                !ancienPublicId.isBlank()) {
//
//            try {
//
//                cloudinaryService.deleteFile(
//                        ancienPublicId,
//                        ancienResourceType
//                );
//
//            } catch (Exception e) {
//
//                /*
//                 * Le nouveau document est déjà enregistré.
//                 *
//                 * On ne fait donc pas échouer
//                 * toute l'opération.
//                 */
//                System.err.println(
//                        "Impossible de supprimer l'ancien fichier Cloudinary : "
//                                + e.getMessage()
//                );
//            }
//        }
//    }

    @Override
    @Transactional
    public void remplacerDocument(
            Long documentId,
            MultipartFile nouveauFichier
    ) throws IOException {

        if (nouveauFichier == null || nouveauFichier.isEmpty()) {
            throw new IllegalArgumentException(
                    "Aucun nouveau fichier sélectionné.");
        }

        // 1. Récupérer le document
        Document document = documentRepository
                .findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document introuvable."));

        // 2. Récupérer la candidature
        Candidature candidature = document.getCandidature();
        if (candidature == null) {
            throw new RuntimeException(
                    "Ce document n'est associé à aucune candidature.");
        }

        // 3. ⚠️ VÉRIFICATION ÉTENDUE
        //    EN_ATTENTE  → modif libre
        //    EN_COURS_DE_CORRECTION → modif autorisée UNIQUEMENT sur les docs demandés
        StatutCandidature statut = candidature.getStatutCandidature();

        if (statut != StatutCandidature.EN_ATTENTE
                && statut != StatutCandidature.EN_COURS_DE_CORRECTION) {
            throw new RuntimeException(
                    "Votre dossier est déjà traité. " +
                            "Les documents ne peuvent plus être modifiés.");
        }

        TypeDocument typeDoc = document.getTypeDocument();
        boolean docConcerneParCorrection = candidature
                .getDocumentsACorriger()
                .contains(typeDoc);

        // 4. Si en correction : autoriser uniquement les docs demandés
        if (statut == StatutCandidature.EN_COURS_DE_CORRECTION
                && !docConcerneParCorrection) {
            throw new RuntimeException(
                    "Ce document n'est pas concerné par la correction demandée.");
        }

        // 5. Si en correction et limite atteinte → bloquer
        if (statut == StatutCandidature.EN_COURS_DE_CORRECTION
                && !candidature.peutEncoreCorriger()) {
            throw new RuntimeException(
                    "Limite de corrections atteinte. " +
                            "Contactez l'administration.");
        }

        // 6. Upload du nouveau fichier
        String ancienPublicId = document.getPublicId();
        String ancienResourceType = document.getResourceType();

        CloudinaryResponse response = cloudinaryService.uploadFile(
                nouveauFichier,
                typeDoc
        );

        // 7. Mise à jour du document
        document.setNomFichier(nouveauFichier.getOriginalFilename());
        document.setUrl(response.getSecureUrl());
        document.setPublicId(response.getPublicId());
        document.setResourceType(response.getResourceType());

        documentRepository.save(document);

        // 8. Suppression ancien fichier Cloudinary
        if (ancienPublicId != null && !ancienPublicId.isBlank()) {
            try {
                cloudinaryService.deleteFile(ancienPublicId, ancienResourceType);
            } catch (Exception e) {
                System.err.println(
                        "Impossible de supprimer l'ancien fichier Cloudinary : "
                                + e.getMessage());
            }
        }

        // ============================================================
        // 9. LOGIQUE DE CORRECTION
        // ============================================================
        if (statut == StatutCandidature.EN_COURS_DE_CORRECTION
                && docConcerneParCorrection) {

            // Retirer ce doc de la liste "à corriger"
            candidature.getDocumentsACorriger().remove(typeDoc);

            // Si TOUS les docs demandés ont été corrigés → 1 tentative consommée
            if (candidature.getDocumentsACorriger().isEmpty()) {

                candidature.setNombreTentativesCorrection(
                        candidature.getNombreTentativesCorrection() + 1
                );

                candidature.setStatutCandidature(StatutCandidature.EN_ATTENTE);
                candidature.setMessageCorrection(null);

                // NOTE : la notification "correction reçue" sera
                // automatiquement visible via getNotifications()
                // (voir le case EN_ATTENTE modifié dans NotificationServiceImpl)
            }

            candidatureRepository.save(candidature);
        }
    }

// ============================================================
// VOIR UN DOCUMENT
// ============================================================

    /**
     * Affiche un document directement
     * dans le navigateur.
     *
     * Le document est récupéré depuis l'URL
     * réellement fournie par Cloudinary lors de l'upload.
     */
    @Override
    public ResponseEntity<Resource> voirDocument(
            Long documentId
    ) {

        Document document =
                documentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document introuvable."
                                )
                        );

        if (document.getUrl() == null ||
                document.getUrl().isBlank()) {

            throw new RuntimeException(
                    "Ce document ne possède pas d'URL Cloudinary."
            );
        }

        try {

            /*
             * ========================================================
             * RÉCUPÉRATION DIRECTE DE L'URL CLOUDINARY
             * ========================================================
             *
             * On utilise exactement l'URL retournée par Cloudinary
             * lors de l'upload.
             *
             * Cela évite de reconstruire une mauvaise URL
             * contenant /v1/.
             */
            java.net.URL url =
                    new java.net.URL(
                            document.getUrl()
                    );

            java.net.HttpURLConnection connection =
                    (java.net.HttpURLConnection)
                            url.openConnection();

            connection.setRequestMethod("GET");

            connection.setConnectTimeout(
                    10_000
            );

            connection.setReadTimeout(
                    30_000
            );

            connection.connect();


            int status =
                    connection.getResponseCode();


            if (status !=
                    java.net.HttpURLConnection.HTTP_OK) {

                throw new IOException(
                        "Cloudinary a retourné HTTP "
                                + status
                                + " | URL : "
                                + document.getUrl()
                );
            }


            /*
             * Lecture du fichier.
             */
            byte[] contenu;

            try (
                    java.io.InputStream inputStream =
                            connection.getInputStream()
            ) {

                contenu =
                        inputStream.readAllBytes();
            }


            if (contenu.length == 0) {

                throw new IOException(
                        "Le document récupéré depuis Cloudinary est vide."
                );
            }


            /*
             * Transformation en Resource.
             */
            Resource resource =
                    new ByteArrayResource(
                            contenu
                    );


            /*
             * Détermination du type MIME.
             */
            MediaType mediaType =
                    determinerMediaType(
                            document.getNomFichier()
                    );


            /*
             * Réponse HTTP.
             */
            return ResponseEntity.ok()

                    .contentType(
                            mediaType
                    )

                    .contentLength(
                            contenu.length
                    )

                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    document.getNomFichier() +
                                    "\""
                    )

                    .body(
                            resource
                    );


        } catch (Exception e) {

            throw new RuntimeException(
                    "Impossible d'afficher le document depuis Cloudinary.",
                    e
            );
        }
    }
    @Transactional
    public Document remplacerPhotoFormateur_and_Actualite(Long formateurID, MultipartFile nouveauFichier) throws IOException {

        if (nouveauFichier == null || nouveauFichier.isEmpty()) {
            throw new IllegalArgumentException("Aucun nouveau fichier sélectionné.");
        }

        // 1. Récupérer le formateur
        Formateur formateur = formateurRepository.findById(formateurID)
                .orElseThrow(() -> new RuntimeException("Formateur introuvable avec l'ID: " + formateurID));

        // 2. Vérifier que le formateur a une photo
        if (formateur.getPhoto() == null) {
            throw new RuntimeException("Le formateur n'a pas de photo à remplacer.");
        }

        // 3. Récupérer le document
        Document document = documentRepository.findById(formateur.getPhoto().getId())
                .orElseThrow(() -> new RuntimeException("Document photo introuvable."));

        // 4. Sauvegarder l'ancien publicId
        String ancienPublicId = document.getPublicId();
        String ancienResourceType = document.getResourceType();

        // 5. Upload du nouveau fichier
        CloudinaryResponse response = cloudinaryService.uploadFile(
                nouveauFichier,
                document.getTypeDocument()
        );

        // 6. Mettre à jour le document
        document.setNomFichier(nouveauFichier.getOriginalFilename());
        document.setUrl(response.getSecureUrl());
        document.setPublicId(response.getPublicId());
        document.setResourceType(response.getResourceType());

        document = documentRepository.save(document);

        // 7. Supprimer l'ancien fichier
        if (ancienPublicId != null && !ancienPublicId.isBlank()) {
            try {
                cloudinaryService.deleteFile(ancienPublicId, ancienResourceType);
            } catch (Exception e) {
                System.out.println("Impossible de supprimer l'ancien fichier Cloudinary: {}" + e.getMessage());
            }
        }

        // ✅ IMPORTANT: Retourner le document avec le formateur synchronisé
        document.setFormateur(formateur);
        return document;
    }

    @Override
    public Document remplacerPhoto_Actualite(Long actuId, MultipartFile nouveauFichier) throws IOException {
        if (nouveauFichier == null || nouveauFichier.isEmpty()) {
            throw new IllegalArgumentException("Aucun nouveau fichier sélectionné.");
        }

        // 1. Récupérer le formateur
        Actualites actualites = actualiteRepository.findById(actuId)
                .orElseThrow(() -> new RuntimeException("Formateur introuvable avec l'ID: " + actuId));

        // 2. Vérifier que le formateur a une photo
        if (actualites.getPhoto() == null) {
            throw new RuntimeException("Le formateur n'a pas de photo à remplacer.");
        }

        // 3. Récupérer le document
        Document document = documentRepository.findById(actualites.getPhoto().getId())
                .orElseThrow(() -> new RuntimeException("Document photo introuvable."));

        // 4. Sauvegarder l'ancien publicId
        String ancienPublicId = document.getPublicId();
        String ancienResourceType = document.getResourceType();

        // 5. Upload du nouveau fichier
        CloudinaryResponse response = cloudinaryService.uploadFile(
                nouveauFichier,
                document.getTypeDocument()
        );

        // 6. Mettre à jour le document
        document.setNomFichier(nouveauFichier.getOriginalFilename());
        document.setUrl(response.getSecureUrl());
        document.setPublicId(response.getPublicId());
        document.setResourceType(response.getResourceType());

        document = documentRepository.save(document);

        // 7. Supprimer l'ancien fichier
        if (ancienPublicId != null && !ancienPublicId.isBlank()) {
            try {
                cloudinaryService.deleteFile(ancienPublicId, ancienResourceType);
            } catch (Exception e) {
                System.out.println("Impossible de supprimer l'ancien fichier Cloudinary: {}" + e.getMessage());
            }
        }

        // ✅ IMPORTANT: Retourner le document avec le formateur synchronisé
        document.setActualites(actualites);
        return document;
    }

    // ============================================================
    // LISTE DES DOCUMENTS
    // ============================================================

    @Override
    @Transactional
    public List<DocumentDTO> getDocuments(String email) {

        /*
         * Recherche du candidat.
         */
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable."));

        /*
         * Recherche de sa dernière candidature.
         */
        Optional<Candidature> candidatureOpt =
                candidatureRepository
                        .findFirstByUtilisateurOrderByDateCandidatureDesc(utilisateur);

        if (candidatureOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Candidature candidature = candidatureOpt.get();

        /*
         * Recherche des documents.
         */
        List<Document> documents =
                documentRepository.findByCandidature(candidature);

        /*
         * ⚠️ NOUVEAU : liste des types de documents à corriger
         * (vide si la candidature n'est pas en correction).
         */
        java.util.Set<IFFPO_Web_Platform.entity.enums.TypeDocument> aCorriger =
                candidature.getDocumentsACorriger() != null
                        ? candidature.getDocumentsACorriger()
                        : Collections.emptySet();

        /*
         * Transformation en DTO.
         */
        return documents.stream()
                .map(document -> {

                    DocumentDTO dto = new DocumentDTO();

                    dto.setId(document.getId());
                    dto.setNomFichier(document.getNomFichier());
                    dto.setTypeDocument(document.getTypeDocument());
                    dto.setDateModification(document.getDateModification());

                    // ⚠️ NOUVEAU : flag "à corriger"
                    dto.setACorriger(
                            aCorriger.contains(document.getTypeDocument())
                    );

                    return dto;

                })
                .toList();
    }

//    @Override
//    @Transactional
//    public List<DocumentDTO> getDocuments(
//            String email
//    ) {
//
//
//        /*
//         * Recherche du candidat.
//         */
//        Utilisateur utilisateur =
//                utilisateurRepository
//                        .findByEmail(email)
//                        .orElseThrow(() ->
//                                new RuntimeException(
//                                        "Utilisateur introuvable."
//                                )
//                        );
//
//
//        /*
//         * Recherche de sa dernière candidature.
//         */
//        Optional<Candidature> candidatureOpt =
//                candidatureRepository
//                        .findFirstByUtilisateurOrderByDateCandidatureDesc(
//                                utilisateur
//                        );
//
//
//        if (candidatureOpt.isEmpty()) {
//
//            return Collections.emptyList();
//        }
//
//
//        Candidature candidature =
//                candidatureOpt.get();
//
//
//        /*
//         * Recherche des documents.
//         */
//        List<Document> documents =
//                documentRepository
//                        .findByCandidature(candidature);
//
//
//        /*
//         * Transformation en DTO.
//         */
//        return documents.stream()
//                .map(document -> {
//
//                    DocumentDTO dto =
//                            new DocumentDTO();
//
//
//                    dto.setId(
//                            document.getId()
//                    );
//
//
//                    dto.setNomFichier(
//                            document.getNomFichier()
//                    );
//
//
//                    dto.setTypeDocument(
//                            document.getTypeDocument()
//                    );
//
//
//                    dto.setDateModification(
//                            document.getDateModification()
//                    );
//
//
//                    return dto;
//
//                })
//                .toList();
//    }


    // ============================================================
    // TYPE MIME
    // ============================================================

    /**
     * Détermine le type MIME à partir
     * de l'extension du fichier.
     */
    private MediaType determinerMediaType(
            String nomFichier
    ) {


        if (nomFichier == null) {

            return MediaType.APPLICATION_OCTET_STREAM;
        }


        String nom =
                nomFichier.toLowerCase();


        if (nom.endsWith(".pdf")) {

            return MediaType.APPLICATION_PDF;
        }


        if (nom.endsWith(".jpg") ||
                nom.endsWith(".jpeg")) {

            return MediaType.IMAGE_JPEG;
        }


        if (nom.endsWith(".png")) {

            return MediaType.IMAGE_PNG;
        }


        if (nom.endsWith(".gif")) {

            return MediaType.IMAGE_GIF;
        }


        return MediaType.APPLICATION_OCTET_STREAM;
    }
}