package location_voiture.event;

import location_voiture.persistence.model.Alert;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.TypeAlert;
import location_voiture.repository.AlertRepository;
import ma.abisoft.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Autowired
    private AlertRepository alertRepository;

    @EventListener
    public void handleReservationEvent(ReservationEvent event) {
        Réservation reservation = event.getReservation();
        User owner = reservation.getVoiture().getOwner();
        Alert alert = new Alert(
                "Nouvelle réservation",
                "Une nouvelle réservation a été effectuée pour votre voiture : " + reservation.getVoiture().getModele(),
                TypeAlert.RESERVATION,
                owner,
                false
        );
        alertRepository.save(alert);
    }
    // Méthodes similaires pour PaymentEvent, DisputeEvent, MessageEvent
}