package IFFPO_Web_Platform.entity;

import IFFPO_Web_Platform.entity.enums.ModePaiement;
import IFFPO_Web_Platform.entity.enums.PaymentStatus;
import IFFPO_Web_Platform.entity.enums.StatutPaiement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "paiement")
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomCandidat;

    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    /**
     * Statut du paiement
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Référence interne de notre application
     */
    @Column(nullable = false, unique = true)
    private String reference;

    /**
     * Référence renvoyée par Campay
     */
    private String externalReference;

    /**
     * Date de création
     */
    private LocalDateTime createdAt;

    /**
     * Dernière mise à jour
     */
    private LocalDateTime updatedAt;


    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidature_id", nullable = false)
    private Candidature candidature;


    /**
     * Reçu généré après validation.
     */
    @OneToOne(mappedBy = "paiement",
            cascade = CascadeType.ALL)
    private Recu recu;



    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
