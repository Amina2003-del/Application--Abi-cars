package location_voiture.persistence.dto;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import location_voiture.persistence.model.StatutEntretien;

/**
 * DTO pour transférer les données d'un entretien entre le backend et le frontend.
 */
public class EntretienDTO {

    private Long id;
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    private Double cout;
    private String fichierFacturePath;
    private String observations;
    private String commentaire;
    private Boolean periodique;
    private Integer prochainKmEstime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prochaineDateEstimee;

    private boolean rappelEnvoye;
    private StatutEntretien statut;
    private Integer supprimer;

    // 🔗 Voiture liée à l’entretien
    private CarDTO car;

    // ==============================
    // 🔹 Constructeurs
    // ==============================
    public EntretienDTO() {
    }

    public EntretienDTO(Long id, String type, LocalDate dateDebut, LocalDate dateFin, Double cout,
                        String fichierFacturePath, String observations, String commentaire,
                        Boolean periodique, Integer prochainKmEstime, LocalDate prochaineDateEstimee,
                        boolean rappelEnvoye, StatutEntretien statut, Integer supprimer, CarDTO car) {
        this.id = id;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.cout = cout;
        this.fichierFacturePath = fichierFacturePath;
        this.observations = observations;
        this.commentaire = commentaire;
        this.periodique = periodique;
        this.prochainKmEstime = prochainKmEstime;
        this.prochaineDateEstimee = prochaineDateEstimee;
        this.rappelEnvoye = rappelEnvoye;
        this.statut = statut;
        this.supprimer = supprimer;
        this.car = car;
    }

    // ==============================
    // 🔹 Getters et Setters
    // ==============================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getPeriodique() {
        return periodique;
    }

    public void setPeriodique(Boolean periodique) {
        this.periodique = periodique;
    }

    public Integer getProchainKmEstime() {
        return prochainKmEstime;
    }

    public void setProchainKmEstime(Integer prochainKmEstime) {
        this.prochainKmEstime = prochainKmEstime;
    }

    public LocalDate getProchaineDateEstimee() {
        return prochaineDateEstimee;
    }

    public void setProchaineDateEstimee(LocalDate prochaineDateEstimee) {
        this.prochaineDateEstimee = prochaineDateEstimee;
    }

    public boolean isRappelEnvoye() {
        return rappelEnvoye;
    }

    public void setRappelEnvoye(boolean rappelEnvoye) {
        this.rappelEnvoye = rappelEnvoye;
    }

    public StatutEntretien getStatut() {
        return statut;
    }

    public void setStatut(StatutEntretien statut) {
        this.statut = statut;
    }

    public Integer getSupprimer() {
        return supprimer;
    }

    public void setSupprimer(Integer supprimer) {
        this.supprimer = supprimer;
    }

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
    }
}
