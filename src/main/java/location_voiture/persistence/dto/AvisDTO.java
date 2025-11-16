package location_voiture.persistence.dto;

import location_voiture.persistence.model.Avis;

public class AvisDTO {
	 private int note;
	    private String commentaire;
	    public Long id;
	    public String voitureNom;
	    public String auteurNom;
	 
	    public String date;
	    public AvisDTO(int note, String commentaire) {
	        this.note = note;
	        this.commentaire = commentaire;
	    }

	    public AvisDTO() {
			// TODO Auto-generated constructor stub
		}

		// getters et setters
	    public int getNote() { return note; }
	    public void setNote(int note) { this.note = note; }

	    public String getCommentaire() { return commentaire; }
	    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

		public void setId(Long id) {
			// TODO Auto-generated method stub
			
		}
	
		

		    public AvisDTO(Avis avis) {
		        this.id = avis.getId();
		        this.voitureNom = avis.getVoiture() != null ? avis.getVoiture().getMarque() : "Inconnu";
		        if (avis.getAuteur() == null) {
		            System.out.println("Auteur est null pour avis id=" + avis.getId());
		            this.auteurNom = "Inconnu";
		        } else {
		            String firstName = avis.getAuteur().getFirstName();
		            String lastName = avis.getAuteur().getLastName();

		            boolean firstNameEmpty = (firstName == null || firstName.trim().isEmpty());
		            boolean lastNameEmpty = (lastName == null || lastName.trim().isEmpty());

		            if (firstNameEmpty && lastNameEmpty) {
		                System.out.println("Auteur prénom et nom sont null ou vides pour avis id=" + avis.getId());
		                this.auteurNom = "Inconnu";
		            } else {
		                // Construire le nom complet selon la disponibilité
		                if (firstNameEmpty) {
		                    this.auteurNom = lastName.trim();
		                } else if (lastNameEmpty) {
		                    this.auteurNom = firstName.trim();
		                } else {
		                    this.auteurNom = firstName.trim() + " " + lastName.trim();
		                }
		                System.out.println("Auteur nom complet pour avis id=" + avis.getId() + " = " + this.auteurNom);
		            }
		        }


		        this.note = avis.getNote();
		        this.commentaire = avis.getCommentaire();
		        this.date = avis.getDate() != null ? avis.getDate().toString() : "";
		    }
}
