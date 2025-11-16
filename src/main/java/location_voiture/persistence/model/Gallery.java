package location_voiture.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Gallery") // Nom personnalisé de la table
@Data

public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_image", nullable = false, length = 512)
    private String urlImage;
    
    public Gallery() {
        this.urlImage = ""; // Valeur par défaut
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voiture_id", nullable = false)
    private Car voiture;

    public Car getVoiture() {
        return voiture;
    }


	public void setImagePath(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setUrl(String string) {
		// TODO Auto-generated method stub
		
	}

	public String getUrlImage() {
        return urlImage;
    }
	public Long getId() {
	    return id;
	}

   
	public void setPrincipale(boolean first) {
		// TODO Auto-generated method stub
		
	}

	public void setNomImage(String fileName) {
		// TODO Auto-generated method stub
		
	}


	public void setFileName(String fileName) {
		// TODO Auto-generated method stub
		
	}

	public void setNom(String fileName) {
		// TODO Auto-generated method stub
		
	}

	public void setType(String contentType) {
		// TODO Auto-generated method stub
		
	}

	public void setData(byte[] bytes) {
		// TODO Auto-generated method stub
		
	}

	public void setCar(Car car) {
		// TODO Auto-generated method stub
		
	}

	

	public void setContentType(String contentType) {
		// TODO Auto-generated method stub
		
	}

	public void setImageURL(String urlImage) {
	    this.urlImage = urlImage;
	}

	public void setVoiture(Car voiture) {
	    this.voiture = voiture;
	}

	public void setUrlImage(String urlImage) {
	    this.urlImage = urlImage;
	}

	
	

	
}