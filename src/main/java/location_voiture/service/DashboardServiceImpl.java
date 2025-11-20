package location_voiture.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import location_voiture.persistence.dto.DashboardStatsDTO;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutDemande;
import location_voiture.persistence.model.StatutLitige;
import location_voiture.persistence.model.StatutReservation;
import location_voiture.repository.AvisRepository;
import location_voiture.repository.CarRepository;
import location_voiture.repository.DemandePartenariatRepository;
import location_voiture.repository.LitigeRepository;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.ReservationRepository;

@Service
public class DashboardServiceImpl implements DashboardService {
	    private final AvisRepository avisRepository;
	    private final LitigeRepository litigeRepository;
	    
	  
	    @Override
	    public List<String> getTachesEnAttente() {
	        List<String> taches = new ArrayList<>();

	        if (carRepository.countByStatut(StatutDemande.EN_ATTENTE) > 0) {
	            taches.add("Approuver Voitures en attente");
	        }

	        if (reservationRepository.countByStatut(StatutReservation.EN_ATTENTE) > 0) {
	            taches.add("Vérifier Reservations en attente");
	        }

	        if (avisRepository.countByVoiture_ValideFalse() > 0) {
	            taches.add("Modérer Avis en attente");
	        }

	        if (litigeRepository.countByStatut( StatutLitige.OUVERT) > 0) {
	            taches.add("Examiner Litiges ouverts");
	        }

	        return taches;
	    }
	  
	  public DashboardServiceImpl(ReservationRepository reservationRepository,
              CarRepository carRepository,
              AvisRepository avisRepository,
              LitigeRepository litigeRepository) {
this.reservationRepository = reservationRepository;
this.carRepository = carRepository;
this.avisRepository = avisRepository;
this.litigeRepository = litigeRepository;
}
	 @Override
	    public Map<String, Double> getRevenusDes6DerniersMois() {
	        List<Object[]> results = paiementRepository.getRevenueLast6Months();
	        Map<String, Double> revenus = new LinkedHashMap<>();
	        for (Object[] row : results) {
	            String mois = (String) row[0]; // "Jan", "Feb"
	            Double total = (Double) row[1];
	            revenus.put(mois, total);
	        }
	        return revenus;
	    }

	    @Override
	    public Map<String, Long> getStatutsReservation() {
	        Map<String, Long> stats = new LinkedHashMap<>();
	        stats.put("ACTIVE", reservationRepository.countByStatut(StatutReservation.ACTIVE));
	        stats.put("TERMINEE", reservationRepository.countByStatut(StatutReservation.TERMINEE));
	        stats.put("EN_ATTENTE", reservationRepository.countByStatut(StatutReservation.EN_ATTENTE));
	        stats.put("ANNULEE", reservationRepository.countByStatut(StatutReservation.ANNULEE));
	        return stats;
	    }

	 
    
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DemandePartenariatRepository demandeRepository;

    public DashboardStatsDTO getStats() {
        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setRevenusDes6DerniersMois(getRevenusDes6DerniersMois());
        dto.setStatutsReservation(getStatutsReservation());
        // Nombre de voitures actives
       // dto.setVoituresActives(carRepository.countByDisponible("true"));

        // Revenus du mois (Handle possible null value)
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        Double revenusMois = paiementRepository.sumMontantDuMois(startOfMonth, endOfMonth);
        dto.setRevenusMois(revenusMois != null ? revenusMois : 0.0);  // Default to 0.0 if null

        // Taux d'occupation
        long totalVoitures = carRepository.count();
        long nbReservationsActives = reservationRepository.countByStatut(StatutReservation.ACTIVE);  // Use enum value
        dto.setTauxOccupation(totalVoitures > 0 ? ((double) nbReservationsActives / totalVoitures) * 100 : 0);

        // Nombre d'approbations en attente
        dto.setApprobationsEnAttente(carRepository.countByValideFalse()
                + demandeRepository.countByStatut(StatutDemande.EN_ATTENTE)); 

        // Optional: If you plan to use the list of active reservations
        List<Reservation> reservations = reservationRepository.findByStatut(StatutReservation.ACTIVE);  // Use enum value
        // You can process this list if needed, for example:
        // dto.setSomeField(reservations.size());

        return dto;
    }

	@Override
	public List<String> getActivitesRecentes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Long countReservationsByMonth(int month) {
	    LocalDate start = LocalDate.of(LocalDate.now().getYear(), month, 1);
	    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
	    return reservationRepository.countByMonth(start, end);
	}

}
