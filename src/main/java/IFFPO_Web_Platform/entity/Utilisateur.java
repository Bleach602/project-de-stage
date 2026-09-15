package IFFPO_Web_Platform.entity;

import IFFPO_Web_Platform.entity.enums.StatutCompte;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCompte statutCompte;

    private LocalDateTime dateInscription = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Candidature> candidatures = new ArrayList<>();

    /**
     * Une seule fiche d'inscription
     * par utilisateur.
     */
    @OneToOne(mappedBy = "utilisateur",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private FicheInscription ficheInscription;


}
