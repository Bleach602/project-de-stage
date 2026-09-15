package IFFPO_Web_Platform.service.implementation;


import IFFPO_Web_Platform.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;



@Service
@RequiredArgsConstructor
public class AccountCleanupService {
    private final UtilisateurRepository utilisateurRepository;

    @Transactional
    @Scheduled(cron = "0 0 2 * * *")// deux heures
    //@Scheduled(cron = "0 * * * * *") // pour une mintues
    public void AccountCleanupInactiveAccount(){

        LocalDateTime limit = LocalDateTime.now().minusDays(3);


        int users =
                utilisateurRepository.findUserEligibleForDeletion(limit);

       System.out.println("{} comptes inactifs supprimés : " + users);

    }

}
