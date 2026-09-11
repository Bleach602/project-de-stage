package IFFPO_Web_Platform.repository;

import IFFPO_Web_Platform.entity.Actualites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ActualiteRepository extends JpaRepository<Actualites, Long> {

    @Query("""
        SELECT a
        FROM Actualites a
        WHERE a.dateEXpiration > :maintenant
        ORDER BY a.datePublication DESC
    """)
    Page<Actualites> trouverActualites(@Param("maintenant") LocalDateTime maintenant,
            Pageable pageable
    );


    @Query("""
        SELECT a
        FROM Actualites a
        WHERE a.dateEXpiration <= :maintenant
        ORDER BY a.datePublication DESC
    """)
    Page<Actualites> trouverArchives(@Param("maintenant") LocalDateTime maintenant,
            Pageable pageable
    );



    @Query("""
        SELECT a
        FROM Actualites a
        WHERE a.dateEXpiration > :maintenant
        AND LOWER(a.titre) LIKE LOWER(CONCAT('%', :motCle, '%'))
        ORDER BY a.datePublication DESC
    """)
    Page<Actualites> rechercherActualites(
            @Param("maintenant") LocalDateTime maintenant,
            @Param("motCle") String motCle,
            Pageable pageable
    );


    @Query("""
        SELECT a
        FROM Actualites a
        WHERE a.dateEXpiration <= :maintenant
        AND LOWER(a.titre) LIKE LOWER(CONCAT('%', :motCle, '%'))
        ORDER BY a.datePublication DESC
    """)
    Page<Actualites> rechercherArchives(
            @Param("maintenant") LocalDateTime maintenant,
            @Param("motCle") String motCle,
            Pageable pageable
    );

}
