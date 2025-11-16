package location_voiture.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import ma.abisoft.persistence.model.User;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonProperty("telephone")
    private String telephone;   
    private String statut;
    private String password;
    private String adresse; // <-- Ajout de l'attribut adresse
    private int voituresCount; // nombre de voitures (pour propriétaires)
    private int reservationsCount; 
    private String permis;
// nombre de réservations (pour clients)
    // Constructeur par défaut
    public UserDTO() {
    }

    public UserDTO(Long id, String firstName, String lastName, String email, String tel, String statut, int voituresCount, int reservationsCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = tel;
        this.statut = statut;
        this.voituresCount = voituresCount;
        this.reservationsCount = reservationsCount;
        this.fullName = firstName + " " + lastName;
        
    }
    public UserDTO(User user) {
        System.out.println("🔄 Constructeur UserDTO(User user) appelé pour: " + user.getEmail());
        System.out.println("🔍 user.isEnabled(): " + user.isEnabled());
        
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName(); // <- corriger ici
        this.email = user.getEmail();
        this.telephone = user.getTel();
        this.statut = user.isEnabled() ? "Actif" : "Inactif";
        
        System.out.println("✅ Statut défini à: " + this.statut);
        
        if (user.getLocataire() != null) {
            this.adresse = user.getLocataire().getAdresse();
            this.permis = user.getLocataire().getNumeroPermis();
        }
    }

   
    private String fullName;

    public String getFullName() {
        if (fullName == null || fullName.isEmpty()) {
            fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        }
        return fullName.trim();
    }


    public int getVoituresCount() {
        return voituresCount;
    }
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setVoituresCount(int VoituresCount) {
        this.voituresCount = VoituresCount;
    }
    public int getreservationsCount() {
        return reservationsCount;
    }

    public void setreservationsCount(int reservationsCount) {
        this.reservationsCount = reservationsCount;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    // Getters et Setters

    public UserDTO(Long id, String lastName, String email, String tel, String statut, int voituresCount, int reservationsCount) {
        this.id = id;
        this.lastName = lastName;
        this.email = email;
        this.telephone = tel;
        this.statut = statut;
        this.voituresCount = voituresCount;
        this.reservationsCount = reservationsCount;
    }


    public UserDTO(String email) {
        this.email = email;
    }


	public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getStatut() { return statut; }
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String tel) {
        this.telephone = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermis() { return permis; }
    public void setPermis(String permis) { this.permis = permis; }

	
	
}


