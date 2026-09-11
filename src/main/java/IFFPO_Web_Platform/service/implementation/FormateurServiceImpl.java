package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.FormateurDTO;
import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.Formateur;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.FormateurRepository;
import IFFPO_Web_Platform.service.DocumentService;
import IFFPO_Web_Platform.service.FileStorageService;
import IFFPO_Web_Platform.service.FormateurService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormateurServiceImpl implements FormateurService {

    private final FormateurRepository formateurRepository;
    private final FileStorageService fileStorageService;
    private final DocumentService documentService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;
    private final Tika tika = new Tika();
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg" // Couvre également les fichiers .jpg
    );


    @Override
    public FormateurDTO trouverParId(Long id) {
//
//        Formateur formateur = formateurRepository.findById(id)
//                .orElseThrow(()-> new RuntimeException("Formateur introuvable."));
//
//        return mapToDTO(formateur);
        // ✅ Utiliser la requête avec FETCH
        Formateur formateur = formateurRepository.findByIdWithPhoto(id);
        if (formateur == null) {
            throw new RuntimeException("Formateur introuvable.");
        }
        return mapToDTO(formateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormateurDTO> getFormateurs(String keyWord, int page, int size) {

//        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
//        Page<Formateur> formateursPage;
//
//        if (keyWord != null && !keyWord.isBlank()){
//            formateursPage = formateurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
//                    keyWord.trim(), keyWord.trim(), pageable);
//        } else {
//            formateursPage = formateurRepository.findAll(pageable);
//        }
//
//        return formateursPage.map(this::mapToDTO);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Formateur> formateursPage;

        if (keyWord != null && !keyWord.isBlank()) {
            //  Utiliser la requête avec FETCH pour la recherche
            formateursPage = formateurRepository.searchWithPhoto(keyWord.trim(), pageable);
        } else {
            //  Utiliser la requête avec FETCH pour tout charger
            formateursPage = formateurRepository.findAllWithPhoto(pageable);
        }

        return formateursPage.map(this::mapToDTO);
    }

    @Override
    public Formateur createFormateur(FormateurDTO dto) {

        Formateur formateur = new Formateur();

        formateur.setNom(dto.getNom());
        formateur.setPrenom(dto.getPrenom());
        formateur.setPoste(dto.getPoste());
        formateur.setBio(dto.getBio());
        formateur.setWhatsAppNumber(normalizeWhatsAppNumber(dto.getWhatsAppNumber()));
        formateur.setAdresseMail(dto.getAdresseMail());

        // Sauvegarde de la photo sur Cloudinary
        MultipartFile photo = validateImage(dto.getPhoto());

        Document photoFormateur = fileStorageService.saveDocument(photo, TypeDocument.PHOTO_FORMATEUR, "Formateur");

        //  Synchronisation bidirectionnelle
        formateur.setPhoto(photoFormateur);
        photoFormateur.setFormateur(formateur);

        return formateurRepository.save(formateur);

    }

    @Override
    public Formateur updateFormateur(Long id, FormateurDTO formateurDTO) throws IOException {

        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur introuvable."));

        // Mettre à jour les champs
        formateur.setNom(formateurDTO.getNom());
        formateur.setPrenom(formateurDTO.getPrenom());
        formateur.setPoste(formateurDTO.getPoste());
        formateur.setBio(formateurDTO.getBio());
        formateur.setWhatsAppNumber(normalizeWhatsAppNumber(formateurDTO.getWhatsAppNumber()));
        formateur.setAdresseMail(formateurDTO.getAdresseMail());

        // Remplacer la photo si présente
        if (formateurDTO.getPhoto() != null && !formateurDTO.getPhoto().isEmpty()) {

            Document photoFormateur = documentService.remplacerPhotoFormateur_and_Actualite(
                    formateur.getId(),
                    formateurDTO.getPhoto()
            );

            //  Synchronisation bidirectionnelle
            formateur.setPhoto(photoFormateur);
            photoFormateur.setFormateur(formateur);
        }

        return formateurRepository.save(formateur);
    }

    @Override
    public void deleteFormateur(Long id) {

        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Formateur introuvable."));

        //DELETE FILE PHYSIQUE AVANT LE DELETE EN BD
//        if (formateur.getPhoto() != null){
//            fileStorageService.deleteFichier(formateur.getPhoto());
//        }

        formateurRepository.delete(formateur);
    }

    @Override
    public String normalizeWhatsAppNumber(String rawNumber) {

        if (rawNumber == null || rawNumber.isBlank()){
            return null;
        }

        return rawNumber.replaceAll("[^0-9]", "");
    }

    //Convertir Formateur en FormateurDTO
//    private FormateurDTO mapToDTO(Formateur formateur){
//        FormateurDTO formateurDTO = new FormateurDTO();
//
//        formateurDTO.setId(formateur.getId());
//        formateurDTO.setNom(formateur.getNom());
//        formateurDTO.setPrenom(formateur.getPrenom());
//        formateurDTO.setPoste(formateur.getPoste());
//        formateurDTO.setBio(formateur.getBio());
//        formateurDTO.setWhatsAppNumber(formateur.getWhatsAppNumber());
//        formateurDTO.setAdresseMail(formateur.getAdresseMail());
//
//      //  formateurDTO.setExistingPhotoUrl(formateur.getPhoto());
//
//        return formateurDTO;
//    }
    private FormateurDTO mapToDTO(Formateur formateur) {
        FormateurDTO formateurDTO = new FormateurDTO();

        formateurDTO.setId(formateur.getId());
        formateurDTO.setNom(formateur.getNom());
        formateurDTO.setPrenom(formateur.getPrenom());
        formateurDTO.setPoste(formateur.getPoste());
        formateurDTO.setBio(formateur.getBio());
        formateurDTO.setWhatsAppNumber(formateur.getWhatsAppNumber());
        formateurDTO.setAdresseMail(formateur.getAdresseMail());

        //  Récupérer l'URL de la photo depuis Cloudinary
        if (formateur.getPhoto() != null && formateur.getPhoto().getUrl() != null) {
            formateurDTO.setPhotoUrl(formateur.getPhoto().getUrl());
        }

        return formateurDTO;
    }


    // utilitaire pour securisé les images televerser

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
