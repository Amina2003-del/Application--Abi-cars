package location_voiture.persistence.dto;

import location_voiture.persistence.model.Propritaire;

public class ProprietaireDTOS {

	  private Long id;
	    private String raisonSociale;
	    private String ice;
	    private String descriptionAgence;
	    private String logoVoiturePath;
	    private Double prixMinParAgence;

	    public ProprietaireDTOS(Propritaire agence, Double prixMin) {
	        this.id = agence.getId();
	        this.raisonSociale = agence.getRaisonSociale();
	        this.ice = agence.getIce();
	        this.descriptionAgence = agence.getDescriptionAgence();
	        this.logoVoiturePath = agence.getLogovoiturePath();
	        this.prixMinParAgence = prixMin;
	    }


}
