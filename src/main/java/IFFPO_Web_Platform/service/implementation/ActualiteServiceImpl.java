package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.ActualiteDTO;
import IFFPO_Web_Platform.entity.Actualites;
import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.ActualiteRepository;
import IFFPO_Web_Platform.service.ActualiteService;
import IFFPO_Web_Platform.service.DocumentService;
import IFFPO_Web_Platform.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActualiteServiceImpl implements ActualiteService {

    private final ActualiteRepository actualiteRepository;
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
    public ActualiteDTO trouverParId(Long id) {

        Actualites actualites = actualiteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Actualité introuvable"));

        return convertToDTO(actualites);
    }

    @Override
    public Page<ActualiteDTO> trouverActualites(int page, int size, String motCle) {

        LocalDateTime maintenant = LocalDateTime.now();

        Pageable pageable = PageRequest.of(page, size);

        Page<Actualites> resultats;

        if (motCle == null || motCle.trim().isEmpty()) {
            resultats = actualiteRepository.trouverActualites(maintenant, pageable);

        } else {
            resultats = actualiteRepository.rechercherActualites(maintenant, motCle.trim(), pageable);
        }

        return resultats.map(this::convertToDTO);
    }

    @Override
    public Page<ActualiteDTO> trouverArchives(int page, int size, String motCle) {

        LocalDateTime maintenant = LocalDateTime.now();

        Pageable pageable = PageRequest.of(page, size);

        Page<Actualites> resultats;

        if (motCle == null || motCle.trim().isEmpty()) {
            resultats = actualiteRepository.trouverArchives(maintenant, pageable);
        } else {
            resultats = actualiteRepository.rechercherArchives(maintenant, motCle.trim(), pageable);
        }

        return resultats.map(this::convertToDTO);
    }

    @Override
    public void createActualite(ActualiteDTO dto) {

        if (dto.getImage() == null || dto.getImage().isEmpty()) {
            throw new RuntimeException("L'image de l'actualité est obligatoire");
        }

        LocalDateTime datePublication = LocalDateTime.now();


        Actualites actualites = new Actualites();


        actualites.setTitre(dto.getTitre());
        actualites.setDescription(dto.getDescription());
        actualites.setDateEvenement(dto.getDateEvenement());
        actualites.setDatePublication(datePublication);

        actualites.setDateEXpiration(datePublication.plusDays(14));
        //actualites.setDateEXpiration(datePublication.(2));

        // Sauvegarde de la photo sur Cloudinary
        MultipartFile photo = validateImage(dto.getImage());

        Document actu = fileStorageService.saveDocument(photo, TypeDocument.ACTUALITE, "Actualites");

        //  Synchronisation bidirectionnelle
        actualites.setPhoto(actu);
        actu.setActualites(actualites);

        actualiteRepository.save(actualites);

    }

    @Override
    public void updateActualite(Long id, ActualiteDTO dto) throws IOException{

        Actualites actualites = actualiteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Actualité introuvable")
                );

        actualites.setTitre(dto.getTitre());
        actualites.setDescription(dto.getDescription());
        actualites.setDateEvenement(dto.getDateEvenement());


            // Remplacer la photo si présente
            if (dto.getImage() != null && !dto.getImage().isEmpty()) {

                Document actu = documentService.remplacerPhoto_Actualite(
                        actualites.getId(),
                        dto.getImage()
                );

                //  Synchronisation bidirectionnelle
                actualites.setPhoto(actu);
                actu.setActualites(actualites);

        }

        actualiteRepository.save(actualites);
    }

    @Override
    public void deleteActualite(Long id) {

        Actualites actualites = actualiteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Actualité introuvable"));

        if (actualites.getPhoto() != null){
          //  fileStorageService.deleteFichier(actualites.getImage());
        }

        actualiteRepository.delete(actualites);
    }

    //CONVERTIR EN DTO
    private ActualiteDTO convertToDTO(Actualites actualites){

        ActualiteDTO actualiteDTO = new ActualiteDTO();

        actualiteDTO.setId(actualites.getId());
        actualiteDTO.setTitre(actualites.getTitre());
        actualiteDTO.setDescription(actualites.getDescription());
        actualiteDTO.setDatePublication(actualites.getDatePublication());
        actualiteDTO.setDateEvenement(actualites.getDateEvenement());
        actualiteDTO.setDateEXpiration(actualites.getDateEXpiration());

        //  Récupérer l'URL de la photo depuis Cloudinary
        if (actualites.getPhoto() != null && actualites.getPhoto().getUrl() != null) {
            actualiteDTO.setPhotoUrl(actualites.getPhoto().getUrl());
        }

        return actualiteDTO;
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
