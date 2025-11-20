package location_voiture.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import location_voiture.persistence.dto.ReservationRequest;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Reservation;
import location_voiture.repository.PaiementRepository;
import location_voiture.service.ReservationService;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.UserService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // ✅ Injecter l'instance correctement

    @PostMapping("/reserver")
    public ResponseEntity<?> reserver(@RequestBody ReservationRequest reservationRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User utilisateur = userService.findByEmail(email);

        if (utilisateur == null) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Utilisateur non trouvé. Veuillez vous connecter."));
        }

        String message = reservationService.reserverVoiture(
                reservationRequest.getVoitureId(),
                reservationRequest.getDateDebut(),
                reservationRequest.getDateFin(),
                utilisateur);

        return ResponseEntity.ok(new ResponseMessage("Reservation réussie!"));
    }


    @PostMapping("/verifierUtilisateur")
    public ResponseEntity<Map<String, String>> verifierUtilisateur(@RequestBody User userInput) {
        // Récupérer les informations saisies par l'utilisateur
        String email = userInput.getEmail();
        String firstName = userInput.getFirstName();
        String lastName = userInput.getLastName();
        String tel = userInput.getTel();

        // Vérifier si un utilisateur existe dans la base de données avec ces informations
        User utilisateur = userRepository.findByEmailAndTel(email, tel);

        Map<String, String> response = new HashMap<>();

        if (utilisateur != null) {
            // Vérification si le prénom et le nom correspondent
            if (utilisateur.getFirstName().equals(firstName) && utilisateur.getLastName().equals(lastName)) {
                // Si tout correspond, l'utilisateur existe
                String fullNameWithId = utilisateur.getFullNameWithId();
                response.put("message", "Utilisateur existe : " + fullNameWithId);
            } else {
                // Si le prénom ou nom ne correspondent pas
                response.put("message", "Utilisateur trouvé, mais le nom/prénom ne correspondent pas.");
            }
        } else {
            // Si aucun utilisateur ne correspond, afficher qu'il s'agit d'un nouvel utilisateur
            response.put("message", "Nouvel utilisateur");
        }

        return ResponseEntity.ok(response);
    }


    // Classe interne pour la réponse JSON
    public static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
  
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservationsWithFilter(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String clientName) {
        try {
            List<Reservation> reservations = reservationService.getReservationsWithFilter(status, clientName);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            e.printStackTrace(); // Debug dans console backend
            return ResponseEntity.status(500).build();
        }
    }


    // Récupérer une réservation par ID
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
    	Reservation reservation = reservationService.getReservationById(id);
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        }
        return ResponseEntity.notFound().build();
    }

    // Confirmer une réservation
    @PutMapping("/confirm/{id}")
    public ResponseEntity<String> confirmReservation(@PathVariable Long id) {
        boolean isConfirmed = reservationService.confirmReservation(id);
        if (isConfirmed) {
            return ResponseEntity.ok("Reservation confirmée.");
        }
        return ResponseEntity.status(400).body("Erreur lors de la confirmation de la réservation.");
    }

    // Refuser une réservation
    @PutMapping("/refuse/{id}")
    public ResponseEntity<String> refuseReservation(@PathVariable Long id) {
        boolean isRefused = reservationService.refuseReservation(id);
        if (isRefused) {
            return ResponseEntity.ok("Reservation refusée.");
        }
        return ResponseEntity.status(400).body("Erreur lors du refus de la réservation.");
    }

    // Modifier une réservation (mettre à jour les informations de la réservation)
    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modifyReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        boolean isModified = reservationService.modifyReservation(id, reservation);
        if (isModified) {
            return ResponseEntity.ok("Reservation modifiée.");
        }
        return ResponseEntity.status(400).body("Erreur lors de la modification de la réservation.");
    }
    
}
