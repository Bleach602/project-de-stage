package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Paiement;
import IFFPO_Web_Platform.entity.Recu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecuRepository extends JpaRepository<Recu, Long> {

    /**
     * Recherche le reçu associé à un paiement.
     */
    Optional<Recu> findByPaiement(Paiement paiement);

    /**
     * Recherche par numéro de reçu.
     */
    Optional<Recu> findByNumeroRecu(String numeroRecu);
}
