package IFFPO_Web_Platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formateur")
@Getter
@Setter
public class Formateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //pour les  formateur
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "photo_id", referencedColumnName = "id")  // ← La clé étrangère est ici
    private Document photo;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String poste;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false)
    private String whatsAppNumber;

    private String adresseMail;


    /**
     * Met à jour la photo du formateur
     */
    // Dans Formateur.java
    public void setPhoto(Document photo) {
        // Si on a déjà une photo, on la retire
        if (this.photo != null && this.photo != photo) {
            this.photo.setFormateur(null);
        }

        this.photo = photo;

        // Synchronisation inverse
        if (photo != null && photo.getFormateur() != this) {
            photo.setFormateur(this);
        }
    }
}
