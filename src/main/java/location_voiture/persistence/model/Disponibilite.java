package location_voiture.persistence.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin;


    private String statut; // "Disponible" ou "Indisponible"

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference 
    @JoinColumn(name = "car_id")
    private Car car;

    // Getters et Setters

    public Long getId() {
        return id;
    }
    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

  
    public void setDateDebut(LocalDate date) {
        this.dateDebut = date;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
	
	public LocalDate getDateDebut() {
        return dateDebut;
    }

}
