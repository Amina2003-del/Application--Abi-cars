package location_voiture.persistence.dto;

import lombok.Data;

@Data
public class DisponibiliteDTO {
    private Long voitureId;
    private String dateDebut;
    private String dateFin;    private String statut;

    public DisponibiliteDTO(String string, String string2) {
		// TODO Auto-generated constructor stub
	}
	// Getters et Setters
    public Long getVoitureId() { return voitureId; }
    public void setVoitureId(Long voitureId) { this.voitureId = voitureId; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
   
    public DisponibiliteDTO() {
        // constructeur par défaut requis par Jackson
    }

  
    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }
    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }
   

}
