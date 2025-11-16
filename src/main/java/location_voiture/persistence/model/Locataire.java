package location_voiture.persistence.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ma.abisoft.persistence.model.User;

@Entity
	@Table(name = "locataires")
//@DiscriminatorValue("CLIENT")
	public class Locataire implements Serializable{
	    
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	  
		private String adresse;

	    @Column(unique = true)
	    private String numeroPermis;
	    
	  
	    
	    // Relation "effectue" (supposée être OneToMany)
	   // @OneToMany(mappedBy = "locataire", cascade = CascadeType.ALL, orphanRemoval = true)
	   // private List<Location> locations = new ArrayList<>();
	    public String getAdresse() {
	        return adresse;
	    }
	 
	    @OneToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    @JsonIgnore  // ← AJOUTEZ CETTE LIGNE

	    private User user;
	    
	    public void setAdresse(String adresse) {
	        this.adresse = adresse;
	    }	    // Constructeurs
	    public Locataire() {}
	
	    /*public Locataire(TypeLocataire type, String numeroPermis, Double noteMoyenne) {
	        this.type = type;
	        this.numeroPermis = numeroPermis;
	        this.noteMoyenne = noteMoyenne;
	    }
	    
	    // Méthodes pour gérer la relation bidirectionnelle
	    public void addLocation(Location location) {
	        if (location != null && !locations.contains(location)) {
	            locations.add(location);
	            location.setLocataire(this);
	        }
	    }
	    */
	  /*  public void removeLocation(Location location) {
	        if (location != null && locations.contains(location)) {
	            locations.remove(location);
	            location.setLocataire(null);
	        }
	    }
	    */
	    // Getters et Setters
	    public Long getId() {
	        return id;
	    }
	    
	  /*  public TypeLocataire getType() {
	        return type;
	    }
	    */
	   /* public void setType(TypeLocataire type) {
	        this.type = type;
	    }
	    */
	    public String getNumeroPermis() {
	        return numeroPermis;
	    }
	    
	    public void setNumeroPermis(String numeroPermis) {
	        this.numeroPermis = numeroPermis;
	    }
	    
	   
	    
	    /*public List<Location> getLocations() {
	        return locations;
	    }*/
	 // Relation OneToMany avec Réservation
	    @OneToMany(mappedBy = "locataire", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Réservation> reservations = new ArrayList<>();
	    
	    // Constructeurs
	    
	    public Locataire(String numeroPermis) {
	        this.numeroPermis = numeroPermis;
	    }

	

		 // Méthodes pour gérer la relation bidirectionnelle
	    public void addReservation(Réservation reservation) {
	        if (reservation != null && !reservations.contains(reservation)) {
	            reservations.add(reservation);
	            reservation.setLocataire(this);
	        }
	    }
	    
	    public void removeReservation(Réservation reservation) {
	        if (reservation != null && reservations.contains(reservation)) {
	            reservations.remove(reservation);
	            reservation.setLocataire((Locataire) null);
	        }
	    }
	    
	    // Getters et Setters
	
	    // Méthodes pour gérer les relations
	    public void addMessage(Message message) {
	        if (message != null && !messages.contains(message)) {
	            messages.add(message);
	            message.setReservation(this);
	        }
	    }
	    public void removeMessage(Message message) {
	        if (message != null && messages.contains(message)) {
	            messages.remove(message);
	            message.setReservationMes(null);
	        }
	    }

	    public void addPaiement(Paiement paiement) {
	        if (paiement != null && !paiements.contains(paiement)) {
	            paiements.add(paiement);
	            paiement.setReservation(this);
	        }
	    }

	    public void removePaiement(Paiement paiement) {
	        if (paiement != null && paiements.contains(paiement)) {
	            paiements.remove(paiement);
	            paiement.setReservations(null);
	        }}
	    public List<Réservation> getReservations() {
	        return reservations;
	    }
	    // Relation avec Message (OneToMany)
	    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Message> messages = new ArrayList<>();
	    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Paiement> paiements = new ArrayList<>();

	    public User getUser() {
	        return user;
	    }

	    public void setUser(User user) {
	        this.user = user; // ⚠️ Ici on affecte réellement l'objet User
	    }
	
}
