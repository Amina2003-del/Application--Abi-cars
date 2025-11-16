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
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutReservation;
import ma.abisoft.persistence.model.User;
@Repository
public interface ReservationRepository extends JpaRepository<Réservation, Long> {
    List<Réservation> findTop6ByOrderByDateDebutDesc();


    @Query("SELECT SUM(r.prixTotal) FROM Réservation r WHERE FUNCTION('MONTH', r.dateDebut) = :mois")
    Double sommeRevenuDuMois(@Param("mois") int mois);

    List<Réservation> findTop5ByOrderByIdDesc();
    
    // Méthode modifiée pour accepter un objet StatutReservation
    long countByStatut(StatutReservation statut);

    @Query("SELECT SUM(r.prixTotal) " +
           "FROM Réservation r " +
           "GROUP BY FUNCTION('MONTH', r.dateDebut) " +
           "ORDER BY FUNCTION('MONTH', r.dateDebut)")
    List<Double> revenusParMois();
    
    @Query("SELECT r FROM Réservation r WHERE r.statut = :statut")
    List<Réservation> findByStatut(StatutReservation statut);
    
    List<Réservation> findByLocataire(Locataire locataire);  // Pour trouver toutes les réservations d'un locataire
    List<Réservation> findByVoiture(Car voiture);
    List<Réservation> findByVoiture_Id(Long voitureId);


	List<Réservation> findByVoiture(Long id);
	
    long count();

    List<Réservation> findByUtilisateur_Id(Long utilisateurId);


    @Query("SELECT COUNT(r) FROM Réservation r WHERE r.dateDebut BETWEEN :start AND :end")
    Long countByMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);


    @Query("SELECT COUNT(r) FROM Réservation r WHERE DATE(r.dateDebut) = :date")
    Long countByDay(@Param("date") LocalDate date);

    @Query("SELECT r FROM Réservation r WHERE r.utilisateur.email = :email")
    List<Réservation> findByUserEmail(@Param("email") String email);



	// Vérifier la disponibilité d'une voiture pour une période donnée en utilisant dateDebut et dateFin
    @Query("SELECT r FROM Réservation r WHERE r.voiture = :carId AND (r.dateDebut < :endDate AND r.dateFin > :startDate)")
    List<Réservation> findByCarAndDateRange(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT r FROM Réservation r WHERE r.voiture.id = :carId AND " +
    	       "(:startDate <= r.dateFin AND :endDate >= r.dateDebut)")
    	List<Réservation> findConflictingReservations(@Param("carId") Long carId,
    	                                              @Param("startDate") LocalDate startDate,
    	                                              @Param("endDate") LocalDate endDate);


	List<Réservation> findTop5ByOrderByDateDebutDesc();



	// Méthode pour calculer la somme des prix
    @Query("SELECT SUM(r.prixTotal) FROM Réservation r")
    Double sumTotalPrix();

	
	    // Compter les utilisateurs distincts associés à une réservation
	    @Query("SELECT COUNT(DISTINCT r.utilisateur) FROM Réservation r")
	    long countDistinctUser();
	
	long countByStatut(String statut);

	@Query("SELECT r FROM Réservation r WHERE r.utilisateur.id = :id ORDER BY r.dateDebut DESC")
	List<Réservation> findReservationsByUtilisateur(@Param("id") Long utilisateurId);


	List<Réservation> findAll();


	long countByStatutAndUtilisateur_Id(StatutReservation confirmee, Long id);
	 @Query("SELECT COUNT(r) FROM Réservation r WHERE r.statut IN :statuts")
	    long countByStatutIn(@Param("statuts") List<StatutReservation> statuts);

	    Optional<Réservation> findFirstByOrderByDateDebutDesc();

	    @Query("SELECT FUNCTION('MONTH', r.dateDebut) as mois, COUNT(r) as nb FROM Réservation r GROUP BY FUNCTION('MONTH', r.dateDebut) ORDER BY mois")
	    List<Object[]> countReservationsGroupedByMonth();


		Optional<Réservation> findTopByOrderByDateDebutDesc();


		@Query("SELECT r FROM Réservation r " +
			       "JOIN r.voiture v " +
			       "JOIN v.proprietaire p " +
			       "JOIN p.user u " +
			       "JOIN u.roles role " +
			       "WHERE u.email = :email AND role.name = 'ROLE_OWNER'")
			List<Réservation> findReservationsByOwnerEmail(@Param("email") String email);





		List<Réservation> findByVoitureIn(List<Car> voitures);


		List<Réservation>findByUtilisateur(User utilisateur);






		@Query("SELECT r FROM Réservation r WHERE r.utilisateur.id = :userId AND r.statut = :statut ORDER BY r.dateDebut DESC")
		List<Réservation> findByUtilisateurIdAndStatutOrderByDateDebutDesc(@Param("userId") Long userId, @Param("statut") StatutReservation confirmee);


	




		@Query("SELECT v.modele, COUNT(r) as nb FROM Réservation r JOIN r.voiture v GROUP BY v.modele ORDER BY nb DESC")
		List<Object[]> topVoitures(Pageable pageable);

		@Query("SELECT r.statut, COUNT(r) FROM Réservation r GROUP BY r.statut")
		List<Object[]> countByStatut();

		@Query("SELECT v.ville, COUNT(r) FROM Réservation r JOIN r.voiture v WHERE MONTH(r.dateDebut) = MONTH(CURRENT_DATE) GROUP BY v.ville")
		List<Object[]> occupationParVille();

		@Query("SELECT MONTH(r.dateDebut), SUM(r.prixTotal) FROM Réservation r GROUP BY MONTH(r.dateDebut) ORDER BY MONTH(r.dateDebut)")
		List<Object[]> findMonthlyRevenue();




		List<Réservation> findByVoiture_IdAndStatut(Long voitureId, StatutReservation statut);

	
		  @Query("SELECT r FROM Réservation r WHERE r.voiture.proprietaire.user.email = :email")
		    List<Réservation> findByVoitureProprietaireEmail(@Param("email") String email);

		    @Query("SELECT r FROM Réservation r WHERE r.voiture.proprietaire.user.email = :email")
		    List<Réservation> findByProprietaireEmail(@Param("email") String email);

		    @Query("SELECT r FROM Réservation r WHERE r.voiture.proprietaire.id = :proprietaireId")
		    List<Réservation> findByVoitureProprietaireId(@Param("proprietaireId") Long proprietaireId);


		

		    List<Réservation> findByVoitureProprietaire(Propritaire proprietaire);
		    @Query("SELECT r FROM Réservation r " +
		    	       "WHERE r.locataire.user = :client AND r.voiture.proprietaire = :proprietaire " +
		    	       "ORDER BY r.dateDebut DESC")
		    	List<Réservation> findLastReservationByClientAndOwner(
		    	        @Param("client") User client,
		    	        @Param("proprietaire") Propritaire proprietaire);

		    @Query("SELECT r FROM Réservation r WHERE r.locataire = :client AND r.voiture.proprietaire = :proprietaire ORDER BY r.dateDebut DESC")
		    Réservation findTopByClientAndOwner(@Param("client") User client, @Param("proprietaire") Propritaire proprietaire);

		    
		    @Query("SELECT r FROM Réservation r " +
		            "WHERE r.locataire.user.id = :clientId " +
		            "AND r.voiture.proprietaire.id = :ownerId " +
		            "ORDER BY r.dateDebut DESC")
		     Réservation findLastReservationByClientAndOwner(@Param("clientId") Long clientId,
		                                                     @Param("ownerId") Long ownerId);

		     // Liste des réservations pour un client et un propriétaire
		     @Query("SELECT r FROM Réservation r " +
		            "WHERE r.locataire.user.id = :clientId " +
		            "AND r.voiture.proprietaire.id = :ownerId " +
		            "ORDER BY r.dateDebut DESC")
		     List<Réservation> findReservationsByClientAndOwner(@Param("clientId") Long clientId,
		                                                        @Param("ownerId") Long ownerId);

		     // Liste de toutes les réservations pour un propriétaire
		     @Query("SELECT r FROM Réservation r " +
		            "WHERE r.voiture.proprietaire.id = :ownerId " +
		            "ORDER BY r.dateDebut DESC")
		     List<Réservation> findReservationsByOwner(@Param("ownerId") Long ownerId);
		 
		    		
		 

}

