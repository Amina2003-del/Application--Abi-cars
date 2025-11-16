package location_voiture.persistence.dto;

public class PaiementDTO {
    private Long id;
    private Double montant;
    private String methode;   // ex: "PayPal", "Carte bancaire"
    private String date;      // format ISO, ex: "2025-06-04T14:30:00"
    private String statut;    // ex: "EN_ATTENTE", "PAYE"
    private ClientDTO client;
    private CarDTO voiture;
    private ReservationDTO reservation;
    private String clientNom;
    private String voitureMarque;

    // Constructeurs
    public PaiementDTO() {}

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public String getVoitureMarque() {
        return voitureMarque;
    }

    public void setVoitureMarque(String voitureMarque) {
        this.voitureMarque = voitureMarque;
    }


    public Double getMontant() {
        return montant;
    }
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    

    public String getMethode() {
        return methode;
    }
    public void setMethode(String methode) {
        this.methode = methode;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

    public ClientDTO getClient() {
        return client;
    }
    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public CarDTO getVoiture() {
        return voiture;
    }
    public void setVoiture(CarDTO voiture) {
        this.voiture = voiture;
    }

    public ReservationDTO getReservation() {
        return reservation;
    }
    public void setReservation(ReservationDTO reservation) {
        this.reservation = reservation;
    }

	public void setClientNom(Object nom) {
		// TODO Auto-generated method stub
		
	}


}
