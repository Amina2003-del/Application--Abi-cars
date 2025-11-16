package location_voiture.persistence.dto;

import java.time.LocalDate;

public class SearchCriteria {
    private String adressePriseEnCharge;
    private String adresseRestitution;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String typeVoiture;
}