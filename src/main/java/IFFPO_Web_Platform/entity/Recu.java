package IFFPO_Web_Platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "recu")
public class Recu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numéro unique du reçu.
     * Exemple :
     * REC-2026-000001
     */
    @Column(nullable = false, unique = true)
    private String numeroRecu;

    /**
     * Date de génération.
     */
    private LocalDateTime dateGeneration;

    /**
     * Nombre de téléchargements.
     */
    private Integer nbreTelechargements = 0;

    /**
     * Paiement concerné.
     */
    @OneToOne
    @JoinColumn(name = "paiement_id", nullable = false, unique = true)
    private Paiement paiement;

    /**
     * PDF du reçu.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id")
    private Document document;

    @PrePersist
    public void prePersist() {
        dateGeneration = LocalDateTime.now();
    }

}