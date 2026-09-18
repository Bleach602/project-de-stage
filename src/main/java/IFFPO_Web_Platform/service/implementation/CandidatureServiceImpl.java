package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.SessionCandidature;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.entity.enums.StatutSession;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.*;
import IFFPO_Web_Platform.service.CandidatureService;
import IFFPO_Web_Platform.service.FileStorageService;
import IFFPO_Web_Platform.specification.CandidatureSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CandidatureServiceImpl implements CandidatureService {

    private final UtilisateurRepository utilisateurRepository;

    private final CandidatureRepository candidatureRepository;

    private final FicheInscriptionRepository ficheRepository;

    private final SessionRepository sessionRepository;

    private final DocumentRepository documentRepository;

    private final FileStorageService fileStorageService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;
    private final Tika tika = new Tika();
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg" // Couvre également les fichiers .jpg
    );



    @Override
    public Page<Candidature> listerAvecFiltres(Long sessionId, StatutCandidature statut,
                                               Long filiereId, Long specialiteId, Pageable pageable) {

        return candidatureRepository.findAll(
                CandidatureSpecification.avecFiltres(sessionId, statut,
                        filiereId, specialiteId), pageable);
    }

    @Override
    public Candidature trouverParId(Long id) {
        return candidatureRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Candidature introuvable"));
    }


    @Override
    public String valider(Long id) {

        Candidature candidature = trouverParId(id);

        verifierTransitionDepuisEnAttente(candidature);
        candidature.setStatutCandidature(StatutCandidature.VALIDER);
        candidature.setDateValidation(LocalDate.now());
        candidatureRepository.save(candidature);

        // Retourner l'URL WhatsApp
        return genererUrlWhatsApp(id);
    }



    @Override
    public String rejeter(Long id, String motifRejet) {

        if (motifRejet == null || motifRejet.isBlank()){
            throw new IllegalArgumentException("Le motif du rejet est obligatoire.");
        }

        Candidature candidature = trouverParId(id);

        verifierTransitionDepuisEnAttente(candidature);
        candidature.setStatutCandidature(StatutCandidature.REJETEE);
        candidature.setMotifRejet(motifRejet);
        candidatureRepository.save(candidature);

        // Retourner l'URL WhatsApp
        return genererUrlWhatsApp(id);

    }



//    @Override
//    public String demanderCorrection(Long id, String messageCorrection) {
//
//        if (messageCorrection == null || messageCorrection.isBlank()){
//            throw new IllegalArgumentException("Message de correction est obligatoire.");
//        }
//
//        Candidature candidature = trouverParId(id);
//        verifierTransitionDepuisEnAttente(candidature);
//        candidature.setStatutCandidature(StatutCandidature.EN_COURS_DE_CORRECTION);
//        candidature.setMessageCorrection(messageCorrection);
//        candidatureRepository.save(candidature);
//
//        return genererUrlWhatsApp(id);
//    }

    @Override
    @Transactional
    public String demanderCorrection(Long id,
                                     List<TypeDocument> documents,
                                     String messageCorrection) {

        if (messageCorrection == null || messageCorrection.isBlank()) {
            throw new IllegalArgumentException("Message de correction est obligatoire.");
        }

        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException(
                    "Veuillez sélectionner au moins un document à corriger.");
        }

        Candidature candidature = trouverParId(id);

        // Vérifie que la limite de 2 corrections n'est pas atteinte
        if (candidature.getNombreTentativesCorrection() >= 2) {
            throw new IllegalStateException(
                    "Limite de corrections atteinte (maximum 2).");
        }

        verifierTransitionDepuisEnAttente(candidature);

        // ✅ Remplir la liste des docs à corriger
        candidature.getDocumentsACorriger().clear();
        candidature.getDocumentsACorriger().addAll(documents);

        candidature.setStatutCandidature(StatutCandidature.EN_COURS_DE_CORRECTION);
        candidature.setMessageCorrection(messageCorrection);

        candidatureRepository.save(candidature);

        return genererUrlWhatsApp(id);
    }



    @Override
    public void verifierTransitionDepuisEnAttente(Candidature candidature) {

        if (candidature.getStatutCandidature() != StatutCandidature.EN_ATTENTE){
            throw new IllegalStateException("Action possible que depuis le statut EN_ATTENTE " +
                    "Statut actuel : " + candidature.getStatutCandidature());
        }

    }
    /*
    *  La méthode verifierTransitionDepuisEnAttente() empeche toute action
    *   admin ( VALIDER, REJETER) si la candidature n'est pas EN_ATTENTE
    * ce qui bloque toute tentative sur une candidature déjà
    * VALIDER, REJETER, EN_COURS_DE_CORRECTION
    */



    // de mon côté
    @Override
    @Transactional
    public void deposerCandidature(
            String utilisateurEmail,
            MultipartFile cni,
            MultipartFile diplome,
            MultipartFile acte
    ) throws IOException {

        /**
         * Recherche de l'utilisateur connecté.
         */
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(utilisateurEmail)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable."));

        /**
         * Un candidat ne peut déposer que deux candidatures.
         */
        long nombreCandidatures =
                candidatureRepository.countByUtilisateur(utilisateur);

        if (nombreCandidatures >= 2) {
            throw new RuntimeException(
                    "Vous avez déjà atteint la limite de deux candidatures.");
        }


        /**
         * Recherche de la fiche d'inscription du candidat.
         */
        FicheInscription fiche =
                ficheRepository
                        .findByUtilisateur(utilisateur)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Aucune fiche d'inscription trouvée."));

        /**
         * Vérifie que la fiche possède une spécialité.
         */
        if (fiche.getSpecialite() == null) {
            throw new RuntimeException(
                    "Aucune spécialité n'est associée à votre fiche d'inscription.");
        }

        /**
         * Vérifie que le candidat n'a pas déjà déposé une candidature
         * pour cette spécialité.
         */
        boolean dejaCandidat =
                candidatureRepository.existsByUtilisateurAndSpecialite(
                        utilisateur,
                        fiche.getSpecialite());

        if (dejaCandidat) {
            throw new RuntimeException(
                    "Vous avez déjà déposé une candidature pour cette spécialité.");
        }

        SessionCandidature session =
                sessionRepository
                        .findByStatutSession(
                                StatutSession.OUVERTE)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Aucune session de candidature n'est ouverte."));


        /**
         * Création de la candidature.
         */
        Candidature candidature = new Candidature();

        candidature.setUtilisateur(utilisateur);

        candidature.setFicheInscription(fiche);

/**
 * La spécialité est automatiquement récupérée
 * depuis la fiche d'inscription.
 */
        candidature.setSpecialite(
                fiche.getSpecialite());

        candidature.setSessionCandidature(session);

        candidature.setDateCandidature(LocalDate.now());

        candidature.setStatutCandidature(
                StatutCandidature.EN_ATTENTE);



        candidature.addDocument( fileStorageService.saveDocument(validateImage(cni), TypeDocument.CNI,"cni"));
        candidature.addDocument( fileStorageService.saveDocument(validateImage(diplome),TypeDocument.DIPLOME,"diplome"));
        candidature.addDocument( fileStorageService.saveDocument(validateImage(acte),TypeDocument.ACTE_NAISSANCE,"acte_naissance"));

        candidatureRepository.save(candidature);


    }


    @Override
    @Transactional
    public boolean candidatureValidee(String email) {

        /*
         * Recherche de l'utilisateur connecté.
         */
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable."));

        /*
         * Recherche de la dernière candidature.
         */
        return candidatureRepository
                .findFirstByUtilisateurOrderByDateCandidatureDesc(utilisateur)
                .map(candidature ->
                        candidature.getStatutCandidature()
                                == StatutCandidature.VALIDER)
                .orElse(false);
    }


    @Override
    public Optional<Candidature> findDerniereCandidature(String email) {
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));
        return candidatureRepository
                .findFirstByUtilisateurOrderByDateCandidatureDesc(utilisateur);
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



    public String genererUrlWhatsApp(Long id) {
        Candidature candidature = trouverParId(id);
        String contact = candidature.getUtilisateur().getTelephone().replaceAll("[^0-9]", "");

        // Si le numéro ne commence pas par +237, on l'ajoute
        if (!contact.startsWith("237")) {
            contact = "237" + contact;
        }

        // Construire le message selon le statut
        String message = construireMessage(candidature);

        // Encoder le message pour URL
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

        // Retourner l'URL WhatsApp
        return "https://wa.me/" + contact + "?text=" + encodedMessage;
    }

    /**
     * Construit le message WhatsApp en fonction du statut
     */
    private String construireMessage(Candidature candidature) {
        String prenom = candidature.getUtilisateur().getPrenom();
        String nom = candidature.getUtilisateur().getNom();
        String email = candidature.getUtilisateur().getEmail();
        String specialite = candidature.getSpecialite().getNom();

        if (candidature.getStatutCandidature() == StatutCandidature.VALIDER) {
            return String.format(
                    "Bonjour %s %s,\n\n" +
                            "Email : %s\n\n" +
                            "Sujet : ✅ VALIDATION DE VOTRE CANDIDATURE\n\n" +
                            "Message :\n" +
                            "Excellente nouvelle ! Votre candidature pour la Spécialité « %s » " +
                            "a été validée par l'administration.\n\n" +
                            "👉 Prochaine étape : connectez-vous à votre tableau de bord " +
                            "pour effectuer le paiement des frais d'inscription.\n\n" +
                            "Merci de votre confiance.",

                    prenom, nom, email, specialite
            );
        }

        else if (candidature.getStatutCandidature() == StatutCandidature.EN_COURS_DE_CORRECTION) {

            String documents = "vos documents";

            if (candidature.getDocumentsACorriger() != null
                    && !candidature.getDocumentsACorriger().isEmpty()) {

                documents = candidature.getDocumentsACorriger()
                        .toString()
                        .replace("[", "")
                        .replace("]", "");
            }

            int tentative = candidature.getNombreTentativesCorrection() + 1;

            return String.format(
                    "Bonjour %s %s,\n\n" +
                            "Email : %s\n\n" +
                            "Sujet : CORRECTION DEMANDÉE SUR VOTRE CANDIDATURE\n\n" +
                            "Message : Votre dossier pour la Spécialité « %s » nécessite une correction.\n" +
                            "Documents concernés : %s\n" +
                            "Motif : %s\n" +
                            "Tentative : %d/2\n\n" +
                            "Connectez-vous à votre tableau de bord, rubrique Mes documents, " +
                            "pour déposer les fichiers corrigés.\n\n" +
                            "Merci.",

                    prenom, nom, email, specialite,
                    documents,
                    candidature.getMessageCorrection(),
                    tentative
            );
        }

        else {
            return String.format(

                    "Bonjour %s %s,\n\n" +
                            "Email : %s\n\n" +
                            "Sujet : ❌ REJET DE VOTRE CANDIDATURE\n\n" +
                            "Message :\n" +
                            "Après étude attentive de votre dossier pour la Spécialité « %s », " +
                            "nous sommes au regret de vous informer que votre candidature n'a pas été retenue.\n\n" +
                            "💬 Motif : %s\n\n" +
                            "Vous pouvez néanmoins déposer une nouvelle candidature si une autre " +
                            "session est ouverte.\n\n" +
                            "Merci de votre compréhension.",

                    prenom, nom, email, specialite, candidature.getMotifRejet()
            );
        }
    }

}



