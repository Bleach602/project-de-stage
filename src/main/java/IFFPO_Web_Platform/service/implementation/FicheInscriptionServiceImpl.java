package IFFPO_Web_Platform.service.implementation;


import IFFPO_Web_Platform.dto.FicheInscriptionDTO;
import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.Specialite;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.repository.FicheInscriptionRepository;
import IFFPO_Web_Platform.repository.SpecialiteRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.FicheInscriptionService;
import IFFPO_Web_Platform.service.FileStorageService;
import IFFPO_Web_Platform.service.PdfGeneratorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
@RequiredArgsConstructor
@Service
public class FicheInscriptionServiceImpl implements FicheInscriptionService {

    private final FicheInscriptionRepository ficheRepository;

    private final UtilisateurRepository utilisateurRepository;

    private final FileStorageService fileStorageService;
    private final SpecialiteRepository specialiteRepository;
    private final PdfGeneratorService pdfGeneratorService;


    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;
    private final Tika tika = new Tika();
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg" // Couvre également les fichiers .jpg
    );


    @Override
    public FicheInscription enregistrer(FicheInscriptionDTO fiche, String emailUtilisateur) {

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(emailUtilisateur)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable"));


        /*
         * Vérifie si le candidat possède déjà
         * une fiche d'inscription.
         *
         * Chaque candidat ne peut avoir
         * qu'une seule fiche.
         */
        if (utilisateur.getFicheInscription() != null) {

            throw new RuntimeException(
                    "Vous avez déjà enregistré votre fiche d'inscription.");
        }

        Specialite specialite = specialiteRepository.findById(fiche.getSpecialiteId())
                .orElseThrow(() -> new EntityNotFoundException("Spécialité introuvable avec l'ID : " + fiche.getSpecialiteId()));

        FicheInscription ficheInscription = new FicheInscription();
        ficheInscription.setDateNaissance(fiche.getDateNaissance());
        ficheInscription.setLieuNaissance(fiche.getLieuNaissance());
        ficheInscription.setSexe(fiche.getSexe());
        ficheInscription.setAdresse(fiche.getAdresse());
        ficheInscription.setDiplomePlusEleve(fiche.getDiplomePlusEleve());
        ficheInscription.setAnneeObtention(fiche.getAnneeObtention());
        ficheInscription.setHandicap(fiche.getHandicap());
        ficheInscription.setPrecisionHandicap(fiche.getPrecisionHandicap());
        ficheInscription.setMaladie(fiche.getMaladie());
        ficheInscription.setPrecisionMaladie(fiche.getPrecisionMaladie());
        ficheInscription.setNomResponsable(fiche.getNomResponsable());
        ficheInscription.setPrenomResponsable(fiche.getPrenomResponsable());
        ficheInscription.setTelephoneResponsable(fiche.getTelephoneResponsable());
        ficheInscription.setAdresseResponsable(fiche.getAdresseResponsable());
        ficheInscription.setStatutResponsable(fiche.getStatutResponsable());

        ficheInscription.setSpecialite(specialite);

        MultipartFile photo = validateImage(fiche.getPhoto());

        /*
         * Enregistrement de la photo.
         */
        Document photoDocument = fileStorageService.savePhoto(photo);


        /*
         * Association de la photo
         * à la fiche.
         */
        ficheInscription.setPhotoIdentite(photoDocument);
        /*
         * Association Fiche -> Utilisateur
         */
        ficheInscription.setUtilisateur(utilisateur);

        /*
         * Sauvegarde de la fiche d'inscription.
         */
        FicheInscription ficheSauvegardee =
                ficheRepository.save(ficheInscription);

        /*
         * Génération du PDF officiel.
         *
         * Cette opération est exécutée une seule fois,
         * juste après la création de la fiche.
         */
        pdfGeneratorService.genererPdf(ficheSauvegardee);

    /*
//         * Retour de la fiche enregistrée.
//         */
        return ficheSauvegardee;
    }

    @Override
    public boolean ficheExiste(String email) {

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable."));

        return ficheRepository
                .findByUtilisateur(utilisateur)
                .isPresent();
    }


    public MultipartFile validateImage(MultipartFile file) {
        // 1. Vérifier si le fichier est vide
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        // 2. Vérification de la taille max
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Le fichier dépasse la taille maximale autorisée de 5 Mo.");
        }

        // 3. Vérification de l'extension du fichier
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasAllowedExtension(originalFilename)) {
            throw new IllegalArgumentException("Extension de fichier non autorisée. Formats acceptés : .png, .jpg, .jpeg");
        }

        // 4. Sécurité renforcée : Vérification des Magic Bytes via Apache Tika
        try (InputStream inputStream = file.getInputStream()) {
            String detectedMimeType = tika.detect(inputStream, originalFilename);

            if (!ALLOWED_MIME_TYPES.contains(detectedMimeType)) {
                throw new IllegalArgumentException("Le contenu réel du fichier ne correspond pas à une image valide (PNG ou JPEG).");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier.", e);
        }
        return file;
    }


    private boolean hasAllowedExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private String getFileExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return "";
        }
        return filename.substring(lastIndexOfDot + 1);
    }

}
