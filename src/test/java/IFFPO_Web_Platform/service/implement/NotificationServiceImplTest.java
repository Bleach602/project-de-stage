package IFFPO_Web_Platform.service.implement;

import IFFPO_Web_Platform.dto.NotificationDTO;
import IFFPO_Web_Platform.entity.*;
import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.repository.*;
import IFFPO_Web_Platform.service.implementation.NotificationServiceImpl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    //on injecte nos faux repository
    @Mock
   private  UtilisateurRepository utilisateurRepository;

    @Mock
     private FicheInscriptionRepository ficheInscriptionRepository;

    @Mock
    private  CandidatureRepository candidatureRepository;

    @Mock
   private  PaiementRepository paiementRepository;

    @Mock
    private RecuRepository recuRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void doitRetournerUneListeVideSansCandidature(){

        //ARRAGE
        Utilisateur user = new Utilisateur();
       user.setEmail("Arafat@gmail.com");


        // Quand on cherche l'utilisateur,
        // le faux repository retourne notre utilisateur.
        when(utilisateurRepository.findByEmail("Arafat@gmail.com"))
                .thenReturn(Optional.of(user));

        // L'utilisateur n'a aucune candidature.
        when(candidatureRepository.findFirstByUtilisateurOrderByDateCandidatureDesc(user))
                .thenReturn(Optional.empty());

        //qaund il cherche un paiement

        when(paiementRepository.findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(user))
                .thenReturn(Optional.empty());

        //ACT
        List<NotificationDTO> notifications =
                notificationService.getNotifications("Arafat@gmail.com");

        //ASSERT
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void doitLeverExceptionSiUtilisateurIntrouvable(){

        // ARRANGE

        when(
                utilisateurRepository.findByEmail("Arafat@gmail.com")
        )
                .thenReturn(Optional.empty());


        // ACT + ASSERT

        assertThrows(
                RuntimeException.class,
                () -> notificationService.getNotifications("Arafat@gmail.com")
        );
    }

    @Test
    void doitRetournerNotificationPourCandidatureEnAttente() {

        //ARRANGE

        Utilisateur user = new Utilisateur();
        user.setEmail("Arafaft@gmail.com");

        Candidature candidature = new Candidature();
        Specialite specialite =  new Specialite();

       specialite.setNom("Informatique");

        candidature.setSpecialite(specialite);

        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.findFirstByUtilisateurOrderByDateCandidatureDesc(user))
                .thenReturn(Optional.of(candidature));

        when (paiementRepository.findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(user))
                .thenReturn(Optional.empty());

        candidature.setStatutCandidature(StatutCandidature.EN_ATTENTE);


        //Act
        List<NotificationDTO> notifications = notificationService.getNotifications( user.getEmail() );

        //ASSERT
        assertNotNull(notifications);
        assertTrue(!notifications.isEmpty());
        verify(candidatureRepository)
                .findFirstByUtilisateurOrderByDateCandidatureDesc(user);
        assertEquals( "Dossier en cours d'étude", notifications.get(1).getTitre() );

        //veruify
        verify(
                utilisateurRepository,
                times(1)
        ).findByEmail("Arafaft@gmail.com");

       verifyNoInteractions(ficheInscriptionRepository);
    }

    @Test
    void doitRetournerNotificationNullPourCandidatureNonexistanteEtPaiementNONExist(){

        //ARRANge
        Utilisateur user = new Utilisateur();
        user.setEmail("Arafat@gmail.com");



        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(candidatureRepository.findFirstByUtilisateurOrderByDateCandidatureDesc(user))
                .thenReturn(Optional.empty());

        when(paiementRepository.findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(user))
                .thenReturn(Optional.empty());

        //ACT

        List<NotificationDTO> notificationDTOS = notificationService.getNotifications(user.getEmail());

        //ASSERT
        verify(utilisateurRepository)
                .findByEmail(user.getEmail());

        assertTrue(notificationDTOS.isEmpty());

        assertNotNull(notificationDTOS);
    }

    @Test
    void neDoitPasChercherCandidatureSiUtilisateurIntrouvable() {

        //ARRANge
        Utilisateur user = new Utilisateur();
        user.setEmail("Arafat@gmail.com");

        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());



        //ACt + ASSERT
        List<NotificationDTO> notificationDTOS ;


        assertThrows(RuntimeException.class, ()->{
            notificationService.getNotifications(user.getEmail());
        });

        //verify

        verify(candidatureRepository, never())
                .findFirstByUtilisateurOrderByDateCandidatureDesc(user);

        verify(paiementRepository, never())
                .findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(user);

    }

    @Test
    void doitRetournerDeuxNotificationsSiPaiementSuccesEtRecuDisponible(){

        //ARRANGE

        Utilisateur user =  new Utilisateur();
        Paiement paiement = new Paiement();
        Recu recu = new Recu();

        user.setEmail("Arafat@gmail.com");
        paiement.setStatus(PaymentStatus.SUCCESS);
        recu.setPaiement(paiement);
        paiement.setRecu(recu);

        paiement.setCreatedAt(LocalDateTime.now());
        paiement.setUpdatedAt(LocalDateTime.now());

        when(utilisateurRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(paiementRepository.findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(user))
                .thenReturn(Optional.of(paiement));

        when(candidatureRepository.findFirstByUtilisateurOrderByDateCandidatureDesc(user))
                .thenReturn(Optional.empty());

        //ACT
        List<NotificationDTO> notificationDTOS = notificationService.getNotifications(user.getEmail());

        //ASSERT



        assertNotNull(notificationDTOS);

        assertEquals(3, notificationDTOS.size());

        System.out.println("Nombre : " + notificationDTOS.size());

        notificationDTOS.forEach(notification -> {
            System.out.println(
                    "Titre = " + notification.getTitre()
                            + " | Message = " + notification.getMessage()
                            + " | Date = " + notification.getDate()
            );
        });

        assertTrue(
                notificationDTOS.stream()
                        .anyMatch(n -> n.getTitre().equals("Paiement validé"))
        );

        assertTrue(
                notificationDTOS.stream()
                        .anyMatch(n -> n.getTitre().equals("Reçu disponible"))
        );

        //Verify
        verify(
                paiementRepository,
                times(1)
        )
                .findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(user);




    }
}
