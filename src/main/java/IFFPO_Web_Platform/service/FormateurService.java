package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.FormateurDTO;
import IFFPO_Web_Platform.entity.Formateur;
import org.springframework.data.domain.Page;

import java.io.IOException;


public interface FormateurService {

    FormateurDTO trouverParId(Long id);

    Page<FormateurDTO> getFormateurs(String keyWord, int page, int size);

    Formateur createFormateur(FormateurDTO dto);
    Formateur updateFormateur(Long id, FormateurDTO formateurDTO) throws IOException;
    void deleteFormateur(Long id);

    //Nettoie le numero WhatsApp pour ne conserver que les chiffres.
    String normalizeWhatsAppNumber(String rawNumber);

}
