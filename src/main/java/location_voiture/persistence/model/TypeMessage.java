package location_voiture.persistence.model;

public enum TypeMessage {
    CONTACT,        // Pour visiteurs
    NOTIFICATION,   // Pour alertes systèmes
    ALERTE,         // Pour litiges ou avertissements
    ADMIN,          // Pour messages liés à l’administration
    INTERNE         // Pour messages internes client <-> propriétaire
}
