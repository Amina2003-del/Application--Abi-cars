package location_voiture.persistence.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationDetailsDTO {

	
	    private Long id;
	    private String statut;
	    private LocalDateTime dateCreation;
	    private LocalDate dateDebut;
	    private LocalDate dateFin;
	    private Integer nombreJours;
	    private Double prixTotal;
	    private String notes;
	    private ClientDTO client;
	    private CarDTO voiture;

	    // Constructeurs
	    public ReservationDetailsDTO() {}

	    public ReservationDetailsDTO(Long id, String statut, LocalDateTime dateCreation, 
	                                LocalDate dateDebut, LocalDate dateFin, Integer nombreJours, 
	                                Double prixTotal, String notes) {
	        this.id = id;
	        this.statut = statut;
	        this.dateCreation = dateCreation;
	        this.dateDebut = dateDebut;
	        this.dateFin = dateFin;
	        this.nombreJours = nombreJours;
	        this.prixTotal = prixTotal;
	        this.notes = notes;
	    }

	    // Getters et Setters
	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }

	    public String getStatut() { return statut; }
	    public void setStatut(String statut) { this.statut = statut; }

	    public LocalDateTime getDateCreation() { return dateCreation; }
	    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

	    public LocalDate getDateDebut() { return dateDebut; }
	    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

	    public LocalDate getDateFin() { return dateFin; }
	    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

	    public Integer getNombreJours() { return nombreJours; }
	    public void setNombreJours(Integer nombreJours) { this.nombreJours = nombreJours; }

	    public Double getPrixTotal() { return prixTotal; }
	    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }

	    public String getNotes() { return notes; }
	    public void setNotes(String notes) { this.notes = notes; }

	    public ClientDTO getClient() { return client; }
	    public void setClient(ClientDTO client) { this.client = client; }

	    public CarDTO getVoiture() { return voiture; }
	    public void setVoiture(CarDTO voiture) { this.voiture = voiture; }
	}

