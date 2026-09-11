package IFFPO_Web_Platform.service.Cloudinary;

import IFFPO_Web_Platform.dto.cloudinary.CloudinaryResponse;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.util.FileNameGenerator;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // ============================================================
    // UPLOAD MULTIPARTFILE
    // ============================================================

    @Override
    public CloudinaryResponse uploadFile(
            MultipartFile file,
            TypeDocument typeDocument
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Le fichier est vide."
            );
        }

        String folder = getFolder(typeDocument);

        String filename =
                FileNameGenerator.generate(
                        file.getOriginalFilename(),
                        typeDocument
                );

        Map<?, ?> result =
                cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder",
                                folder,

                                "public_id",
                                filename
                        )
                );

        return CloudinaryResponse.builder()

                .publicId(
                        result.get("public_id").toString()
                )

                .secureUrl(
                        result.get("secure_url").toString()
                )

                .resourceType(
                        result.get("resource_type").toString()
                )

                .originalFilename(
                        file.getOriginalFilename()
                )

                .format(
                        result.get("format") != null
                                ? result.get("format").toString()
                                : null
                )

                .build();
    }


    // ============================================================
    // UPLOAD BYTE[]
    // ============================================================

    @Override
    public CloudinaryResponse uploadFile(
            byte[] content,
            String filename,
            TypeDocument typeDocument
    ) throws IOException {

        if (content == null || content.length == 0) {
            throw new IllegalArgumentException(
                    "Le contenu du fichier est vide."
            );
        }

        String folder = getFolder(typeDocument);

        String generatedName =
                FileNameGenerator.generate(
                        filename,
                        typeDocument
                );

        Map<?, ?> result =
                cloudinary.uploader().upload(
                        content,
                        ObjectUtils.asMap(

                                "folder",
                                folder,

                                "public_id",
                                generatedName,

                                "resource_type",
                                "raw"
                        )
                );

        return CloudinaryResponse.builder()

                .publicId(
                        result.get("public_id").toString()
                )

                .secureUrl(
                        result.get("secure_url").toString()
                )

                .resourceType(
                        result.get("resource_type").toString()
                )

                .originalFilename(
                        filename
                )

                .format(
                        result.get("format") != null
                                ? result.get("format").toString()
                                : "pdf"
                )

                .build();
    }


    // ============================================================
    // DOWNLOAD CLOUDINARY
    // ============================================================
    @Override
    public byte[] downloadFile(
            String publicId,
            String resourceType
    ) throws IOException {

        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException(
                    "Le publicId Cloudinary est obligatoire."
            );
        }

        if (resourceType == null || resourceType.isBlank()) {
            resourceType = "raw";
        }

        /*
         * ============================================================
         * CONSTRUCTION DE L'URL CLOUDINARY
         * ============================================================
         *
         * Pour les images et les fichiers raw, on utilise une URL
         * de livraison Cloudinary sans signature.
         */

        String url;

        if ("raw".equalsIgnoreCase(resourceType)) {

            url = cloudinary.url()
                    .resourceType("raw")
                    .secure(true)
                    .generate(publicId);

        } else {

            url = cloudinary.url()
                    .resourceType("image")
                    .secure(true)
                    .generate(publicId);
        }


        /*
         * ============================================================
         * TÉLÉCHARGEMENT
         * ============================================================
         */

        java.net.URL cloudinaryUrl =
                new java.net.URL(url);

        java.net.HttpURLConnection connection =
                (java.net.HttpURLConnection)
                        cloudinaryUrl.openConnection();

        connection.setRequestMethod("GET");

        connection.setConnectTimeout(15000);

        connection.setReadTimeout(30000);

        int status =
                connection.getResponseCode();

        if (status != java.net.HttpURLConnection.HTTP_OK) {

            throw new IOException(
                    "Impossible de télécharger le fichier Cloudinary. "
                            + "HTTP status : "
                            + status
                            + " | URL : "
                            + url
            );
        }

        try (
                java.io.InputStream inputStream =
                        connection.getInputStream();

                java.io.ByteArrayOutputStream outputStream =
                        new java.io.ByteArrayOutputStream()
        ) {

            byte[] buffer =
                    new byte[8192];

            int bytesRead;

            while (
                    (bytesRead =
                            inputStream.read(buffer))
                            != -1
            ) {

                outputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            return outputStream.toByteArray();

        } finally {

            connection.disconnect();
        }
    }
    // ============================================================
    // SUPPRESSION
    // ============================================================

    @Override
    public void deleteFile(
            String publicId,
            String resourceType
    ) throws IOException {

        if (publicId == null || publicId.isBlank()) {
            return;
        }

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap(
                        "resource_type",
                        resourceType
                )
        );
    }


    // ============================================================
    // URL SIGNÉE
    // ============================================================

    @Override
    public String generateSignedUrl(
            String publicId,
            String resourceType
    ) {

        return cloudinary.url()
                .resourceType(resourceType)
                .signed(true)
                .generate(publicId);
    }


    // ============================================================
    // DOSSIERS CLOUDINARY
    // ============================================================

    private String getFolder(
            TypeDocument typeDocument
    ) {

        return switch (typeDocument) {

            case PHOTO_IDENTITE ->
                    "documents/photos";

            case CNI ->
                    "documents/cni";

            case ACTE_NAISSANCE ->
                    "documents/actes";

            case DIPLOME ->
                    "documents/diplomes";

            case RELEVE_NOTES ->
                    "documents/releves";

            case CERTIFICAT_MEDICAL ->
                    "documents/certificats";

            case RECU_PAIEMENT ->
                    "documents/recu";

            case FICHE_INSCRIPTION ->
                    "documents/pdf";

            case AUTRE ->
                    "documents/autres";
            case PHOTO_FORMATEUR ->
                    "documents/Formateur";

            case ACTUALITE ->
                "documents/Actualites";
        };
    }
}