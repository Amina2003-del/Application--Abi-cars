package location_voiture.persistence.dto;

public class PasswordChangeDTO {
	 private String currentPassword;
	    private String newPassword;
	    private String confirmPassword;
	    
	    
	    public String getCurrentPassword() {
	        return currentPassword;
	    }

	    // Setter pour currentPassword
	    public void setCurrentPassword(String currentPassword) {
	        this.currentPassword = currentPassword;
	    }

	    // Getter pour newPassword
	    public String getNewPassword() {
	        return newPassword;
	    }

	    // Setter pour newPassword
	    public void setNewPassword(String newPassword) {
	        this.newPassword = newPassword;
	    }

	    // Getter pour confirmPassword
	    public String getConfirmPassword() {
	        return confirmPassword;
	    }

	    // Setter pour confirmPassword
	    public void setConfirmPassword(String confirmPassword) {
	        this.confirmPassword = confirmPassword;
	    }

}
