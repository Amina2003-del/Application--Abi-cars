package location_voiture.service;

import java.util.List;
import java.util.Map;

import location_voiture.persistence.dto.DashboardStatsDTO;

public interface DashboardService {
	  DashboardStatsDTO getStats();
	    Map<String, Double> getRevenusDes6DerniersMois();
	    Map<String, Long> getStatutsReservation();
	    List<String> getTachesEnAttente();
	    List<String> getActivitesRecentes();
		Long countReservationsByMonth(int month);
}
