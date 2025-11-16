package location_voiture.persistence.dto;

import java.util.List;

import ma.abisoft.persistence.model.User;

public class UserProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String tel;
    private String numeroPermis;
    private boolean enabled;
    private List<ReservationDTO> reservations;
    private List<AvisDTO> avis;
    private List<LitigeDTO> litiges;
    private List<CarDTO> voituresReservees;

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.tel=user.getTel();   
        
        this.numeroPermis = user.getNumeroPermis(); // Assurez-vous que User a cette méthode
    }


	public UserProfileDTO() {
		// TODO Auto-generated constructor stub
	}


	// Getters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getTel() {
        return tel;
    }

    public String getNumeroPermis() {
        return numeroPermis;
    }
   
    public boolean isEnabled() {
        return enabled;
    }

    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public List<AvisDTO> getAvis() {
        return avis;
    }

    public List<LitigeDTO> getLitiges() {
        return litiges;
    }

    public List<CarDTO> getVoituresReservees() {
        return voituresReservees;
    }

    // Setters
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
        this.email = email;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

  

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }

    public void setAvis(List<AvisDTO> avis) {
        this.avis = avis;
    }

    public void setLitiges(List<LitigeDTO> litiges) {
        this.litiges = litiges;
    }
    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }
    public void setVoituresReservees(List<CarDTO> voituresReservees) {
        this.voituresReservees = voituresReservees;
    }

	
}
