package location_voiture.persistence.dto;

public class ResponseDTO {
	   private String note;
	    private String statut;
	    public ResponseDTO(Boolean statut, String note) {
	      this.statut = String.valueOf(statut); // conversion en texte
;
	        this.note = note;
	    }

		public String getStatut() {
	        return this.statut;
	    }

	    public String getNote() {
	        return this.note;
	    }

	    public void setStatut(String statut) {
	        this.statut = statut;
	    }

	    public void setNote(String note) {
	        this.note = note;
	    }

}
