package location_voiture.persistence.dto;

import org.springframework.web.multipart.MultipartFile;

import location_voiture.persistence.model.StatutApprobationVoiture;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarDTO {
  
	    private Long id;
	    private String marque;
	    private String modele;
	    private Integer annee;
	    private String type; // Ajouté basé sur vos logs précédents
	    private Integer places; // Ajouté basé sur vos logs précédents
	    private String categorie; // Ajouté basé sur vos logs précédents
	    private Integer kilometrage; // Ajouté basé sur vos logs précédents
	    private String immatriculation;
	    private String carburant; // Ajouté basé sur vos logs précédents
	    private String imagePrincipaleURL;
	    private String boite; // Ajouté basé sur vos logs précédents
	    private Double prixJournalier;
	    private String ville;
	    private String description;
	    private StatutApprobationVoiture statutApprobation;
	    private String disponible; // Peut rester String ou devenir Boolean si vous changez dans l'entité Car
	    private Long proprietaireId;
	    private ProprietaireDto proprietaire;
	    private MultipartFile[] images;

	    // Constructeur par défaut (important pour certaines librairies de sérialisation/désérialisation)
	    public CarDTO() {
	    }
	    public Long getProprietaireId() {
	        return proprietaireId;
	    }

	    public void setProprietaireId(Long proprietaireId) {
	        this.proprietaireId = proprietaireId;
	    }

	    // Getters
	    public Long getId() {
	        return id;
	    }
	   
	   
	    public String getMarque() {
	        return marque != null ? marque : "Marque inconnue";
	    }

	    public String getModele() {
	        return modele != null ? modele : "";
	    }


	    

	    public Integer getAnnee() {
	        return annee;
	    }

	    public String getType() {
	        return type;
	    }

	    public Integer getPlaces() {
	        return places;
	    }

	    public String getCategorie() {
	        return categorie;
	    }

	    public Integer getKilometrage() {
	        return kilometrage;
	    }

	    public String getImmatriculation() {
	        return immatriculation;
	    }

	    public String getCarburant() {
	        return carburant;
	    }

	    public String getImagePrincipaleURL() {
	        return imagePrincipaleURL;
	    }

	    public String getBoite() {
	        return boite;
	    }

	    public Double getPrixJournalier() {
	        return prixJournalier;
	    }

	    public String getVille() {
	        return ville;
	    }

	    public String getDescription() {
	        return description;
	    }

	    public StatutApprobationVoiture getStatutApprobation() {
	        return statutApprobation;
	    }

	    public String getDisponible() {
	        return disponible;
	    }

	    public ProprietaireDto getProprietaire() {
	        return proprietaire;
	    }

	    // Setters
	    public void setId(Long id) {
	        this.id = id;
	    }

	    public void setMarque(String marque) {
	        this.marque = marque;
	    }

	    public void setModele(String modele) {
	        this.modele = modele;
	    }

	    public void setAnnee(Integer annee) {
	        this.annee = annee;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public void setPlaces(Integer places) {
	        this.places = places;
	    }

	    public void setCategorie(String categorie) {
	        this.categorie = categorie;
	    }

	    public void setKilometrage(Integer kilometrage) {
	        this.kilometrage = kilometrage;
	    }

	    public void setImmatriculation(String immatriculation) {
	        this.immatriculation = immatriculation;
	    }

	    public void setCarburant(String carburant) {
	        this.carburant = carburant;
	    }

	    public void setImagePrincipaleURL(String imagePrincipaleURL) {
	        this.imagePrincipaleURL = imagePrincipaleURL;
	    }

	    public void setBoite(String boite) {
	        this.boite = boite;
	    }

	    public void setPrixJournalier(Double prixJournalier) {
	        this.prixJournalier = prixJournalier;
	    }

	    public void setVille(String ville) {
	        this.ville = ville;
	    }

	    public void setDescription(String description) {
	        this.description = description;
	    }

	    public void setStatutApprobation(StatutApprobationVoiture statutApprobation) {
	        this.statutApprobation = statutApprobation;
	    }

	    public void setDisponible(String disponible) {
	        this.disponible = disponible;
	    }

	    public void setProprietaire(ProprietaireDto proprietaire) {
	        this.proprietaire = proprietaire;
	    }
	public MultipartFile[] getImages() {
		return images;
	}


	
	
	public CarDTO(Long id, String marque, String modele, Integer annee, String type, Integer places,
            String categorie, Integer kilometrage, String immatriculation, String carburant,
            String imagePrincipaleURL, String boite, Double prixJournalier, String ville,
            String description, StatutApprobationVoiture statutApprobation, String disponible,
            ProprietaireDto proprietaire,MultipartFile[] images) {
  this.id = id;
  this.marque = marque;
  this.modele = modele;
  this.annee = annee;
  this.type = type;
  this.places = places;
  this.categorie = categorie;
  this.kilometrage = kilometrage;
  this.immatriculation = immatriculation;
  this.carburant = carburant;
  this.imagePrincipaleURL = imagePrincipaleURL;
  this.boite = boite;
  this.prixJournalier = prixJournalier;
  this.ville = ville;
  this.description = description;
  this.statutApprobation = statutApprobation;
  this.disponible = disponible;
  this.proprietaire = proprietaire;
  this.images=images;
}
	public CarDTO(Long id, String marque, String modele, Integer annee, String boite, String ville, String imagePrincipaleURL) {
	    this.id = id;
	    this.marque = marque;
	    this.modele = modele;
	    this.annee = annee;
	    this.boite = boite;
	    this.ville = ville;
	    this.imagePrincipaleURL = imagePrincipaleURL;
	}


	 public String getStatutApprobationAffichage() {
	        if (this.statutApprobation == null) {
	            return "N/A";
	        }
	        // Vous pouvez personnaliser davantage la façon dont l'enum est converti en chaîne si nécessaire
	        // Par exemple, si votre enum a une méthode getLibelle()
	        // return this.statutApprobation.getLibelle();
	        return this.statutApprobation.toString(); // Ou .name() selon ce que vous préférez
	    }

	    // Setter pour initialiser statutApprobation à partir d'une chaîne (par exemple, depuis un formulaire)
	    // Il est important que ce setter puisse aussi gérer "N/A" ou une chaîne vide si cela signifie 'null'
	    public void setStatutApprobation(String statutString) {
	        if (statutString == null || statutString.trim().isEmpty() || "N/A".equalsIgnoreCase(statutString)) {
	            this.statutApprobation = null;
	            return;
	        }
	        try {
	            this.statutApprobation = StatutApprobationVoiture.valueOf(statutString.toUpperCase());
	        } catch (IllegalArgumentException e) {
	            System.err.println("Valeur de statut d'approbation invalide : '" + statutString + "'. Mise à null.");
	            this.statutApprobation = null; // Ou affecter une valeur par défaut, ou lever une exception
	        }


	    }
	

}
