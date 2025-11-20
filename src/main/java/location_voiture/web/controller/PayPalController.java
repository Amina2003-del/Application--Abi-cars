package location_voiture.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutReservation;
import location_voiture.repository.FactureRepository;
import location_voiture.service.CarService;
import location_voiture.service.PaiementService;
import location_voiture.service.PayPalService;
import location_voiture.service.ReservationService;
import location_voiture.service.PdfFactureGenerator;

@Controller
@RequestMapping("/Clientes")
public class PayPalController {
	


    private static final Logger logger = LoggerFactory.getLogger(PayPalController.class);

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaiementService paiementService;

    
    @Autowired
    private CarService carService;
    @Autowired
    private PdfFactureGenerator pdfFactureGenerator;

    @Autowired
    private FactureRepository factureRepository;

    @Value("${paypal.client.id}")
    private String paypalClientId;
    
  


    @GetMapping("/paypal/checkout")
    public String showPaypalCheckout(
            @RequestParam(value = "reservationId", required = false) Long reservationId,
            @RequestParam(value = "carId", required = false) Long carId,
            @RequestParam(value = "prixTotal", required = false) Double prixTotal,
            Model model) {
        logger.info("Appel à /paypal/checkout avec reservationId={}, carId={}, prixTotal={}",
                reservationId, carId, prixTotal);

        if (reservationId == null || carId == null || prixTotal == null) {
            logger.error("Paramètres manquants");
            return "redirect:/error?message=Paramètres de paiement manquants";
        }
        

        try {
            Optional<Reservation> reservationOpt = reservationService.findById(reservationId);
            if (reservationOpt.isEmpty()) {
                logger.error("Reservation non trouvée: {}", reservationId);
                return "redirect:/error?message=Reservation non trouvée";
            }

            Reservation reservation = reservationOpt.get();
            if (!reservation.getPrixTotal().equals(prixTotal)) {
                logger.warn("Montant incorrect: attendu {}, reçu {}", reservation.getPrixTotal(), prixTotal);
                return "redirect:/error?message=Erreur de validation du montant";
            }

            model.addAttribute("montant", prixTotal);
            model.addAttribute("description", "Reservation de voiture ID: " + reservationId);
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("carId", carId);
            model.addAttribute("paypalClientId", paypalClientId);
            return "Clientes/PayPal";

        } catch (Exception e) {
            logger.error("Erreur affichage PayPal: {}", e.getMessage());
            return "redirect:/error?message=Erreur lors du chargement de la page de paiement";
        }
    }

    @PostMapping("/paypal/success")
    @ResponseBody
    public ResponseEntity<?> handlePaypalSuccess(@RequestBody Map<String, Object> requestData) {
        try {
            Long reservationId = Long.parseLong(requestData.get("reservationId").toString());
            String transactionId = requestData.get("transactionId").toString();

            boolean isValid = payPalService.validateTransaction(transactionId);
            if (!isValid) {
                throw new RuntimeException("Transaction PayPal non valide");
            }

            Optional<Reservation> reservationOpt = reservationService.findById(reservationId);
            if (reservationOpt.isEmpty()) {
                throw new RuntimeException("Reservation non trouvée");
            }

            Reservation reservation = reservationOpt.get();

            Paiement paiement = paiementService.creerPaiement(reservation.getPrixTotal(), "PayPal", reservation);
            paiement.confirmerPaiement();
            paiementService.confirmerPaiement(paiement.getId());

            reservation.setStatut(StatutReservation.CONFIRMEE);
            reservationService.saveReservation(reservation);

            Facture facture = new Facture();
            facture.setReservation(reservation);
            facture.setClient(reservation.getUtilisateur()); // Correction ici
            facture.setDateEmission(new Date());
            facture.setDateLimite(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000));
            facture.setStatut("payée");
            facture.setModePaiement("PayPal");
            facture.setReferencePaiement(transactionId);
            String factureFilename = "facture_" + reservation.getId() + "_" + System.currentTimeMillis() + ".pdf";
            byte[] pdf = pdfFactureGenerator.genererFacturePDF(facture);
            facture.setFacturePdf(pdf);
            facture.setNomFichier(factureFilename); // optionnel si tu veux l’avoir aussi dans Facture
            factureRepository.save(facture);
            paiementService.save(paiement);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("factureId", facture.getId());
            response.put("redirectUrl", "/Voitures/confirmation?reservationId=" + reservationId + "&carId=" + reservation.getVoiture().getId() +
                    "&factureId=" + facture.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur traitement paiement", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/factures/download/{id}")
    public ResponseEntity<byte[]> downloadFacturePdf(@PathVariable Long id) {
        Optional<Facture> factureOpt = factureRepository.findById(id);
        if (factureOpt.isEmpty() || factureOpt.get().getFacturePdf() == null) {
            return ResponseEntity.notFound().build();
        }

        Facture facture = factureOpt.get();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(facture.getFacturePdf());
    }
    

}
