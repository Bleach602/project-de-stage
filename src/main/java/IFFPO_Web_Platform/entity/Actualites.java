package IFFPO_Web_Platform.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "actualite")
@Getter
@Setter
public class Actualites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime datePublication;

    private LocalDateTime dateEvenement;

    //pour savoir quand l'évènement s'etait deroulé
    private LocalDateTime dateEXpiration;

    //pour les  formateur
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", referencedColumnName = "id")
    private Document photo;

    /**
     * Met à jour la photo du formateur
     */
    // Dans Formateur.java
    public void setPhoto(Document photo) {
        // Si on a déjà une photo, on la retire
        if (this.photo != null && this.photo != photo) {
            this.photo.setActualites(null);
        }

        this.photo = photo;

        // Synchronisation inverse
        if (photo != null && photo.getActualites() != this) {
            photo.setActualites(this);
        }
    }
}
