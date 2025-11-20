package location_voiture.persistence.dto;

import java.time.LocalDate;

import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Reservation;
import lombok.Data;
import ma.abisoft.persistence.model.User;
@Data
public class ReservationRequest {
	
	 private Reservation reservation;
	    private Paiement paiement;
	    private Long voitureId;     // ID de la voiture sélectionnée
	    private User utilisateur;
	    private Double montant;
	    private String methode;
	    
	    // ID du locataire (connecté ou saisi)
		public Reservation getReservation() {
			// TODO Auto-generated method stub
			return null;
		}
		

		    public Double getMontant() {
		        return montant;
		    }

		    public void setMontant(Double montant) {
		        this.montant = montant;
		    }

		   
		public Paiement getPaiement() {
			// TODO Auto-generated method stub
			return null;
		}
		public User getUtilisateur() {
	        return utilisateur;
	    }
		public Long getVoitureId() {
			// TODO Auto-generated method stub
			return null;
		}
		public LocalDate getDateDebut() {
			// TODO Auto-generated method stub
			return null;
		}
		public LocalDate getDateFin() {
			// TODO Auto-generated method stub
			return null;
		}
		public void setUtilisateurId(User utilisateur) {
	        this.utilisateur= utilisateur;
	    }
		
		public String getMethode() {
			// TODO Auto-generated method stub
			return null;
		}
}
