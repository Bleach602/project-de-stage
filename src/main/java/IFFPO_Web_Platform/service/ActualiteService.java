package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.ActualiteDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface ActualiteService {

    ActualiteDTO trouverParId(Long id);

    Page<ActualiteDTO> trouverActualites(int page, int size, String motCle);
    Page<ActualiteDTO> trouverArchives(int page, int size, String motCle);

    void createActualite(ActualiteDTO dto);
    void updateActualite(Long id, ActualiteDTO dto)throws IOException;
    void deleteActualite(Long id);
//    <>


}
