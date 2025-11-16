package ma.abisoft.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import ma.abisoft.validation.PasswordMatches;
import ma.abisoft.validation.ValidEmail;
import ma.abisoft.validation.ValidPassword;

@PasswordMatches
public class UserDto {
    @NotNull
    @Size(min = 1, message = "{Size.userDto.firstName}")
    private String firstName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.lastName}")
    private String lastName;
    @NotNull
    private String role; // ex: "ROLE_OWNER" ou "ROLE_CLIENT"
    @NotNull(message = "La description de l'agence est requise")
    private String descriptionAgence;

    @ValidPassword
    private String password;
    private MultipartFile logovoiture;  // Le fichier uploadé (input form)
    private String logovoitureFileName; // Le nom du fichier sauvegardé

    public MultipartFile getLogovoiture() { return logovoiture; }
    public void setLogovoiture(MultipartFile logovoiture) { this.logovoiture = logovoiture; }

    public String getLogovoitureFileName() { return logovoitureFileName; }
    public void setLogovoitureFileName(String logovoitureFileName) { this.logovoitureFileName = logovoitureFileName; }
      private String numeroPermis;
;
    private String adresse;

   

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;
    public String getDescriptionAgence() {
        return descriptionAgence;
    }

    public void setDescriptionAgence(String descriptionAgence) {
        this.descriptionAgence = descriptionAgence;
    }

    @NotNull
    @Size(min = 10, message = "{Size.userDto.tel}")
    private String tel;

    private boolean isUsing2FA;

    private String raisonsociale;

    @NotNull(message = "Le champ ICE est requis")
    private String ice;


    private String userType; // CLIENT, OWNER...

    private String fullName;
    private String telephone;

    // === Getters / Setters ===
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMatchingPassword() { return matchingPassword; }
    public void setMatchingPassword(String matchingPassword) { this.matchingPassword = matchingPassword; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public boolean isUsing2FA() { return isUsing2FA; }
    public void setUsing2FA(boolean using2FA) { isUsing2FA = using2FA; }

    public String getRaisonsociale() { return raisonsociale; }
    public void setRaisonsociale(String raisonsociale) { this.raisonsociale = raisonsociale; }

    public String getIce() { return ice; }
    public void setIce(String ice) { this.ice = ice; }

  

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    @Override
    public String toString() {
        return "UserDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
	public void setRole(int intValue) {
		// TODO Auto-generated method stub
		
	}

    public String getNumeroPermis() {
        return numeroPermis;
    }

    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }

}
