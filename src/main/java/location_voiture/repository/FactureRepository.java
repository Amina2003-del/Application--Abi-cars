package location_voiture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutFacture;
import ma.abisoft.persistence.model.User;

public interface FactureRepository extends JpaRepository<Facture, Long> {

	long countByStatut(StatutFacture enAttente);

	long countByStatut(String statut);

	List<Facture> findByUtilisateurId(Long id);

	List<Facture> findByClient(User user);

	long countByClientAndStatut(User user, String string);

	
	 Optional<Facture> findByReservationId(Long reservationId);


}
