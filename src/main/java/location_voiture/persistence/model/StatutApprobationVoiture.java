package location_voiture.persistence.model;

public enum StatutApprobationVoiture {
    APPROUVEE("Approuvée"),
    EN_ATTENTE("En attente"),
    REJETEE("Rejetée");

    private final String label;

    // ✅ le constructeur doit porter le même nom que l'enum
    StatutApprobationVoiture(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Convertit une chaîne de caractères en une valeur d'enum StatutApprobationVoiture.
     * Gère les noms des constantes enum (EN_ATTENTE, REJETEE, APPROUVEE)
     * ainsi que des alias en anglais (PENDING, REJECTED, APPROVED).
     * La comparaison est insensible à la casse.
     *
     * @param text La chaîne à convertir.
     * @return La valeur d'enum correspondante, ou null si aucune correspondance n'est trouvée.
     */
    public static StatutApprobationVoiture fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String upperText = text.trim().toUpperCase();

        // Vérification directe avec les noms d'enum
        for (StatutApprobationVoiture b : StatutApprobationVoiture.values()) {
            if (b.name().equals(upperText)) {
                return b;
            }
        }

        // Vérification des alias anglais
        switch (upperText) {
            case "PENDING":
                return EN_ATTENTE;
            case "REJECTED":
                return REJETEE;
            case "APPROVED":
                return APPROUVEE;
            default:
                return null; // ou une valeur par défaut
        }
    }
}
