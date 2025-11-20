package location_voiture.persistence.model;

import java.util.ArrayList;

import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import ma.abisoft.persistence.model.User;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Table(name = "voitures")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Car {
	
	public static final String ETAT_DISPONIBLE = "Disponible"; // Correspond à votre base de données
	public static final String ETAT_INDISPONIBLE = "Indisponible";
	@OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Gallery> galerieImages;
	  @Enumerated(EnumType.STRING)
	    @Column(length = 20)
	    private StatutApprobationVoiture statutApprobation;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean visible = true;

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

   
    @Column(nullable = false, length = 50)
    private String marque;

    @Column(nullable = false, length = 50)
    private String modele;

    @Column(name = "valide", nullable = false)
    private boolean valide;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Disponibilite> disponibilites;
    public void setDisponibilites(List<Disponibilite> disponibilites) {
        this.disponibilites = disponibilites;
    }

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Panne> pannes = new ArrayList<>();
    
    public List<Disponibilite> getDisponibilites() {
        return disponibilites;
    }
    @Enumerated(EnumType.STRING)
    private StatutTechnique statutTechnique;


    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }
    public StatutTechnique getStatutTechnique() {
        return statutTechnique;
    }

    public void setStatutTechnique(StatutTechnique statutTechnique) {
        this.statutTechnique = statutTechnique;
    }

    @Column(name = "annee", nullable = false)
    private Integer annee;
    @Column(length = 30)
    private String type;
    
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer places;
    
    @Column(nullable = false)
    private String categorie;
    
    public String getImagePrincipaleURL() {
        return imagePrincipaleURL;
    }
    public String getCategorie() {
        return categorie;
    }
  
    @Column(nullable = false)
    private Integer kilometrage;
    public Integer getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(Integer kilometrage) {
        this.kilometrage = kilometrage;
    }

    @Column(nullable = false)
    private String immatriculation;

    @Column(nullable = false)
    private String carburant;

    @Column(nullable = false)
    private String imagePrincipaleURL;
 
    
   
    @Column(name = "Boite", nullable = false)
    private String boite;
    @Enumerated(EnumType.STRING) // Utiliser l'EnumType.STRING pour stocker les noms des valeurs
    private StatutDemande statut;

    @Column(name = "prix_journalier", nullable = false, precision = 10, scale = 2)
    private Double prixJournalier;

    @Column(length = 100)
    private String ville;

    @Column(columnDefinition = "TEXT")
    private String description;
 

    @ManyToOne(fetch = FetchType.LAZY)
    
    @JoinColumn(name = "proprietaire_id")
    
    @JsonIgnore // Prevent serialization of proprietaire
    private Propritaire proprietaire;

    @OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Gallery> images = new ArrayList<>();

    @OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "voiture")
    private List<Avis> avis = new ArrayList<>();

    // Helper methods
    public String getFullName() {
        return marque + " " + modele + " (" + annee + ")";
    }
    
    @OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gallery> galleries = new ArrayList<>();

    public void addImage(Gallery image) {
        images.add(image);
        image.setCar(this);
    }
    public User getOwner() {
        if (proprietaire != null) {
            return proprietaire.getUser();
        }
        return null;
    }


    public void removeImage(Gallery image) {
        images.remove(image);
        image.setCar(null);
    }
    

    // Getters et setters (bien que @Data génère déjà ces méthodes, vous pouvez les personnaliser si nécessaire)

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    

    public Long getId() {
        return id;
    }
   
   
    public String getType() {
        return type;
    }
    

   
    public void setType(String type) {
        this.type = type;
    }

    public String getCarburant() {
        return carburant;
    }

    public void setCarburant(String carburant) {
        this.carburant = carburant;
    }

    public String getBoite() {
        return boite;
    }

    public void setBoite(String boite) {
        this.boite = boite;
    }

    public int getPlaces() {
        return places;
    }

    public void setPlaces(int places) {
        this.places = places;
    }

    public double getPrixJournalier() {
        return prixJournalier;
    }

    public void setPrixJournalier(double prixJournalier) {
        this.prixJournalier = prixJournalier;
    }

   


    public void setImagePrincipaleURL(String fileName) {
        this.imagePrincipaleURL = fileName;
    }

   

	public void setProprietaire(Propritaire proprietaires) {
	    this.proprietaire = proprietaires;
	}



	public void setKilometrage(int kilometrage) {
this.kilometrage=kilometrage;
	}
	public void setCategorie(String categorie2) {
this.categorie=categorie2;
	}
	public void setStatutApprobation(String statutApprobation2) {
		// TODO Auto-generated method stub
		
	}
	
	public void setDisponible(boolean equalsIgnoreCase) {
		// TODO Auto-generated method stub
		
	}
	public void setproprietaire(Propritaire proprietaireId) {
this.proprietaire=proprietaireId;
	}

	
	public Propritaire getProprietaire() {
		return proprietaire;
		
	}
	  public void setStatutApprobation(StatutApprobationVoiture statutApprobation) {
	        this.statutApprobation = statutApprobation;
	    }

	public StatutApprobationVoiture getStatutApprobation() {
	    // Devrait être simple comme ça :
	    return this.statutApprobation;

	    // Et non quelque chose de compliqué qui pourrait retourner null par erreur :
	    // if (someCondition) { return null; } else { return this.statutApprobation; }
	}
	public Reservation[] getReservations() {
		// TODO Auto-generated method stub
		return null;
	}
	public Integer getPrixTotal() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isDisponible() {
		// TODO Auto-generated method stub
		return false;
	}


	
 
   
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }


	

	public String getLastName() {
		// TODO Auto-generated method stub
		return null;
	}



	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}


	public Double getLatitude() { return latitude; }
	public void setLatitude(Double latitude) { this.latitude = latitude; }
	public Double getLongitude() { return longitude; }
	public void setLongitude(Double longitude) { this.longitude = longitude; }



	public void setApprobation(String approbation) {
		// TODO Auto-generated method stub
		
	}



	public Car orElseThrow(Object object) {
		// TODO Auto-generated method stub
		return null;
	}
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
		
		public Propritaire orElseThrows(Object object) {
			// TODO Auto-generated method stub
			return null;
		}
		 public List<Avis> getAvis() {
		        return this.avis; // Retourne la liste, pas null
		    }

		    // Assurez-vous d'avoir aussi le setter
		    public void setAvis(List<Avis> avis) {
		        this.avis = avis;
		    }
		    public List<Panne> getPannes() {
		        return this.pannes;
		    }

		    public void setPannes(List<Panne> pannes) {
		        this.pannes = pannes;
		    }

}
