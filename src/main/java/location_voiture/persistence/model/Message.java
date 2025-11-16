package location_voiture.persistence.model;

import javax.persistence.*;


import ma.abisoft.persistence.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;


    @Column(nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    private boolean lu = false;
    @Column(nullable = false)
    private String sujet;
    
    @Column(nullable = false)
    private boolean archive = false;
    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }
    public User getExpediteur() {
        return expediteur;
    }
    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }

    public void setExpediteur(User expediteur) {
        this.expediteur = expediteur;
    }
    public void marquerCommeLu() {
        this.lu = true;
    }

    public void marquerCommeArchive() {
        this.archive = true;
    }
    public User getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(User destinataire) {
        this.destinataire = destinataire;
    }

    // Relation avec Réservation (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Réservation reservation;

    @Enumerated(EnumType.STRING)
    private TypeMessage type; // INTERNE ou NOTIFICATION

    @ManyToOne
    private User expediteur;

    @ManyToOne
    private User destinataire; // null pour les notifications

    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;
  

	
    
    public Message() {}

    public Message(String content, Réservation reservation) {
        this.content = content;
        this.reservation = reservation;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public Réservation getReservation() {
        return reservation;
    }

    public void setReservation(Réservation reservation) {
        this.reservation = reservation;
    }

   

	public void setReservation(Locataire locataire) {
		// TODO Auto-generated method stub
		
	}

	public void setReservationMes(Object object) {
		// TODO Auto-generated method stub
		
	}

	@ManyToOne
	@JoinColumn(name = "utilisateur")
	private Message messagesEnvoyes;



	




	

	

	public void setUtilisateur(User utilisateur) {
	    this.utilisateur = utilisateur;
	}

	public void setType(TypeMessage type) {
	    this.type = type;
	}




	public void setTypeMessage(TypeMessage interne) {
		// TODO Auto-generated method stub
		
	}

	public void setCar(Car car) {
		// TODO Auto-generated method stub
		
	}

	public void setContet(String contenu) {
		// TODO Auto-generated method stub
		
	}

	 public boolean isArchiver() {
	        return archive;
	    }

	    public void setArchiver(boolean archiver) {
	        this.archive= archiver;
	    }

		public void setExpediteurEmail(String emailExpediteur) {
			// TODO Auto-generated method stub
			
		}

		public void setDestinataireEmail(Object destinataireEmail) {
			// TODO Auto-generated method stub
			
		}

		public void setDestinataireNom(String string) {
			// TODO Auto-generated method stub
			
		}
		public String getDestinataireEmail() {
		    return destinataire != null ? destinataire.getEmail() : "";
		}

		public String getDestinataireNom() {
		    return destinataire != null ? destinataire.getFirstName() + " " + destinataire.getLastName() : "";
		}

		public String getExpediteurEmail() {
		    return expediteur != null ? expediteur.getEmail() : "";
		}
		  public Boolean getDeleted() {
		        return deleted;
		    }

		    public void setDeleted(Boolean deleted) {
		        this.deleted = deleted;
		    }
		public String getExpediteurNom() {
		    return expediteur != null ? expediteur.getFirstName() + " " + expediteur.getLastName() : "";
		}

	
		public void setDeleted(boolean b) {
this.deleted=b;			
		}

		public TypeMessage getType() {
	        return type;  // ⬅️ Retourne la vraie valeur
	    }

	 

}