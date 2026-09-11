package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {


    FicheInscription findByFicheInscriptionAndTypeDocument(FicheInscription fiche, TypeDocument type);


    // Recherche tous les documents d'une candidature.
    List<Document> findByCandidature(Candidature candidature);

    /**
     * Recherche un document selon son type.
     * Cette méthode est utilisée lors de la
     * modification d'une pièce justificative.

     */
    Optional<Document> findByCandidatureAndTypeDocument(
            Candidature candidature,
            TypeDocument typeDocument);
}
