package location_voiture.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntretienUpdateDTO {
    private String carId;
    private String type;
    private String dateDebut;
    private String dateFin;
    private boolean periodique;
    @JsonProperty("prochainKmEstime")
    private Integer prochainKmEstimes;  // champ Java avec "s", JSON sans "s"

    @JsonProperty("prochaineDateEstimee")
    private String prochaineDateEstimees;
    private String marque; // Ajouté
    private String modele; // Ajouté

    private String remarks; // Ajouté

 
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    // Getters and Setters
    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }
    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }

    public boolean isPeriodique() { return periodique; }
    public void setPeriodique(boolean periodique) { this.periodique = periodique; }
    public Integer getProchainKmEstimes() { return prochainKmEstimes; }
    public void setProchainKmEstimes(Integer prochainKmEstime) { this.prochainKmEstimes = prochainKmEstime; }
    public String getProchaineDateEstimees() { return prochaineDateEstimees; }
    public void setProchaineDateEstimees(String prochaineDateEstimee) { this.prochaineDateEstimees = prochaineDateEstimee; }}
    