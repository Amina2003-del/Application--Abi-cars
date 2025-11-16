package location_voiture.persistence.dto;

import ma.abisoft.persistence.model.User;

public class ClientDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String tel;
    private String numeroPermis;
    private boolean enabled;
    private String fullName;

    public ClientDTO() {
    }

    public ClientDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.tel = user.getTel();
        this.numeroPermis = user.getNumeroPermis();
        this.enabled = user.isEnabled();
        this.fullName = user.getFirstName() + " " + user.getLastName();

    }

    // Getters et Setters
    public Long getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
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
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getNumeroPermis() {
        return numeroPermis;
    }
    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static ClientDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        ClientDTO dto = new ClientDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setTel(user.getTel());
        
        Object permis = user.getNumeroPermis();
        dto.setNumeroPermis(permis != null ? permis.toString() : null);

        dto.setEnabled(user.isEnabled());

        return dto;
    }

}
