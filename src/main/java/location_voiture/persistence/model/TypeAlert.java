package location_voiture.persistence.model;

public enum TypeAlert {
    RESERVATION("Réservation"),
    LITIGE("Litige"),
    PAIEMENT("Paiement"),
    ALERTE("Alerte"),
    NOTIFICATION("Notification");

    private final String description;

    TypeAlert(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
