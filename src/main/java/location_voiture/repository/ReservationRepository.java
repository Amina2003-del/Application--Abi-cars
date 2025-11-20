package location_voiture.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutReservation;
import ma.abisoft.persistence.model.User;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findTop6ByOrderByDateDebutDesc();


    @Query("SELECT SUM(r.prixTotal) FROM Reservation r WHERE FUNCTION('MONTH', r.dateDebut) = :mois")
    Double sommeRevenuDuMois(@Param("mois") int mois);

    List<Reservation> findTop5ByOrderByIdDesc();
    
    // Méthode modifiée pour accepter un objet StatutReservation
    long countByStatut(StatutReservation statut);

    @Query("SELECT SUM(r.prixTotal) " +
           "FROM Reservation r " +
           "GROUP BY FUNCTION('MONTH', r.dateDebut) " +
           "ORDER BY FUNCTION('MONTH', r.dateDebut)")
    List<Double> revenusParMois();
    
    @Query("SELECT r FROM Reservation r WHERE r.statut = :statut")
    List<Reservation> findByStatut(StatutReservation statut);
    
    List<Reservation> findByLocataire(Locataire locataire);  // Pour trouver toutes les réservations d'un locataire
    List<Reservation> findByVoiture(Car voiture);
    List<Reservation> findByVoiture_Id(Long voitureId);


	List<Reservation> findByVoiture(Long id);
	
    long count();

    List<Reservation> findByUtilisateur_Id(Long utilisateurId);


    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.dateDebut BETWEEN :start AND :end")
    Long countByMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);


    @Query("SELECT COUNT(r) FROM Reservation r WHERE DATE(r.dateDebut) = :date")
    Long countByDay(@Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE r.utilisateur.email = :email")
    List<Reservation> findByUserEmail(@Param("email") String email);



	// Vérifier la disponibilité d'une voiture pour une période donnée en utilisant dateDebut et dateFin
    @Query("SELECT r FROM Reservation r WHERE r.voiture = :carId AND (r.dateDebut < :endDate AND r.dateFin > :startDate)")
    List<Reservation> findByCarAndDateRange(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT r FROM Reservation r WHERE r.voiture.id = :carId AND " +
    	       "(:startDate <= r.dateFin AND :endDate >= r.dateDebut)")
    	List<Reservation> findConflictingReservations(@Param("carId") Long carId,
    	                                              @Param("startDate") LocalDate startDate,
    	                                              @Param("endDate") LocalDate endDate);


	List<Reservation> findTop5ByOrderByDateDebutDesc();



	// Méthode pour calculer la somme des prix
    @Query("SELECT SUM(r.prixTotal) FROM Reservation r")
    Double sumTotalPrix();

	
	    // Compter les utilisateurs distincts associés à une réservation
	    @Query("SELECT COUNT(DISTINCT r.utilisateur) FROM Reservation r")
	    long countDistinctUser();
	
	long countByStatut(String statut);

	@Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :id ORDER BY r.dateDebut DESC")
	List<Reservation> findReservationsByUtilisateur(@Param("id") Long utilisateurId);


	List<Reservation> findAll();


	long countByStatutAndUtilisateur_Id(StatutReservation confirmee, Long id);
	 @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut IN :statuts")
	    long countByStatutIn(@Param("statuts") List<StatutReservation> statuts);

	    Optional<Reservation> findFirstByOrderByDateDebutDesc();

	    @Query("SELECT FUNCTION('MONTH', r.dateDebut) as mois, COUNT(r) as nb FROM Reservation r GROUP BY FUNCTION('MONTH', r.dateDebut) ORDER BY mois")
	    List<Object[]> countReservationsGroupedByMonth();


		Optional<Reservation> findTopByOrderByDateDebutDesc();


		@Query("SELECT r FROM Reservation r " +
			       "JOIN r.voiture v " +
			       "JOIN v.proprietaire p " +
			       "JOIN p.user u " +
			       "JOIN u.roles role " +
			       "WHERE u.email = :email AND role.name = 'ROLE_OWNER'")
			List<Reservation> findReservationsByOwnerEmail(@Param("email") String email);





		List<Reservation> findByVoitureIn(List<Car> voitures);


		List<Reservation>findByUtilisateur(User utilisateur);






		@Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :userId AND r.statut = :statut ORDER BY r.dateDebut DESC")
		List<Reservation> findByUtilisateurIdAndStatutOrderByDateDebutDesc(@Param("userId") Long userId, @Param("statut") StatutReservation confirmee);


	




		@Query("SELECT v.modele, COUNT(r) as nb FROM Reservation r JOIN r.voiture v GROUP BY v.modele ORDER BY nb DESC")
		List<Object[]> topVoitures(Pageable pageable);

		@Query("SELECT r.statut, COUNT(r) FROM Reservation r GROUP BY r.statut")
		List<Object[]> countByStatut();

		@Query("SELECT v.ville, COUNT(r) FROM Reservation r JOIN r.voiture v WHERE MONTH(r.dateDebut) = MONTH(CURRENT_DATE) GROUP BY v.ville")
		List<Object[]> occupationParVille();

		@Query("SELECT MONTH(r.dateDebut), SUM(r.prixTotal) FROM Reservation r GROUP BY MONTH(r.dateDebut) ORDER BY MONTH(r.dateDebut)")
		List<Object[]> findMonthlyRevenue();




		List<Reservation> findByVoiture_IdAndStatut(Long voitureId, StatutReservation statut);

	
		  @Query("SELECT r FROM Reservation r WHERE r.voiture.proprietaire.user.email = :email")
		    List<Reservation> findByVoitureProprietaireEmail(@Param("email") String email);

		    @Query("SELECT r FROM Reservation r WHERE r.voiture.proprietaire.user.email = :email")
		    List<Reservation> findByProprietaireEmail(@Param("email") String email);

		    @Query("SELECT r FROM Reservation r WHERE r.voiture.proprietaire.id = :proprietaireId")
		    List<Reservation> findByVoitureProprietaireId(@Param("proprietaireId") Long proprietaireId);


		

		    List<Reservation> findByVoitureProprietaire(Propritaire proprietaire);
		    @Query("SELECT r FROM Reservation r " +
		    	       "WHERE r.locataire.user = :client AND r.voiture.proprietaire = :proprietaire " +
		    	       "ORDER BY r.dateDebut DESC")
		    	List<Reservation> findLastReservationByClientAndOwner(
		    	        @Param("client") User client,
		    	        @Param("proprietaire") Propritaire proprietaire);

		    @Query("SELECT r FROM Reservation r WHERE r.locataire = :client AND r.voiture.proprietaire = :proprietaire ORDER BY r.dateDebut DESC")
		    Reservation findTopByClientAndOwner(@Param("client") User client, @Param("proprietaire") Propritaire proprietaire);

		    
		    @Query("SELECT r FROM Reservation r " +
		            "WHERE r.locataire.user.id = :clientId " +
		            "AND r.voiture.proprietaire.id = :ownerId " +
		            "ORDER BY r.dateDebut DESC")
		     Reservation findLastReservationByClientAndOwner(@Param("clientId") Long clientId,
		                                                     @Param("ownerId") Long ownerId);

		     // Liste des réservations pour un client et un propriétaire
		     @Query("SELECT r FROM Reservation r " +
		            "WHERE r.locataire.user.id = :clientId " +
		            "AND r.voiture.proprietaire.id = :ownerId " +
		            "ORDER BY r.dateDebut DESC")
		     List<Reservation> findReservationsByClientAndOwner(@Param("clientId") Long clientId,
		                                                        @Param("ownerId") Long ownerId);

		     // Liste de toutes les réservations pour un propriétaire
		     @Query("SELECT r FROM Reservation r " +
		            "WHERE r.voiture.proprietaire.id = :ownerId " +
		            "ORDER BY r.dateDebut DESC")
		     List<Reservation> findReservationsByOwner(@Param("ownerId") Long ownerId);
		 
		    		
		 

}

