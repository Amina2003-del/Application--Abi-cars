package location_voiture.persistence.model;

import javax.persistence.*;
import ma.abisoft.persistence.model.User; // Assurez-vous que cet import est correct
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sujet", length = 255)
    private String sujet;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
 // avec les getters/setters:

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    // Ce champ sera utilisé pour le statut d'envoi du SMS/Notification
    @Column(name = "envoye_avec_succes", nullable = false)
    private boolean envoyeAvecSucces = false; // Initialisé à false

    public String getFormattedDate() {
        if (dateEnvoi == null) return "Date inconnue";
        return dateEnvoi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private TypeAlert type; // Votre enum TypeAlert

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore  // <- ignore la relation pendant la sérialisation JSON
    @JoinColumn(name = "utilisateur_id") // Peut être nullable si c'est une alerte de groupe
    private User utilisateur;

  


    // Constructeur par défaut requis par JPA
    public Alert() {
    }

    // Constructeur pratique (peut être étendu)
    public Alert(String sujet, String message, TypeAlert type, User utilisateur, boolean envoyeAvecSucces) {
        this.sujet = sujet;
        this.message = message;
        this.type = type;
        this.utilisateur = utilisateur;
        this.envoyeAvecSucces = envoyeAvecSucces;
        this.dateEnvoi = LocalDateTime.now(); // Ou passé en argument si nécessaire
    }


    // --- Getters et Setters ---


// Le champ s'appelle "envoyeAvecSucces"

    // Et le getter correspondant est (ou devrait être) :
    public boolean isEnvoyeAvecSucces() {
        return envoyeAvecSucces;
    }
    // Et le setter :
    public void setEnvoyeAvecSucces(boolean envoyeAvecSucces) {
        this.envoyeAvecSucces = envoyeAvecSucces;
    }

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    // Getter et Setter pour envoyeAvecSucces
   

    // Getter et Setter pour type (déjà corrects dans votre code)
    public TypeAlert getType() {
        return this.type;
    }

    public void setType(TypeAlert type) {
        this.type = type;
    }

    // Getter et Setter pour utilisateur (déjà corrects dans votre code)
    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Getter et Setter pour cibleGroupe (maintenant implémentés)
   

	public void setEmailEnvoye(boolean sent) {
		// TODO Auto-generated method stub
		
	}
	public void setDate(LocalDateTime now) {
		// TODO Auto-generated method stub
		
	}
	public Alert(String sujet, String message, TypeAlert type, User utilisateur) {
	    this.sujet = sujet;
	    this.message = message;
	    this.type = type;
	    this.utilisateur = utilisateur;
	    this.dateEnvoi = LocalDateTime.now();
	    this.envoyeAvecSucces = false; // TRÈS important
	}

	

    // J'ai supprimé le champ emailEnvoye et ses getters/setters car redondant avec envoyeAvecSucces
    // public Boolean getEmailEnvoye() { return emailEnvoye; }
    // public void setEmailEnvoye(Boolean emailEnvoye) { this.emailEnvoye = emailEnvoye; }

}