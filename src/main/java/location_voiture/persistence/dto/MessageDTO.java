package location_voiture.persistence.dto;

import java.time.format.DateTimeFormatter;

import location_voiture.persistence.model.Message;
import location_voiture.persistence.model.TypeMessage;
import ma.abisoft.persistence.model.User;

public class MessageDTO {

    private Long id;
    private String sujet;
    private String content;
    private String sender;
    private String dateEnvoi;
    private Long destinataireId;
    private Long reservationId;
    private boolean lu;
    private String destinataireEmail;  // <-- Ajouté ici
    private String expediteurNom;
    private String expediteurEmail;
    private String type; // <-- ajoute cette ligne

    public String getStatut() {
        return lu ? "Lu" : "Non lu";
    }
    public MessageDTO() {}

    // Constructeur à partir d'une entité Message
    public MessageDTO(Message message) {
        this.id = message.getId();
        this.sujet = message.getSujet();
        this.content = message.getContent();
        this.sender = message.getExpediteur() != null ? message.getExpediteur().getDisplayName() : "Inconnu";
        this.dateEnvoi = message.getDateEnvoi() != null
            ? message.getDateEnvoi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            : "Non précisée";
        this.destinataireId = message.getDestinataire() != null ? message.getDestinataire().getId() : null;
        this.reservationId = message.getReservation() != null ? message.getReservation().getId() : null;
        this.lu = message.isLu();
        this.destinataireEmail = message.getDestinataire() != null ? message.getDestinataire().getEmail() : null;
        if (message.getType() != null) {
            this.type = ((TypeMessage) message.getType()).name();
        } else {
            this.type = null;
        }

        if (message.getExpediteur() != null) {
            this.expediteurNom = message.getExpediteur().getDisplayName();
            this.expediteurEmail = message.getExpediteur().getEmail();
        } else {
            this.expediteurNom = "Inconnu";
            this.expediteurEmail = "Non disponible";
        }
    }

    public static MessageDTO fromEntity(Message message) {
        if (message == null) {
            return null;
        }
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSujet(message.getSujet());
        dto.setContent(message.getContent());
        dto.setSender(message.getExpediteur() != null
            ? ((User) message.getExpediteur()).getDisplayName()
            : "Inconnu");

        dto.setDateEnvoi(message.getDateEnvoi() != null
            ? message.getDateEnvoi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            : "Non précisée");

        dto.setDestinataireId(message.getDestinataire() != null ? message.getDestinataire().getId() : null);
        dto.setReservationId(message.getReservation() != null ? message.getReservation().getId() : null);
        dto.setLu(message.isLu());

        dto.setDestinataireEmail(message.getDestinataire() != null ? message.getDestinataire().getEmail() : null);

        return dto;
    }
   
    private void setLu(boolean lu2) {
		// TODO Auto-generated method stub
		
	}
	public String getDestinataireEmail() { return destinataireEmail; }
    public void setDestinataireEmail(String destinataireEmail) { this.destinataireEmail = destinataireEmail; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(String dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Long getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Long destinataireId) { this.destinataireId = destinataireId; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    
    public String getExpediteurNom() { return expediteurNom; }
    public void setExpediteurNom(String expediteurNom) { this.expediteurNom = expediteurNom; }

    public String getExpediteurEmail() { return expediteurEmail; }
    public void setExpediteurEmail(String expediteurEmail) { this.expediteurEmail = expediteurEmail; }


}