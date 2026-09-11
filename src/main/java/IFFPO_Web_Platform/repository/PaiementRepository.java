package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Paiement;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    Optional<Paiement> findByReference(String reference);
    Optional<Paiement> findByExternalReference(String externalReference);

    /**
     * Nombre de paiements selon le statut
     */
    long countByStatus(PaymentStatus status);

    Optional<Paiement>  findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(Utilisateur user);


}
