package location_voiture.web.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import location_voiture.persistence.model.Réservation;

import location_voiture.repository.ReservationRepository;
import location_voiture.service.MessageService;
@RestController
@RequestMapping("/messages")
public class ProprietaireMessageController {

    private final ReservationRepository reservationRepository;
	
	

	    @Autowired
	    private MessageService messageService;
	    

    ProprietaireMessageController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }
	    // Envoyer un message interne
	    @PostMapping("/interne")
	    public ResponseEntity<String> envoyerMessageInterne(@RequestParam Long destinataireId, @RequestParam String contenu, @RequestParam Long reservationId) throws RuntimeException {
	        Réservation reservation =reservationRepository.findById(reservationId)
	            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

	        messageService.envoyerMessageInterne(destinataireId, contenu, reservation);
	        return ResponseEntity.ok("Message envoyé avec succès");
	    }

	    // Envoyer une notification globale
	    @PostMapping("/notification")
	    public ResponseEntity<String> envoyerNotification(@RequestParam String contenu) {
	        messageService.envoyerNotification(contenu);
	        return ResponseEntity.ok("Notification envoyée avec succès");
	    }

	    // Marquer un message comme lu
	    @PostMapping("/marquer-lu")
	    public ResponseEntity<String> marquerCommeLu(@RequestParam Long messageId) {
	        messageService.marquerCommeLu(messageId);
	        return ResponseEntity.ok("Message marqué comme lu");
	    }
	}

