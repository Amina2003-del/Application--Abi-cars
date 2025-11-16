package location_voiture.persistence.model;

import javax.persistence.*;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ma.abisoft.persistence.model.User;

import java.util.List;

@Entity
@Table(name = "proprietaire")
//@DiscriminatorValue("PROPRIETAIRE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Propritaire {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	
	@Column(name = "raison_sociale")
    private String raisonsociale;
    @Column(name = "ice")
    private String ice;
    @Column(name = "descriptionAgence")
    private String descriptionAgence;
    

    // ✅ Utilise le getter hérité de `User`
    public String getLogovoiture() {
        return getLogovoiturePath(); // hérité de User
    }
    @Column(name = "logovoiture")
    private String logovoiture;

    public String getLogovoiturePath() {
        return logovoiture;
    }

    public void setLogovoiturePath(String logovoiturePath) {
        this.logovoiture= logovoiturePath;
    }

   
    public void setLogovoiture(MultipartFile multipartFile) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // On pourrait aussi stocker le fichier ici et enregistrer le chemin
            String fileName = multipartFile.getOriginalFilename();
            setLogovoiturePath(fileName);  // ← maintenant ça fonctionne
        }
    }


    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Car> voitures;

    // Getters et setters
    public String getRaisonsociale() { return raisonsociale; }
    public void setRaisonsociale(String raisonsociale) { this.raisonsociale = raisonsociale; }
    public String getIce() { return ice; } // Keep this getter
   
   
    public List<Car> getVoitures() { return voitures; }
    public void setVoitures(List<Car> voitures) { this.voitures = voitures; }

	 public String getRaisonSociale() {
	        return raisonsociale;
	    }

	   

	 @OneToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "user_id", nullable = false)
	    private User user; 

	    public void setIce(String ice) {
	        this.ice = ice;
	    }
	    public String getDescriptionAgence() {
	        return descriptionAgence;
	    }

	    // Setter
	    public void setDescriptionAgence(String descriptionAgence) {
	        this.descriptionAgence = descriptionAgence;
	    }
	    public Long getId() {
	        return id;
	    }

	    // Setter
	    public void setId(Long id) {
	        this.id = id;
	    }
	    public User getUser() {
	        return this.user;
	    }

	    public void setUser(User user) {
	        this.user = user;
	    }
}