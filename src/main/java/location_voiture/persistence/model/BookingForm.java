package location_voiture.persistence.model;

import java.time.LocalDateTime;

public class BookingForm {
    // Informations personnelles (Utilisateur)
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Détails de la location (Reservation)
    private String adressePriseEnCharge;
    private String adresseRestitution;
    private String typeVoiture;
    private Integer nbJours;
    private LocalDateTime datePriseEnCharge;
    private String heurePriseEnCharge; // Format texte pour l'heure
    private LocalDateTime dateRestitution;
    private String heureRestitution; // Format texte pour l'heure
    private Long voitureId; // ID de la voiture sélectionnée

    // Commentaire (Avis)
    private String commentaires;

    // Constructeurs
    public BookingForm() {}

    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getAdressePriseEnCharge() { return adressePriseEnCharge; }
    public void setAdressePriseEnCharge(String adressePriseEnCharge) { this.adressePriseEnCharge = adressePriseEnCharge; }
    public String getAdresseRestitution() { return adresseRestitution; }
    public void setAdresseRestitution(String adresseRestitution) { this.adresseRestitution = adresseRestitution; }
    public String getTypeVoiture() { return typeVoiture; }
    public void setTypeVoiture(String typeVoiture) { this.typeVoiture = typeVoiture; }
    public Integer getNbJours() { return nbJours; }
    public void setNbJours(Integer nbJours) { this.nbJours = nbJours; }
    public LocalDateTime getDatePriseEnCharge() { return datePriseEnCharge; }
    public void setDatePriseEnCharge(LocalDateTime datePriseEnCharge) { this.datePriseEnCharge = datePriseEnCharge; }
    public String getHeurePriseEnCharge() { return heurePriseEnCharge; }
    public void setHeurePriseEnCharge(String heurePriseEnCharge) { this.heurePriseEnCharge = heurePriseEnCharge; }
    public LocalDateTime getDateRestitution() { return dateRestitution; }
    public void setDateRestitution(LocalDateTime dateRestitution) { this.dateRestitution = dateRestitution; }
    public String getHeureRestitution() { return heureRestitution; }
    public void setHeureRestitution(String heureRestitution) { this.heureRestitution = heureRestitution; }
    public Long getVoitureId() { return voitureId; }
    public void setVoitureId(Long voitureId) { this.voitureId = voitureId; }
    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
}
