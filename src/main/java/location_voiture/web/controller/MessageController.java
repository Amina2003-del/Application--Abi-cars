package location_voiture.web.controller;

import java.util.List;

import javax.mail.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import location_voiture.persistence.model.Reservation;
import location_voiture.repository.ReservationRepository;
import location_voiture.service.MessageService;

@RestController
@RequestMapping("/Voitures")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private  ReservationRepository reservationRepository;


    // Envoyer un message interne
    @PostMapping("/interne")
    public ResponseEntity<String> envoyerMessageInterne(@RequestParam Long destinataireId, @RequestParam String contenu, @RequestParam Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation non trouvée"));

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
    
    @GetMapping("/messages")
    public String afficherMessages(Model model, @RequestParam Long locataireId) {
        List<Message> messages = messageService.getMessagesPourLocataire(locataireId);
        model.addAttribute("messages", messages);
        return "messages"; // Nom du template Thymeleaf
    }
}
