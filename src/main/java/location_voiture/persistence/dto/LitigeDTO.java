package location_voiture.persistence.dto;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Litige;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutLitige;
import ma.abisoft.persistence.model.User;

public class LitigeDTO {

    private Long id;
    private Long reservationId;
    private String type;
    private String statut;
    private String description;
    private String resolution;
    private ClientDTO client;       // nom complet client
    private String vehicle;      // marque + modèle
    private String dateCreation; // date formatée

    public LitigeDTO(Litige litige) {
        this.id = litige.getId();

        // Vérification de la réservation
        if (litige.getReservation() != null) {
            this.reservationId = litige.getReservation().getId();

            // ✅ Extraction du client (utilisateur)
            User user = litige.getReservation().getUtilisateur();
            if (user != null) {
                this.client = new ClientDTO(user);
            } else {
                this.client = new ClientDTO();
                this.client.setFullName("Client inconnu");
            }

            // ✅ Extraction de la voiture
            Car car = litige.getReservation().getVoiture();
            if (car != null) {
                this.vehicle = car.getMarque() + " " + car.getModele() + " (" + car.getImmatriculation() + ")";
            } else {
                this.vehicle = "Véhicule inconnu";
            }

        } else {
            // Aucun lien avec une réservation
            this.reservationId = null;
            this.client = new ClientDTO();
            this.client.setFullName("Client inconnu");
            this.vehicle = "N/A";
        }

        // ✅ Type et statut
        this.type = litige.getType() != null ? litige.getType().name() : "Type non défini";
        this.statut = litige.getStatut() != null ? litige.getStatut().name() : "Statut non défini";
        this.description = litige.getDescription() != null ? litige.getDescription() : "Aucune description";
        this.resolution = litige.getResolution();

        // ✅ Formatage date de création
        if (litige.getDateCreation() != null) {
            this.dateCreation = litige.getDateCreation()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else {
            this.dateCreation = "Date inconnue";
        }

        // ✅ Fichier joint (preuve)
        this.attachmentPath = litige.getAttachmentPath();
    }

    
    
    
    public LitigeDTO() {
		// TODO Auto-generated constructor stub
	}
	public LitigeDTO(Object object, String statutStr) {
		// TODO Auto-generated constructor stub
	}
	public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getType() {
        return type;
    }

    public String getStatut() {
        return statut;
    }

    public String getDescription() {
        return description;
    }
    private String attachmentPath; // <-- chemin/fichier preuve


    public String getResolution() {
        return resolution;
    }
    public ClientDTO getClient() { return client; }
    public String getVehicle() { return vehicle; }
    public String getDateCreation() { return dateCreation; }
   

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }


	public void setHistorique(List<HistoriqueDTO> historique) {
		// TODO Auto-generated method stub
		
	}


	public void setDocuments(List<DocumentDTO> documents) {
		// TODO Auto-generated method stub
		
	}
	
	
	public static LitigeDTO fromEntity(Litige litige) {
	    if (litige == null) {
	        return null;
	    }

	    String statutStr = "Statut inconnu";

	    if (litige.getStatut() != null) {
	        statutStr = litige.getStatut().toString(); // ou name()
	    }

	    return new LitigeDTO(
	        litige.getDescription() != null ? litige.getDescription() : "Description inconnue",
	        statutStr
	    );
	}




	public void setStatut(StatutLitige statut2) {
		// TODO Auto-generated method stub
		
	}
}
