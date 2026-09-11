package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.entity.*;
import IFFPO_Web_Platform.entity.enums.StatutSession;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.CandidatureRepository;
import IFFPO_Web_Platform.repository.FicheInscriptionRepository;
import IFFPO_Web_Platform.repository.SessionRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.implementation.CandidatureServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CandidaterTest {

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    FicheInscriptionRepository ficheInscriptionRepository;

    @Mock
    CandidatureRepository candidatureRepository;

    @Mock
    FileStorageService fileStorageService;

    @Mock
    SessionRepository sessionRepository;

    @InjectMocks
    CandidatureServiceImpl candidatureService;

    @Test
    void doitLeverExceptionSiUtilisateurIntrouvableLorsDepotCandidature() {

        //Arrange
        String email = "Arafat@gmail.com";

        when(utilisateurRepository.findByEmail(email))
                .thenReturn(Optional.empty());


        //ACT + ASSERT

      RuntimeException test = assertThrows(RuntimeException.class, ()->
                candidatureService.deposerCandidature(email,
                        null, null,
                        null)
                );

     verify(utilisateurRepository, times(1))
             .findByEmail(email);

     verify(candidatureRepository,never())
             .countByUtilisateur(any(Utilisateur.class));

    }

    @Test
    void doitLeverExceptionSiUtilisateurPossedeDejaDeuxCandidatures() {

        //ARRANGE
        Utilisateur user = new Utilisateur();
        user.setEmail("arafat@gmail.com");

        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.countByUtilisateur(user))
                .thenReturn(2L);

        //ACT + ASSERT

       assertThrows(RuntimeException.class,()->

                candidatureService.deposerCandidature(user.getEmail(),
                        null,null,null)

                );

       //verify

        verify(utilisateurRepository, times(1))
                .findByEmail(user.getEmail());

        verify(candidatureRepository, times(1))
                .countByUtilisateur(user);

        verify(ficheInscriptionRepository, never())
                .findByUtilisateur(any(Utilisateur.class));

    }

    @Test
    void doitLeverExceptionSiFicheInscriptionIntrouvable() {

        //ARRANGE
        Utilisateur user = new Utilisateur();
        user.setEmail("arafat@gmail.com");

        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.countByUtilisateur(user))
                .thenReturn(0L);

        when(ficheInscriptionRepository.findByUtilisateur(user))
                .thenReturn(Optional.empty());

        //ACT + ASSERT

        assertThrows(RuntimeException.class, () ->
                candidatureService.deposerCandidature(user.getEmail(),null,null,null)
                );

        // VERIFY
        verify(utilisateurRepository, times(1))
                .findByEmail(user.getEmail());

        verify(candidatureRepository, times(1))
                .countByUtilisateur(user);

        verify(ficheInscriptionRepository, times(1))
                .findByUtilisateur(user);

    }

    @Test
    void doitLeverExceptionSiFicheSansSpecialite() {
        //ARRANGE
        Utilisateur user = new Utilisateur();
        user.setEmail("arafat@gmail.com");

        FicheInscription fiche = new FicheInscription();
        fiche.setSpecialite(null);


        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.countByUtilisateur(user))
                .thenReturn(0L);

        when(ficheInscriptionRepository.findByUtilisateur(user))
                .thenReturn(Optional.of(fiche));

        //ACT + ASSERT

        assertThrows(RuntimeException.class, () ->
                candidatureService.deposerCandidature(
                        user.getEmail(),
                        null,
                        null,
                        null
                )
        );


        // VERIFY
        verify(utilisateurRepository, times(1))
                .findByEmail(user.getEmail());

        verify(candidatureRepository, times(1))
                .countByUtilisateur(user);

        verify(ficheInscriptionRepository, times(1))
                .findByUtilisateur(user);

        verify(candidatureRepository, never())
                .existsByUtilisateurAndSpecialite(any(), any());

        verify(candidatureRepository, never())
                .save(any(Candidature.class));

    }

    @Test
    void doitLeverExceptionSiCandidatureDejaExistantePourLaMemeSpecialite(){

        //ARRANGE
        Utilisateur user = new Utilisateur();
        user.setEmail("arafat@gmail.com");

        FicheInscription fiche = new FicheInscription();

        Specialite specialite = new Specialite();
        fiche.setSpecialite(specialite);

        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.countByUtilisateur(user))
                .thenReturn(0L);

        when(ficheInscriptionRepository.findByUtilisateur(user))
                .thenReturn(Optional.of(fiche));

        when(candidatureRepository.existsByUtilisateurAndSpecialite(user,specialite))
                .thenReturn(true);

        //ACT + ASSERT
        assertThrows(RuntimeException.class, () -> candidatureService.deposerCandidature(
                user.getEmail(), null, null, null ) );

        //verify
        verify(utilisateurRepository, times(1)) .findByEmail(user.getEmail());
        verify(candidatureRepository, times(1)) .countByUtilisateur(user);
        verify(ficheInscriptionRepository, times(1)) .findByUtilisateur(user);
        verify(candidatureRepository, times(1)) .existsByUtilisateurAndSpecialite( user, specialite);
        verify(sessionRepository, never()) .findByStatutSession(any());
        verify(candidatureRepository, never()) .save(any(Candidature.class));

    }


    @Test
    void doitLeverExceptionSiAucuneSessionDeCandidatureNestOuverte() {

        //ARRANGE
        Utilisateur user = new Utilisateur();
        user.setEmail("arafat@gmail.com");

        FicheInscription fiche = new FicheInscription();

        Specialite specialite = new Specialite();
        fiche.setSpecialite(specialite);



        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.countByUtilisateur(user))
                .thenReturn(0L);

        when(ficheInscriptionRepository.findByUtilisateur(user))
                .thenReturn(Optional.of(fiche));

        when(sessionRepository.findByStatutSession(StatutSession.OUVERTE))
                .thenReturn(Optional.empty());

        //ACT + ASSERT

        assertThrows(RuntimeException.class, () -> candidatureService.deposerCandidature(
                user.getEmail(), null, null, null ) );

        //verify
        verify(utilisateurRepository, times(1)) .findByEmail(user.getEmail());
        verify(candidatureRepository, times(1)) .countByUtilisateur(user);
        verify(ficheInscriptionRepository, times(1)) .findByUtilisateur(user);
        verify(sessionRepository, times(1)) .findByStatutSession(StatutSession.OUVERTE);
        verify(candidatureRepository, never()) .save(any(Candidature.class));

    }

    @Test
    void doitDeposerCandidatureAvecSucces() {

        //ARrange

        Utilisateur user = new Utilisateur();
        Specialite specialite = new Specialite();
        FicheInscription fiche = new FicheInscription();
        SessionCandidature sessionCandidature = new SessionCandidature();
        Candidature candidature = new Candidature();

        Document documentCni = new Document();
        Document documentDiplome = new Document();
        Document documentActe = new Document();


        user.setEmail("Arafat@gmail.com");
        sessionCandidature.setStatutSession(StatutSession.OUVERTE);
        fiche.setSpecialite(specialite);
        candidature.setFicheInscription(fiche);


        MultipartFile cni = mock(MultipartFile.class);
        MultipartFile acte = mock(MultipartFile.class);
        MultipartFile diplome = mock(MultipartFile.class);

        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.countByUtilisateur(user))
                .thenReturn(0L);

        when(ficheInscriptionRepository.findByUtilisateur(user))
                .thenReturn(Optional.of(fiche));

        when(candidatureRepository.existsByUtilisateurAndSpecialite(user,specialite))
                .thenReturn(false);

        when(sessionRepository.findByStatutSession(StatutSession.OUVERTE))
                .thenReturn(Optional.of(sessionCandidature));

        when(fileStorageService.saveDocument(
                cni,
                TypeDocument.CNI,
                "cni"))
                .thenReturn(documentCni);

        when(fileStorageService.saveDocument(
                diplome,
                TypeDocument.DIPLOME,
                "diplome"))
                .thenReturn(documentDiplome);

        when(fileStorageService.saveDocument(
                acte,
                TypeDocument.ACTE_NAISSANCE,
                "acte_naissance"))
                .thenReturn(documentActe);

        //ACT + ASSERT

       assertDoesNotThrow( () ->
               candidatureService.deposerCandidature(user.getEmail(),
                       cni,diplome,acte));

        verify(utilisateurRepository, times(1))
                .findByEmail(user.getEmail());

        verify(candidatureRepository, times(1))
                .countByUtilisateur(user);

        verify(ficheInscriptionRepository, times(1))
                .findByUtilisateur(user);

        verify(candidatureRepository, times(1))
                .existsByUtilisateurAndSpecialite(
                        user, specialite);

        verify(sessionRepository, times(1))
                .findByStatutSession(
                        StatutSession.OUVERTE);

        verify(fileStorageService, times(1))
                .saveDocument(cni, TypeDocument.CNI, "cni");


        verify(fileStorageService, times(1))
                .saveDocument(diplome, TypeDocument.DIPLOME, "diplome");

        verify(fileStorageService, times(1))
                .saveDocument(
                        acte,
                        TypeDocument.ACTE_NAISSANCE,
                        "acte_naissance");

        verify(candidatureRepository, times(1))
                .save(any(Candidature.class));

    }




    }
