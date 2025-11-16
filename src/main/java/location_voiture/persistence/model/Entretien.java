package location_voiture.persistence.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Entretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔧 Type d'entretien : Vidange, freins, pneus, etc.
    @Column(nullable = false)
    private String type;

    // 📅 Date à laquelle l'entretien a été réalisé ou est prévu
    private LocalDate dateDebut;
    @Column(name = "supprimer", nullable = false)
    private Integer supprimer = 0; // 0 = actif, 1 = supprimé
	
	 @Column(name = "commentaire", length = 500)
	    private String commentaire;


	    public Integer getSupprimer() {
	        return supprimer;
	    }

	    public void setSupprimer(Integer supprimer) {
	        if (supprimer != 0 && supprimer != 1) {
	            throw new IllegalArgumentException("Le champ 'supprimer' doit être 0 ou 1.");
	        }
	        this.supprimer = supprimer;
	    }

	    public String getCommentaire() {
	        return commentaire;
	    }

	    public void setCommentaire(String commentaire) {
	        this.commentaire = commentaire;
	    }
    // 📅 Date estimée de fin (optionnel pour les longs entretiens)
    private LocalDate dateFin;

    // 💸 Coût de l'entretien
    private Double cout;

    // 📄 Chemin du fichier facture (PDF/image)
    private String fichierFacturePath;

    // 📝 Observations ou remarques éventuelles
    @Column(length = 1000)
    private String observations;

    @Column(nullable = false)
    private boolean rappelEnvoye = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEntretien statut;

    // 🔁 Entretien périodique ? Exemple : tous les 6 mois ou tous les 10 000 km
    private Boolean periodique;

    // 📅 Prochaine date d’entretien estimée (pour générer des alertes)
    private LocalDate prochaineDateEstimee;

    // 📏 Kilométrage estimé pour le prochain entretien
    private Integer prochainKmEstime;

    // 🔗 Lien vers la voiture concernée
    @ManyToOne(fetch = FetchType.EAGER) // charger automatiquement la voiture
    @JoinColumn(name = "car_id")
    @JsonIgnoreProperties({"disponibilites", "pannes", "reservations", "avis", "entretiens"})

    private Car car;

    // === Getters et Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    

    // ...

    public StatutEntretien getStatut() {
        return statut;
    }

    public void setStatut(StatutEntretien statut) {
        this.statut = statut;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Double getCout() {
        return cout;
    }

    public void setCout(Double cout) {
        this.cout = cout;
    }

    public String getFichierFacturePath() {
        return fichierFacturePath;
    }

    public void setFichierFacturePath(String fichierFacturePath) {
        this.fichierFacturePath = fichierFacturePath;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }


public boolean isRappelEnvoye() {
    return rappelEnvoye;
}
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "panne_id")
private Panne panne;


public void setRappelEnvoye(boolean rappelEnvoye) {
    this.rappelEnvoye = rappelEnvoye;
}
  

    public Boolean getPeriodique() {
        return periodique;
    }

    public void setPeriodique(Boolean periodique) {
        this.periodique = periodique;
    }

    public LocalDate getProchaineDateEstimee() {
        return prochaineDateEstimee;
    }

    public void setProchaineDateEstimee(LocalDate prochaineDateEstimee) {
        this.prochaineDateEstimee = prochaineDateEstimee;
    }

    public Integer getProchainKmEstime() {
        return prochainKmEstime;
    }

    public void setProchainKmEstime(Integer prochainKmEstime) {
        this.prochainKmEstime = prochainKmEstime;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
