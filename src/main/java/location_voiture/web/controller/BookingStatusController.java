package location_voiture.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import location_voiture.persistence.model.StatutReservation;
import location_voiture.repository.ReservationRepository;

@RestController
@RequestMapping("/api/booking")
public class BookingStatusController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/status")
    public Map<String, Long> getBookingStatuses() {
        Map<String, Long> statusData = new HashMap<>();
        statusData.put("Active", reservationRepository.countByStatut(StatutReservation.ACTIVE));
        statusData.put("Terminée", reservationRepository.countByStatut(StatutReservation.TERMINEE));
        statusData.put("En Attente", reservationRepository.countByStatut(StatutReservation.EN_ATTENTE));
        statusData.put("Annulée", reservationRepository.countByStatut(StatutReservation.ANNULEE));
        return statusData;
    }
}
