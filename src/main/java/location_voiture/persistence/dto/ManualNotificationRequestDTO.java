package location_voiture.persistence.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data

public class ManualNotificationRequestDTO {
	

	public String getNotificationUserIds() {
	    return notificationUserIds;
	}
	 @NotBlank(message = "La cible ne peut pas être vide")
	    private String notificationTarget; // "all", "owners", "clients", "specific"

	    // Utilisé si notificationTarget="specific". Le HTML utilise "notificationUserId"
	    // On peut le mapper dans le service ou le DTO.
	    private String notificationUserIds; // Liste d'IDs sous forme de String séparés par des virgules

	    @NotBlank(message = "Le titre ne peut pas être vide")
	    @Size(max = 255)
	    private String notificationTitle;

	    @NotBlank(message = "Le message ne peut pas être vide")
	    private String notificationMessage;

		public String getNotificationTarget() {
			// TODO Auto-generated method stub
			return null;
		}

		

		public String getNotificationTitle() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getNotificationMessage() {
			// TODO Auto-generated method stub
			return null;
		}

}
