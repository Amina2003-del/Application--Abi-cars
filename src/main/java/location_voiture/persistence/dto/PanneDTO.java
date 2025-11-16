package location_voiture.persistence.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

public class PanneDTO {
    @NotNull(message = "L'ID de la voiture est requis")
    private Long carId;

    @NotNull(message = "La date de début est requise")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String description;
    private String carFullName;
    // Getters et setters
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
   

    public String getCarFullName() { return carFullName; }
    public void setCarFullName(String carFullName) { this.carFullName = carFullName; }

}