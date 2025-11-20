package location_voiture.event;

import location_voiture.persistence.model.Reservation;
import org.springframework.context.ApplicationEvent;

public class ReservationEvent {
    private final Reservation reservation;

    public ReservationEvent(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
