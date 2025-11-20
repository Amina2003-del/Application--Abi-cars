package location_voiture.persistence.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.dto.ProprietaireDto;
import ma.abisoft.persistence.model.User;

@Entity
@Table(name = "reservations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Reservation {
	  @Enumerated(EnumType.STRING)
	    private StatutReservation statut;
	
	@OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference // Serializes the litiges collection
    private List<Litige> litiges = new ArrayList<>();

    // Méthodes pour gérer les litiges
    public void ajouterLitige(Litige litige) {
        if (litige != null && !litiges.contains(litige)) {
            litiges.add(litige);
            litige.setReservation(this);
        }
    }

    public void retirerLitige(Litige litige) {
        if (litige != null) {
            litiges.remove(litige);
            litige.setReservation(null);
        }
    }
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private LocalDate dateDebut;
	    private LocalDate dateFin;
	    private Double prixTotal;

	   

	    // Relation ManyToOne avec Locataire
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "locataire_id")
	    private Locataire locataire;

	    // Constructeurs
	    public Reservation() {
	        this.statut = StatutReservation.EN_ATTENTE;
	    }
	    
	   
	    
	    public Reservation(LocalDate dateDebut, LocalDate dateFin, Double prixTotal, User utilisateur) {
	        this.dateDebut = dateDebut;
	        this.dateFin = dateFin;
	        this.prixTotal = prixTotal;
	        this.statut = StatutReservation.EN_ATTENTE;
	        this.setUser(utilisateur);
	    }
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "utilisateur_id", referencedColumnName = "id",nullable =false)
	    @JsonIgnore // Ignore ce champ lors de la sérialisation
	    private User utilisateur;
	    
	    public Reservation(Car car1, Object object, LocalDate minusDays, double d, String string) {
			// TODO Auto-generated constructor stub
		}@JsonIgnore

	    public User getUtilisateur() {
	        return utilisateur;
	    }

	   
		// Méthode pour gérer la relation avec Locataire
	    public void setLocataire(Locataire locataire) {
	        if (this.locataire == locataire) {
	            return;
	        }

	        Locataire ancienLocataire = this.locataire;
	        this.locataire = locataire;

	        if (ancienLocataire != null) {
	            ancienLocataire.removeReservation(this);
	        }

	        if (locataire != null) {
	            locataire.addReservation(this);
	        }
	    }

	    // Getters et Setters
	    public Long getId() {
	        return id;
	    }

	    public LocalDate getDateDebut() {
	        return dateDebut;
	    }

	    public void setDateDebut(LocalDate dateDebut) {
	        this.dateDebut = dateDebut;
	    }

	    public LocalDate getDateFin() {
	        return dateFin;
	    }

	    public void setDateFin(LocalDate dateFin) {
	        this.dateFin = dateFin;
	    }

	    public Double getPrixTotal() {
	        return prixTotal;
	    }

	    public void setPrixTotal(Double prixTotal) {
	        this.prixTotal = prixTotal;
	    }

	    public StatutReservation getStatut() {
	        return statut;
	    }

	    public void setStatut(StatutReservation statut) {
	        this.statut = statut;
	    }

	    public Locataire getLocataire() {
	        return locataire;
	    }

	    // Méthode métier pour confirmer une réservation
	    public void confirmer() {
	        if (this.statut == StatutReservation.EN_ATTENTE) {
	            this.statut = StatutReservation.CONFIRMEE;
	        }
	    }

	    @Override
	    public String toString() {
	        return "Reservation{" +
	                "id=" + id +
	                ", dateDebut=" + dateDebut +
	                ", dateFin=" + dateFin +
	                ", prixTotal=" + prixTotal +
	                ", statut=" + statut +
	                '}';
	    }
	 
	    public void setVoiture(Car voiture) {
	        this.voiture = voiture;
	    }
	    
	    
	    private String adressePriseEnCharge;
	    private String adresseRestitution;
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "car_id",referencedColumnName = "id")
	    private Car voiture;

		public void setStatut(String string) {
			// TODO Auto-generated method stub
			
		}
		  @Enumerated(EnumType.STRING)
		    private TypeReservation typeReservation;

		
	 

	    public void setUtilisateur(User utilisateur) {
	        this.utilisateur = utilisateur;
	    }

		public String getAdressePriseEnCharge() { return adressePriseEnCharge; }
	    public void setAdressePriseEnCharge(String adressePriseEnCharge) { this.adressePriseEnCharge = adressePriseEnCharge; }
	    public String getAdresseRestitution() { return adresseRestitution; }
	    public void setAdresseRestitution(String adresseRestitution) { this.adresseRestitution = adresseRestitution; }

		public void setLocataire(User locataire2) {
			// TODO Auto-generated method stub
			
		}

		


		public void setCar(Car voiture2) {
			// TODO Auto-generated method stub
			
		}

		public void setUtilisateurId(Long id2) {
			// TODO Auto-generated method stub
			
		}

		public void setVoitureId(Long voitureId) {
			// TODO Auto-generated method stub
			
		}

		public void setTypeVoiture(String typeVoiture) {
			// TODO Auto-generated method stub
			
		}

		public void setNbJours(Integer nbJours) {
			// TODO Auto-generated method stub
			
		}

		public void setNumeroRes(int i) {
			// TODO Auto-generated method stub
			
		}

		public void setDateFin(LocalDateTime with) {
			// TODO Auto-generated method stub
			
		}

		public void setPrixTotal(int i) {
			// TODO Auto-generated method stub
			
		}

		public void setDateDebut(LocalDateTime with) {
			// TODO Auto-generated method stub
			
		}

		public String getNumeroRes() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setUser(User utilisateur2) {
this.utilisateur=	utilisateur2;		
		}

		public Car getCar() {
			// TODO Auto-generated method stub
			return null;
		}

		public double getPrix() {
			// TODO Auto-generated method stub
			return 0;
		}

		public User getClient() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getMontantTotal() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getModele() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setDateReservation(LocalDate now) {
			// TODO Auto-generated method stub
			
		}

		public void setClient(User user) {
			// TODO Auto-generated method stub
			
		}

		public void setPickupAddress(String pickupAddress) {
this.adressePriseEnCharge=		pickupAddress;	
		}

		public void setReturnAddress(String returnAddress) {
			this.adresseRestitution=		returnAddress;	
			
		}

		public void setPrixTotale(double prixTotal2) {
this.prixTotal=	prixTotal2;		
		}

		public Double getMontant() {
		    return getPrixTotal();  // ou simplement prixTotale;
		}

		public User getProprietaire() {
			// TODO Auto-generated method stub
			return null;
		}
		 public List<Litige> getLitiges() {
		        return litiges;
		    }
		public Object getNom() {
			// TODO Auto-generated method stub
			return null;
		}

		public StatutReservation getStatutPaiement() {
		    // exemple : retourne le statut réel depuis un champ ou une relation
		    return this.statut;  // supposant que tu as un champ statutPaiement
		}

		public Paiement getPaiement() {
			// TODO Auto-generated method stub
			return null;
		}

		public Reservation getUser() {
			// TODO Auto-generated method stub
			return null;
		}

		
		
		
		public Car getVoiture() {
		    return this.voiture;
		}

		 public void setTypeReservation(TypeReservation typeReservation) {
		        this.typeReservation = typeReservation;
		    }
		    public TypeReservation getTypeReservation() {
		        return typeReservation;
		    }

		

		

		


		
		

		
	}

