package location_voiture.persistence.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import ma.abisoft.persistence.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "litiges")
public class Litige {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLitige type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLitige statut = StatutLitige.OUVERT;

    // Relation ManyToOne avec Administrateur
 //   @ManyToOne(fetch = FetchType.LAZY)
   // @JoinColumn(name = "administrateur_id")
   // private Administrateur administrateur;

    // Relation avec Reservation (si nécessaire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    @JsonBackReference // Prevents serialization of the reservation field
    private Reservation reservation;

    // Constructeurs
    public Litige() {}
  //  public void setAdministrateur(Administrateur administrateur) {
       // this.administrateur = administrateur;
   // }

    public Litige(String description, TypeLitige type, Reservation reservation) {
        this.description = description;
        this.type = type;
        this.reservation = reservation;
    }

    // Méthodes pour gérer la relation avec Administrateur
    /* public void assignerAdministrateur(Administrateur admin) {
        if (this.administrateur == admin) {
            return;
        }

        Administrateur ancienAdmin = this.administrateur;
        this.administrateur = admin;

        if (ancienAdmin != null) {
            ancienAdmin.retirerLitige(this);
        }

        if (admin != null) {
            admin.ajouterLitige(this);
        }
    }*/

    // Méthodes métier
    public void resoudreLitige(String resolution) {
        this.resolution = resolution;
        this.statut = StatutLitige.RESOLU;
    }

    public void fermerLitige() {
        if (this.statut == StatutLitige.RESOLU) {
            this.statut = StatutLitige.FERME;
        }
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

    public TypeLitige getType() {
        return type;
    }

    public void setType(TypeLitige type) {
        this.type = type;
    }

    public StatutLitige getStatut() {
        return statut;
    }

    public void setStatut(StatutLitige statut) {
        this.statut = statut;
    }

  //  public Administrateur getAdministrateur() {
   //     return administrateur;
   // }

    public Reservation getReservation() {
        return reservation;
    }
    @Column(name = "attachment_path")
    private String attachmentPath;

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

	public void setAdministrateur(Object object) {
		// TODO Auto-generated method stub
		
	}
	 @ManyToOne
	    private User utilisateur;

	public void setDateCreation(LocalDateTime now) {
		// TODO Auto-generated method stub
		
	}
	public void setUtilisateur(User utilisateur2) {
		// TODO Auto-generated method stub
		
	}
	public User getUtilisateur() {
		// TODO Auto-generated method stub
		return null;
	}
	public Optional<Reservation> getDocuments() {
		// TODO Auto-generated method stub
		return null;
	}
	public void addNote(String note) {
		// TODO Auto-generated method stub
		
	}
	public void setResolution(String resolution) {
	    this.resolution = resolution;
	}

	public String getResolution() {
	    return this.resolution;
	}
	public String getVehicle() {
		// TODO Auto-generated method stub
		return null;
	}
	public Object getClient() {
		// TODO Auto-generated method stub
		return null;
	}
	public User map(Object object) {
		// TODO Auto-generated method stub
		return null;
	}
	public Long getReservationId() {
	    return reservation != null ? reservation.getId() : null;
	}

	public String getNomClient() {
	    if (reservation != null && reservation.getUtilisateur() != null) {
	        return reservation.getUtilisateur().getFirstName() + " " + reservation.getUtilisateur().getLastName();
	    }
	    return null;
	}

	public String getNomProprietaire() {
	    if (reservation != null 
	        && reservation.getVoiture() != null 
	        && reservation.getVoiture().getProprietaire() != null
	        && reservation.getVoiture().getProprietaire().getUser() != null) {

	        User user = reservation.getVoiture().getProprietaire().getUser();
	        return user.getFirstName() + " " + user.getLastName();
	    }
	    return null;
	}

	public void setReponse(String response) {
		// TODO Auto-generated method stub
		
	}

	
}
