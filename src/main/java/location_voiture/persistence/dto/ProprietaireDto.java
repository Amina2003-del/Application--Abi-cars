package location_voiture.persistence.dto; // Ou votre package DTO

import lombok.Data;

@Data
// import lombok.Getter; // Optionnel si vous utilisez Lombok
// import lombok.Setter; // Optionnel si vous utilisez Lombok
// import lombok.NoArgsConstructor; // Optionnel si vous utilisez Lombok
// @Getter @Setter @NoArgsConstructor // Si vous utilisez Lombok
public class ProprietaireDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String tel;


    // Constructeur vide EST IMPORTANT pour Jackson dans certains cas
    public ProprietaireDto() {
    }

    public ProprietaireDto(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // GETTERS PUBLICS SONT INDISPENSABLES POUR JACKSON
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // SETTERS (moins critiques pour la sérialisation, mais bons pour la désérialisation/construction)
    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    
	public void setEmail(String email) {
this.email=email;
	}

	public void setTel(String tel) {
this.tel=tel;		
	}
}