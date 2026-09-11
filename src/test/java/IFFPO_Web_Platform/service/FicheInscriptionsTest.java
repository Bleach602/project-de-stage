//package IFFPO_Web_Platform.service;
//
//import IFFPO_Web_Platform.entity.Document;
//import IFFPO_Web_Platform.entity.FicheInscription;
//import IFFPO_Web_Platform.entity.Utilisateur;
//import IFFPO_Web_Platform.repository.DocumentRepository;
//import IFFPO_Web_Platform.repository.FicheInscriptionRepository;
//import IFFPO_Web_Platform.repository.UtilisateurRepository;
//import IFFPO_Web_Platform.service.implementation.FicheInscriptionServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//public class FicheInscriptionsTest {
//
//    @Mock
//    private UtilisateurRepository utilisateurRepository;
//
//    @Mock
//    private DocumentRepository documentRepository;
//
//    @Mock
//    FicheInscriptionRepository ficheInscriptionRepository;
//
//    @InjectMocks
//    private FicheInscriptionServiceImpl ficheInscriptionService;
//
//    @Mock
//    private PdfGeneratorService pdfGeneratorService;
//
//    @Mock
//    FileStorageService fileStorageService;
//
//
//
//    @Test
//    void doitEnregistrerUneFicheAvecSucces(){
//
//        //ARRANGE
//        Utilisateur user = new Utilisateur();
//        user.setEmail("Arafat@gmail.com");
//
//        Document doc = new Document();
//        FicheInscription fiche = new FicheInscription();
//        MultipartFile photo = mock(MultipartFile.class);
//
//
//        when(utilisateurRepository.findByEmail(user.getEmail()))
//                .thenReturn(Optional.of(user));
//
//
//        when(fileStorageService.savePhoto(photo))
//                .thenReturn(doc);
//
//        when(ficheInscriptionRepository.save(fiche))
//                .thenReturn(fiche);
//
//
//
//// ACT
//
//        FicheInscription resultat =
//                ficheInscriptionService.enregistrer(
//                        fiche,
//                        photo,
//                        user.getEmail()
//                );
//
//        //assert
//        assertNotNull(resultat);
//
//
//        verify(fileStorageService, times(1))
//                .savePhoto(photo);
//
//        verify(pdfGeneratorService, times(1))
//                .genererPdf(fiche);
//
//    }
//
//    @Test
//    void NedoitPasEnregistrerUneFicheDejaExistante(){
//
//        //ARRANGE
//        Utilisateur user = new Utilisateur();
//        user.setEmail("Arafat@gmail.com");
//
//        FicheInscription fiche = new FicheInscription();
//        user.setFicheInscription(fiche);
//
//        FicheInscription nouvelleFiche = new FicheInscription();
//
//        MultipartFile photo = mock(MultipartFile.class);
//
//
//        when(utilisateurRepository.findByEmail(user.getEmail()))
//                .thenReturn(Optional.of(user));
//
//
//
//        //Act + Assert
//        assertThrows(RuntimeException.class, ()->
//                ficheInscriptionService.enregistrer(
//                        nouvelleFiche,
//                        photo,
//                        user.getEmail()
//                )
//                );
//
//        //verify
//        verify(fileStorageService, never())
//                .savePhoto(photo);
//
//        verify(ficheInscriptionRepository, never())
//                .save(any(FicheInscription.class));
//
//        verify(pdfGeneratorService, never())
//                .genererPdf(any(FicheInscription.class));
//
//    }
//
//    @Test
//    void doitRetournerTrueSiFicheExiste() {
//
//        // ARRANGE
//        Utilisateur user = new Utilisateur();
//        user.setEmail("Arafat@gmail.com");
//
//        FicheInscription fiche = new FicheInscription();
//        user.setFicheInscription(fiche);
//
//        when(utilisateurRepository.findByEmail(user.getEmail()))
//                .thenReturn(Optional.of(user));
//
//        when(ficheInscriptionRepository.findByUtilisateur(user))
//                .thenReturn(Optional.of(fiche));
//
//
//        // ACT
//        boolean resultat =
//                ficheInscriptionService.ficheExiste(user.getEmail());
//
//
//        // ASSERT
//        assertTrue(resultat);
//
//
//        // VERIFY
//        verify(utilisateurRepository, times(1))
//                .findByEmail(user.getEmail());
//
//        verify(ficheInscriptionRepository, times(1))
//                .findByUtilisateur(user);
//    }
//
//
//    @Test
//    void doitLeverExceptionSiUtilisateurIntrouvableLorsVerificationFiche() {
//
//        // ARRANGE
//
//        when(utilisateurRepository.findByEmail("Arafat@gmail.com"))
//                .thenReturn(Optional.empty());
//
//        // ACT + ASSERT
//
//        assertThrows(
//                RuntimeException.class,
//                () -> ficheInscriptionService.ficheExiste("Arafat@gmail.com")
//        );
//
//
//        // VERIFY
//        verify(ficheInscriptionRepository, never())
//                .findByUtilisateur(any(Utilisateur.class));
//
//    }
//}
