package location_voiture.persistence.dto;


import java.util.Map;

public class DashboardStatsDTO {
    private long voituresActives;
    private double revenusMois;
    private double tauxOccupation; // en %
    private long approbationsEnAttente;
    // getters & setters
	public void setVoituresActives(Object countByDisponibleTrue) {
		// TODO Auto-generated method stub
		
	}
	 private Map<String, Double> revenusDes6DerniersMois;
	    private Map<String, Long> statutsReservation;

	    public Map<String, Double> getRevenusDes6DerniersMois() {
	        return revenusDes6DerniersMois;
	    }

	    public void setRevenusDes6DerniersMois(Map<String, Double> revenusDes6DerniersMois) {
	        this.revenusDes6DerniersMois = revenusDes6DerniersMois;
	    }

	    public Map<String, Long> getStatutsReservation() {
	        return statutsReservation;
	    }

	    public void setStatutsReservation(Map<String, Long> statutsReservation) {
	        this.statutsReservation = statutsReservation;
	    }
	public void setRevenusMois(Object sumMontantDuMois) {
		// TODO Auto-generated method stub
		
	}
	public void setTauxOccupation(double d) {
		// TODO Auto-generated method stub
		
	}
	public void setApprobationsEnAttente(int i) {
		// TODO Auto-generated method stub
		
	}
	 public Double getRevenusMois() {
	        return revenusMois;
	    }
	 public double getTauxOccupation() {
	        return tauxOccupation;
	    }

	
	    public void setRevenusMois(Double revenusMois) {
	        this.revenusMois = revenusMois;
	    }

	    // Autres getters et setters...

	    public long getApprobationsEnAttente() {
	        return approbationsEnAttente;
	    }

	    public void setApprobationsEnAttente(long approbationsEnAttente) {
	        this.approbationsEnAttente = approbationsEnAttente;
	    }

	    public long getVoituresActives() {
	        return voituresActives;
	    }

	    public void setVoituresActives(long voituresActives) {
	        this.voituresActives = voituresActives;
	    }
}

