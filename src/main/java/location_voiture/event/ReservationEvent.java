package location_voiture.event;

import location_voiture.persistence.model.Réservation;
import org.springframework.context.ApplicationEvent;

public class ReservationEvent {
    private final Réservation reservation;

    public ReservationEvent(Réservation reservation) {
        this.reservation = reservation;
    }

    public Réservation getReservation() {
        return reservation;
    }
}
