package location_voiture.persistence.dto;

public class AvisRequestDTO {

	    private Long reservationId;     // ID de la réservation
	    private String commentaire;     // Texte de l'avis
	    private Integer note;           // Note (1 à 5)

	    // Constructeurs
	    public AvisRequestDTO() {
	    }

	    public AvisRequestDTO(Long reservationId, String commentaire, Integer note) {
	        this.reservationId = reservationId;
	        this.commentaire = commentaire;
	        this.note = note;
	    }

	    // Getters & Setters
	    public Long getReservationId() {
	        return reservationId;
	    }

	    public void setReservationId(Long reservationId) {
	        this.reservationId = reservationId;
	    }

	    public String getCommentaire() {
	        return commentaire;
	    }

	    public void setCommentaire(String commentaire) {
	        this.commentaire = commentaire;
	    }

	    public Integer getNote() {
	        return note;
	    }

	    public void setNote(Integer note) {
	        this.note = note;
	    }

	    @Override
	    public String toString() {
	        return "AvisRequestDTO{" +
	                "reservationId=" + reservationId +
	                ", commentaire='" + commentaire + '\'' +
	                ", note=" + note +
	                '}';
	    }
	}


