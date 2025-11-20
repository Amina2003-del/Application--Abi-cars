package location_voiture.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import location_voiture.persistence.dto.MessageDTO;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Message;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.TypeMessage;
import location_voiture.repository.LocataireRepository;
import location_voiture.repository.MessageRepository;
import location_voiture.repository.ReservationRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private LocataireRepository locataireRepository;
    @Autowired

    private  UserRepository userRepository;
    @Autowired
    private  ReservationRepository reservationRepository;

    @Autowired
    private webSocketService webSocketServices;

    // Envoyer un message interne à un locataire
    public void envoyerMessageInterne(Long destinataireId, String contenu, Reservation reservation) {
        Locataire destinataire = locataireRepository.findById(destinataireId)
            .orElseThrow(() -> new RuntimeException("Locataire non trouvé"));

        Message message = new Message();
        message.setContent(contenu);
        message.setReservation(reservation);
        message.setDateEnvoi(LocalDateTime.now());
        message.setLu(false);  // Par défaut, le message n'est pas lu

        messageRepository.save(message);

        // Envoyer la notification en temps réel via WebSocket
        webSocketServices.envoyerNotification(destinataireId, contenu);
    }
    public List<Message> getMessagesForUser(User user) {
        return messageRepository.findByDestinataireOrderByDateEnvoiDesc(user);
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }
    // Envoyer une notification globale à tous les locataires
    public void envoyerNotification(String contenu) {
        Message notification = new Message();
        notification.setContent(contenu);
        notification.setType(TypeMessage.NOTIFICATION);
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setLu(false);  // Par défaut, la notification n'est pas lue

        messageRepository.save(notification);

        // Envoyer la notification en temps réel via WebSocket
        webSocketServices.envoyerNotificationAClients(contenu);
    }

    // Marquer un message comme lu
    public void marquerCommeLu(Long messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        message.setLu(true);
        messageRepository.save(message);
    }

	public List<javax.mail.Message> getMessagesPourLocataire(Long locataireId) {
		// TODO Auto-generated method stub
		return null;
	}

	public long countUnreadMessages() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Map<String, Object>> getMessages() {
		// TODO Auto-generated method stub
		return null;
	}
	public Message envoyerMessageDepuisDto(MessageDTO dto, User expediteur) {
	    Message message = new Message();
	    message.setContent(dto.getContent());
	    message.setDateEnvoi(LocalDateTime.now());
	    message.setLu(false);
	    message.setType(TypeMessage.INTERNE);
	    message.setExpediteur(expediteur); // Client connecté

	    // Récupération de la réservation
	    if (dto.getReservationId() != null) {
	        Reservation reservation = reservationRepository.findById(dto.getReservationId())
	                .orElseThrow(() -> new IllegalArgumentException("Reservation inconnue"));
	        message.setReservation(reservation);

	        // Récupération de la voiture associée
	        Car car = reservation.getCar();
	        if (car == null || car.getProprietaire() == null) {
	            throw new IllegalArgumentException("Voiture ou propriétaire introuvable pour la réservation");
	        }

	        // Affectation automatique du propriétaire comme destinataire
	        Propritaire proprietaire = car.getProprietaire();
	        if (proprietaire != null && proprietaire.getUser() != null) {
	            User destinataire = proprietaire.getUser(); // ✅ récupère le User réel
	            message.setDestinataire(destinataire);
	        } else {
	            throw new IllegalArgumentException("Reservation obligatoire pour identifier le destinataire automatiquement.");
	        }
	    }

	    return messageRepository.save(message);
	}
	public void save(Message msg) {
	    messageRepository.save(msg);
	}
	public void envoyerMessage(User destinataire, String contenu) {
	    Message msg = new Message();
	    msg.setDestinataire(destinataire);
	    msg.setContet(contenu);
	    msg.setDateEnvoi(LocalDateTime.now());
	    messageRepository.save(msg);
	}
	 public Message findById(Long id) {
	        return messageRepository.findById(id).orElse(null);
	    }
	  public List<Message> getMessagesRecusFiltres(User proprietaire) {
	        List<Message> tousLesMessages = messageRepository.findByDestinataire(proprietaire);
	        
	        // ✅ FILTRER pour garder seulement ceux avec réservation liée aux voitures du propriétaire
	        return tousLesMessages.stream()
	                .filter(m -> m.getReservation() != null)
	                .filter(m -> m.getReservation().getVoiture() != null)
	                .filter(m -> m.getReservation().getVoiture().getProprietaire().equals(proprietaire))
	                .collect(Collectors.toList());
	    }

}
