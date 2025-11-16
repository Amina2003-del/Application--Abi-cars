package location_voiture.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Localisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String region;
    private String pays;
    private Double latitude;
    private Double longitude;

    // ✅ Constructeur vide pour JPA
    public Localisation() {
    }

    // ✅ Constructeur avec nom, region, pays (pour l'initialisation sans coordonnées)
    public Localisation(String nom, String region, String pays) {
        this.nom = nom;
        this.region = region;
        this.pays = pays;
    }

    // ✅ Constructeur avec tous les champs (si vous voulez inclure les coordonnées)
    public Localisation(String nom, String region, String pays, Double latitude, Double longitude) {
        this.nom = nom;
        this.region = region;
        this.pays = pays;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ✅ Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Localisation{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", region='" + region + '\'' +
                ", pays='" + pays + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}