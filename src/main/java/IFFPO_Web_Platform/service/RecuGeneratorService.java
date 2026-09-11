package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.entity.Paiement;
import IFFPO_Web_Platform.entity.Recu;
import org.springframework.core.io.Resource;

public interface RecuGeneratorService {

    /**
     * Génère le reçu PDF d'un paiement.
     *
     * @param paiement paiement validé
     * @return reçu enregistré
     */
    Recu genererRecu(Paiement paiement);

    void save(Recu recu);

    Recu GetById(Long id);

    /**
     * Télécharge le reçu de paiement
     * du candidat connecté.
     *
     * @param email email du candidat
     * @return Resource PDF
     */
    Resource telechargerRecu(String email);


    byte[] telechargerRecuParId(Long id);
}
