package location_voiture.persistence.dto;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class ReservationRequestDTO {
	private String adressePriseEnCharge;
    private String adresseRestitution;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Format YYYY-MM-DD
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Format YYYY-MM-DD
    private LocalDate dateFin;

    private String typeVoiture;

    // Getters et Setters
    public String getAdressePriseEnCharge() { return adressePriseEnCharge; }
    public void setAdressePriseEnCharge(String adressePriseEnCharge) { this.adressePriseEnCharge = adressePriseEnCharge; }
    public String getAdresseRestitution() { return adresseRestitution; }
    public void setAdresseRestitution(String adresseRestitution) { this.adresseRestitution = adresseRestitution; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getTypeVoiture() { return typeVoiture; }
    public void setTypeVoiture(String typeVoiture) { this.typeVoiture = typeVoiture; }

    @Override
    public String toString() {
        return "ReservationRequestDTO{" +
               "adressePriseEnCharge='" + adressePriseEnCharge + '\'' +
               ", adresseRestitution='" + adresseRestitution + '\'' +
               ", dateDebut=" + dateDebut +
               ", dateFin=" + dateFin +
               ", typeVoiture='" + typeVoiture + '\'' +
               '}';
    }
}