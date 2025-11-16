package location_voiture.repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.dto.RevenuParVoitureDTO;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutPaiement;
@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findAll();


	@Query("SELECT FUNCTION('MONTH', p.date) as mois, SUM(p.montant) FROM Paiement p GROUP BY FUNCTION('MONTH', p.date)")
    List<Object[]> getRevenuePerMonth();
    // Exemple d'une méthode de somme avec deux dates (début et fin du mois)
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.date BETWEEN :startDate AND :endDate")
    Double sumMontantDuMois(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    
    
    @Query("SELECT FUNCTION('DATE_FORMAT', p.date, '%b'), SUM(p.montant) " +
            "FROM Paiement p " +
            "WHERE p.date >= CURRENT_DATE - 180 " +  // environ 6 mois
            "GROUP BY FUNCTION('DATE_FORMAT', p.date, '%b') " +
            "ORDER BY MIN(p.date)")
     List<Object[]> getRevenueLast6Months();
     
     @Query("SELECT p FROM Paiement p WHERE p.reservation.dateDebut BETWEEN :startDate AND :endDate")
     List<Paiement> findByReservationDateBetween(
         @Param("startDate") LocalDateTime startDate,
         @Param("endDate") LocalDateTime endDate
     );
     Optional<Paiement> findOptionalByReservationId(Long reservationId);

	Paiement findByReservationId(Long reservationId);
	
	@Query("SELECT new location_voiture.persistence.dto.RevenuParVoitureDTO(c.marque, c.modele, SUM(p.montant), COUNT(p)) " +
		       "FROM location_voiture.persistence.model.Paiement p " +
		       "JOIN p.reservation r " +
		       "JOIN r.voiture c " +
		       "WHERE p.statut = 'PAYE' AND c.proprietaire.id = :proprietaireId " +
		       "GROUP BY c.marque, c.modele")
		List<RevenuParVoitureDTO> getRevenusParVoiture(@Param("proprietaireId") Long proprietaireId);


	List<Paiement> findByStatutAndDateBetween(StatutPaiement valide, LocalDate startOfMonth, LocalDate endOfMonth);



	List<Paiement> findByReservationVoitureProprietaireId(Long proprietaireId);


	Optional<Paiement> findByReservation(Réservation reservation);


	@Query("SELECT p FROM Paiement p WHERE p.date BETWEEN :start AND :end")
	List<Paiement> findPaymentsBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);




}

