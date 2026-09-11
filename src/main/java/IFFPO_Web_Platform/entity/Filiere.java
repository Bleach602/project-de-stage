package IFFPO_Web_Platform.entity;

import IFFPO_Web_Platform.entity.enums.StatutFiliere;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "filiere")
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 15)
    private String sigle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFiliere statutFiliere;

    private Integer limitePlace;

    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Specialite> specialites = new ArrayList<>();

    @ManyToMany(mappedBy = "filieres")
    private Set<SessionCandidature> sessions = new HashSet<>();

    //METHOD UTILITAIRE
    public void addSpecialite(Specialite specialite){
        specialites.add(specialite);
        specialite.setFiliere(this);
    }

    public void removeSpecialite(Specialite specialite){
        specialites.remove(specialite);
        specialite.setFiliere(null);
    }

}
