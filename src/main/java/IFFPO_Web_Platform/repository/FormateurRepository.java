package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Formateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {

   // Requête avec FETCH de la photo
    @Query("SELECT f FROM Formateur f LEFT JOIN FETCH f.photo")
    Page<Formateur> findAllWithPhoto(Pageable pageable);


    // ✅ Requête avec recherche et FETCH de la photo
    @Query("SELECT f FROM Formateur f LEFT JOIN FETCH f.photo " +
            "WHERE LOWER(f.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(f.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Formateur> searchWithPhoto(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT f FROM Formateur f LEFT JOIN FETCH f.photo WHERE f.id = :id")
    Formateur findByIdWithPhoto(@Param("id") Long id);

    //Search + Pagination
    Page<Formateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);

}
