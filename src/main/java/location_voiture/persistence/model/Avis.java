package location_voiture.persistence.model;

import java.time.LocalDate;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore; // ← IMPORT AJOUTÉ

import javax.persistence.*;

import ma.abisoft.persistence.model.User;

@Entity
@Table(name = "avis")
public class Avis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "voiture_id")
    @JsonIgnore // Empêche la sérialisation de la voiture

    private Car voiture;
    
    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private User auteur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id") // si tu veux garder un lien utilisateur distinct de auteur
    private User utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Réservation reservation;
 // Remplace reservationId Long par relation ManyToOne
    
    private Integer note;
    
    private String commentaire;
    
    private LocalDate date;

    // Constructeur par défaut
    public Avis() {
    }
    public Long getReservationId() {
        return reservation != null ? reservation.getId() : null;
    }

    // Constructeur utile (optionnel)
    public Avis(Car voiture, User auteur, Integer note, String commentaire, LocalDate date, Réservation reservation) {
        this.voiture = voiture;
        this.auteur = auteur;
        this.note = note;
        this.commentaire = commentaire;
        this.date = date;
        this.reservation = reservation;
    }

    // Getters et Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Car getVoiture() {
        return voiture;
    }
    
    public void setVoiture(Car voiture) {
        this.voiture = voiture;
    }
    
    public User getAuteur() {
        return auteur;
    }
    
    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Réservation getReservation() {
        return reservation;
    }

    public void setReservation(Réservation reservation) {
        this.reservation = reservation;
    }

    public Integer getNote() {
        return note;
    }
    
    public void setNote(Integer note) {
        this.note = note;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }

	public void setReservationId(Long id2) {
		// TODO Auto-generated method stub
		
	}
	
	public String getType() {
	    return voiture != null ? voiture.getType() : "Inconnu";
	}

	public Integer getRating() {
	    return note;
	}

	

}
