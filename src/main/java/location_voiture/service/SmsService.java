package location_voiture.service;

public interface SmsService {
	


	    /**
	     * Envoie un message SMS au numéro de téléphone spécifié.
	     *
	     * @param toPhoneNumber Le numéro de téléphone du destinataire (doit être au format E.164, ex: +33612345678).
	     * @param messageBody Le contenu du message SMS à envoyer.
	     * @return true si le message a été envoyé (ou mis en file d'attente pour envoi) avec succès, false sinon.
	     */
	    boolean sendSms(String toPhoneNumber, String messageBody);

		boolean sendSms(Object phoneNumber, String messageBody);

	}	


