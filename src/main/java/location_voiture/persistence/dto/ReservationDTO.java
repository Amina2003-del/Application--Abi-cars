package location_voiture.persistence.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import location_voiture.persistence.model.Reservation;

public class ReservationDTO {

    private String nomClient;
    private String emailClient;
    private String numeroPermisClient;

    private String marque;
    private String modele;
    private String immatriculation;

    private String dateDebut;  // formaté en "yyyy-MM-dd"
    private String dateFin; 
    private String adressePriseEnCharge;
    private String adresseRestitution;// formaté en "yyyy-MM-dd"

    private double prix;
    private String statutReservation;

    private String statutPaiement;
    private String methodePaiement;
    private Long id;
  
    private double amount;
    private String statut;

    private String carModele;
    private String carImmatriculation;

    private String clientFirstName;
    private String clientLastName;
    private String clientEmail;
    private String clientNumeroPermis;
    private String typeReservation; 
    // Constructeur vide pour Jackson
    public ReservationDTO() {}
    public String getAdressePriseEnCharge() {
        return adressePriseEnCharge;
    }
    public String getTypeReservation() {
        return typeReservation;
    }

    public void setTypeReservation(String typeReservation) {
        this.typeReservation = typeReservation;
    }

    public void setAdressePriseEnCharge(String adressePriseEnCharge) {
        this.adressePriseEnCharge = adressePriseEnCharge;
    }

    public String getAdresseRestitution() {
        return adresseRestitution;
    }

    public void setAdresseRestitution(String adresseRestitution) {
        this.adresseRestitution = adresseRestitution;
    }
    // Constructeur complet
    public ReservationDTO(String nomClient, String emailClient, String numeroPermisClient,
                          String marque, String modele, String immatriculation,
                          String dateDebut, String dateFin,
                          double prix, String statutReservation,
                          String statutPaiement, String methodePaiement) {
        this.nomClient = nomClient;
        this.emailClient = emailClient;
        this.numeroPermisClient = numeroPermisClient;
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prix = prix;
        this.statutReservation = statutReservation;
        this.statutPaiement = statutPaiement;
        this.methodePaiement = methodePaiement;
    }

    // Méthode de conversion depuis l'entité Reservation
    public static ReservationDTO fromEntity(Reservation reservation) {
        if (reservation == null) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Client
        String nomClient = "Inconnu";
        String emailClient = "";
        String numeroPermisClient = "";
        if (reservation.getUtilisateur() != null) {
            nomClient = (reservation.getUtilisateur().getFirstName() != null ? reservation.getUtilisateur().getFirstName() : "") +
                        " " +
                        (reservation.getUtilisateur().getLastName() != null ? reservation.getUtilisateur().getLastName() : "");
            emailClient = reservation.getUtilisateur().getEmail() != null ? reservation.getUtilisateur().getEmail() : "";
            numeroPermisClient = reservation.getUtilisateur().getNumeroPermis() != null ? reservation.getUtilisateur().getNumeroPermis() : "";
        }

        // Voiture
        String marque = "Marque inconnue";
        String modele = "Modèle inconnu";
        String immatriculation = "Immatriculation inconnue";
        if (reservation.getVoiture() != null) {
            marque = reservation.getVoiture().getMarque() != null ? reservation.getVoiture().getMarque() : marque;
            modele = reservation.getVoiture().getModele() != null ? reservation.getVoiture().getModele() : modele;
            immatriculation = reservation.getVoiture().getImmatriculation() != null ? reservation.getVoiture().getImmatriculation() : immatriculation;
        }

        // Dates
        String dateDebut = reservation.getDateDebut() != null ? reservation.getDateDebut().format(formatter) : null;
        String dateFin = reservation.getDateFin() != null ? reservation.getDateFin().format(formatter) : null;

        // Statut réservation
        String statutReservation = reservation.getStatut() != null ? reservation.getStatut().name() : "Statut inconnu";

        // Paiement
        String statutPaiement = "";
        String methodePaiement = "";
        if (reservation.getPaiement() != null) {
            methodePaiement = reservation.getPaiement().getMethodePaiement() != null ? reservation.getPaiement().getMethodePaiement() : "";
        }

        ReservationDTO dto = new ReservationDTO(
            nomClient, emailClient, numeroPermisClient,
            marque, modele, immatriculation,
            dateDebut, dateFin,
            reservation.getPrix(), statutReservation,
            statutPaiement, methodePaiement
        );

        // Affectation des champs supplémentaires
        dto.setId(reservation.getId());
        dto.setCarModele(marque + " " + modele);
        dto.setCarImmatriculation(immatriculation);
        if (reservation.getTypeReservation() != null) {
            dto.setTypeReservation(reservation.getTypeReservation().name()); // récupère DISTANCE ou PRESENTIELLE
        }

        return dto;
    }


    // Getters et setters

    public String getNomClient() {
        return nomClient;
    }
    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getEmailClient() {
        return emailClient;
    }
    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public String getNumeroPermisClient() {
        return numeroPermisClient;
    }
    public void setNumeroPermisClient(String numeroPermisClient) {
        this.numeroPermisClient = numeroPermisClient;
    }

    public String getMarque() {
        return marque;
    }
    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }
    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }


   

    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getStatutReservation() {
        return statutReservation;
    }
    public void setStatutReservation(String statutReservation) {
        this.statutReservation = statutReservation;
    }
 

        // Getters et Setters

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(String dateDebut) {
            this.dateDebut = dateDebut;
        }

        public String getDateFin() {
            return dateFin;
        }

        public void setDateFin(String dateFin) {
            this.dateFin = dateFin;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getStatut() {
            return statut;
        }

        public void setStatut(String statut) {
            this.statut = statut;
        }

        public String getCarModele() {
            return carModele;
        }

        public void setCarModele(String carModele) {
            this.carModele = carModele;
        }

        public String getCarImmatriculation() {
            return carImmatriculation;
        }

        public void setCarImmatriculation(String carImmatriculation) {
            this.carImmatriculation = carImmatriculation;
        }

        public String getClientFirstName() {
            return clientFirstName;
        }

        public void setClientFirstName(String clientFirstName) {
            this.clientFirstName = clientFirstName;
        }

        public String getClientLastName() {
            return clientLastName;
        }

        public void setClientLastName(String clientLastName) {
            this.clientLastName = clientLastName;
        }

        public String getClientEmail() {
            return clientEmail;
        }

        public void setClientEmail(String clientEmail) {
            this.clientEmail = clientEmail;
        }

        public String getClientNumeroPermis() {
            return clientNumeroPermis;
        }

        public void setClientNumeroPermis(String clientNumeroPermis) {
            this.clientNumeroPermis = clientNumeroPermis;
        }
    

    public String getStatutPaiement() {
        return statutPaiement;
    }
    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public String getMethodePaiement() {
        return methodePaiement;
    }
    public void setMethodePaiement(String methodePaiement) {
        this.methodePaiement = methodePaiement;
    }

	public void setDateFin(LocalDate dateFin2) {
		// TODO Auto-generated method stub
		
	}

	public void setPrixtotale(double prix2) {
		// TODO Auto-generated method stub
		
	}

	public String getCarFullName() {
	    return (marque != null ? marque : "Marque inconnue") + " " +
	           (modele != null ? modele : "Modèle inconnu") + " - " +
	           (immatriculation != null ? immatriculation : "Immatriculation inconnue");
	}

}
