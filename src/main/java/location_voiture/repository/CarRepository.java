package location_voiture.repository;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.StatutApprobationVoiture;
import location_voiture.persistence.model.StatutDemande;
import location_voiture.persistence.model.TypeVoiture;
import ma.abisoft.persistence.model.User;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> , JpaSpecificationExecutor<Car> {
   // List<Car> findTop5ByOrderByDisponibleDesc();
    

    List<Car> findByCategorie(String categorie);
    // Trouver les voitures disponibles (avec String "disponible")
    long countByStatut(StatutDemande statut);

    // Trouver les voitures disponibles par catégorie
   // List<Car> findByCategorieAndDisponible(String categorie, String disponible);
    
    // Autres méthodes personnalisées si nécessaire
    List<Car> findByMarqueContainingIgnoreCase(String marque);
    long countByStatutApprobation(StatutApprobationVoiture statutApprobation);

    List<Car> findByPrixJournalierLessThanEqual(Double prix);
    
    List<Car> findByModele(String modele);
    
    List<Car> findByStatutApprobation(StatutApprobationVoiture statut);
    
    List<Car> findByProprietaireId(Long proprietaireId);
   //List<Car> findByDisponible(String disponible);
  
    // Find available cars by city (case-insensitive partial match)
    /*@Query("SELECT c FROM Car c WHERE c.disponible = :disponible AND LOWER(c.ville) LIKE LOWER(CONCAT('%', :ville, '%'))")
    List<Car> findAvailableCarsByVille(@Param("disponible") String disponible, @Param("ville") String ville);*/
    // Correction ici : Utilisez long pour le comptage des voitures disponibles
  //  long countByDisponible(String disponible);
    Car findByImmatriculation(String immatriculation);// Utilisation de String pour compter les voitures disponibles

    List<Car> findByMarque(String marque);
    
    List<Car> findByMarqueContainingOrModeleContainingOrImmatriculationContaining(
            String marque, String modele, String immatriculation);
    
    List<Car> findByMarqueContainingOrModeleContainingOrImmatriculationContainingAndType(
            String marque, String modele, String immatriculation, String type);
    
    List<Car> findByType(String type);

    // Utilisez long pour compter les voitures valides ou non
    long countByValideFalse();
    
    @Query("SELECT c FROM Car c LEFT JOIN FETCH c.proprietaire")
    List<Car> findAllWithProprietaires();
    
   // List<Car> findByDisponibleAndCategorie(String disponible, String categorie);

 // ...
  //  List<Car> findByDisponibleIgnoreCase(String etatDisponible);
   // List<Car> findByDisponibleIgnoreCaseAndCategorieIgnoreCase(String etatDisponible, String categorie);

    List<Car> findByVille(String ville);

    @Query("SELECT c FROM Car c WHERE (:pickupLocation IS NULL OR c.ville = :pickupLocation) " +
    	       "AND (:carType IS NULL OR c.type = :carType) " +
    	       "AND NOT EXISTS (SELECT r FROM Réservation r WHERE r.voiture = c AND (r.dateDebut <= :endDate AND r.dateFin >= :startDate))")
    	List<Car> findAvailableCars(@Param("pickupLocation") String pickupLocation,
    	                            @Param("startDate") LocalDate startDate,
    	                            @Param("endDate") LocalDate endDate,
    	                            @Param("carType") String carType);



    @Query("SELECT c FROM Car c LEFT JOIN FETCH c.disponibilites")
    List<Car> findAllWithDisponibilites();

    @Query("SELECT c FROM Car c WHERE c.type = :typeVoiture " +
    	       "AND c.id NOT IN (" +
    	       "SELECT r.voiture.id FROM Réservation r " +
    	       "WHERE r.statut = 'CONFIRMEE' " +
    	       "AND (r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut))")
    	List<Car> findAvailableCars(@Param("typeVoiture") String typeVoiture,
    	                            @Param("dateDebut") LocalDate dateDebut,
    	                            @Param("dateFin") LocalDate dateFin);




    
    
    @Query("SELECT DISTINCT c.marque FROM Car c")
    List<String> findDistinctMarques();

    @Query("SELECT DISTINCT c.modele FROM Car c")
    List<String> findDistinctModeles();
    
  

    
    @Query("SELECT DISTINCT c.marque FROM Car c")
    List<String> fetchDistinctMarques();

    @Query("SELECT DISTINCT c.modele FROM Car c")
    List<String> fetchDistinctModeles();

    @Query("SELECT DISTINCT c.annee FROM Car c")
    List<String> fetchDistinctAnnees();

    @Query("SELECT DISTINCT c.ville FROM Car c")
    List<String> fetchDistinctVilles();


    @Query("SELECT c FROM Car c WHERE " +
    	       "(:marque IS NULL OR c.marque = :marque) AND " +
    	       "(:modele IS NULL OR c.modele = :modele) AND " +
    	       "(:annee IS NULL OR c.annee = :annee) AND " +
    	       "(:ville IS NULL OR c.ville = :ville)")
    	List<Car> rechercherParCritere(
    	    @Param("marque") String marque,
    	    @Param("modele") String modele,
    	    @Param("annee") Integer annee,
    	    @Param("ville") String ville
    	);


    List<Car> findByProprietaire(User proprietaire);


    @Query("SELECT c, COUNT(r), COALESCE(SUM(r.prixTotal), 0) " +
    	       "FROM Car c JOIN c.reservations r " +
    	       "WHERE r.dateDebut BETWEEN :start AND :end " +
    	       "GROUP BY c")
    	List<Object[]> findCarPerformanceBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);



    	 // COMPTE le nombre de voitures d'un propriétaire via user_id
       
        
        // Alternative si la première ne fonctionne pas
    	 
    	    // Recherche flexible avec LIKE
    	 
    	    
    	    
    	    // ✅ Tous les propriétaires (même sans voitures)
    	    @Query("SELECT p FROM Propritaire p")
    	    List<Propritaire> findAllProprietairesIncludingEmpty();
    	    
    	    // ✅ Alternative flexible pour la recherche par ville
    	    @Query("SELECT DISTINCT p FROM Propritaire p " +
    	           "LEFT JOIN p.voitures v " +
    	           "WHERE (:ville IS NULL OR v.ville = :ville)")
    	    List<Propritaire> findProprietairesParVilleFlexible(@Param("ville") String ville);
    	    
    	    // ✅ COMPTE le nombre de voitures d'un propriétaire via user_id
    	    @Query("SELECT COUNT(v) FROM Car v JOIN v.proprietaire p WHERE p.user.id = :userId")
    	    Long countVoituresByProprietaireUserId(@Param("userId") Long userId);
    	    
    	    // ✅ Méthode pour debug: compter les voitures par propriétaire
    	    @Query("SELECT p.id, COUNT(v) FROM Propritaire p LEFT JOIN p.voitures v GROUP BY p.id")
    	    List<Object[]> countVoituresParProprietaire();
    	    
    	    // ✅ Récupérer les villes distinctes depuis les voitures
    	    @Query("SELECT DISTINCT v.ville FROM Car v WHERE v.ville IS NOT NULL")
    	    List<String> findDistinctVilles();

        List<Car> findBySupprimer(Integer supprimer); // 0 ou 1

        @Query("SELECT COUNT(c) FROM Car c WHERE c.proprietaire.user.id = :userId")
        Long countByUserId(@Param("userId") Long userId);
        
        // REQUÊTE NATIVE CORRIGÉE - utilisez "voitures" au lieu de "car"
        @Query(value = "SELECT COUNT(*) FROM voitures v " +
                      "JOIN proprietaire p ON v.proprietaire_id = p.id " +
                      "WHERE p.user_id = :userId", nativeQuery = true)
        Long countByUserIdNative(@Param("userId") Long userId);
        
        // Méthode de debug
        @Query("SELECT COUNT(c) FROM Car c")
        Long countAll();

    	// List<Car> findByVilleIgnoreCaseAndDisponible(String ville, String disponible);

    	   // @Query("SELECT DISTINCT c.ville FROM Car c WHERE c.disponible = ?1")
    	   // List<String> findDistinctCitiesByDisponibilite(String disponible);

    	    @Query("SELECT DISTINCT c.ville FROM Car c")
    	    List<String> findDistinctCities();
    	  /*@Query("SELECT c FROM Car c " +
    	    	       "WHERE c.id NOT IN ( " +
    	    	       "    SELECT d.car.id FROM Disponibilite d " +
    	    	       "    WHERE d.dateDebut <= :end AND d.dateFin >= :start " +
    	    	       "      AND d.statut = 'INDISPONIBLE' " +
    	    	       ")")
    	    	List<Car> findCarsDisponibles(@Param("start") LocalDate start,
    	    	                              @Param("end") LocalDate end);
*/
		
			
			 @Query("SELECT c FROM Car c WHERE c.proprietaire = :proprietaire AND LOWER(c.ville) = LOWER(:ville)")
			    List<Car> findByProprietaireAndVille(@Param("proprietaire") Propritaire proprietaire, @Param("ville") String ville);
			    
			    @Query("SELECT c FROM Car c WHERE LOWER(c.ville) = LOWER(:ville)")
			    List<Car> findByVilleIgnoreCase(@Param("ville") String ville);
			    
			    @Query("SELECT c FROM Car c WHERE c.proprietaire = :proprietaire")
			    List<Car> findByProprietaire(@Param("proprietaire") Propritaire proprietaire);
				List<Car> findByProprietaireAndVilleContaining(Propritaire proprietaire, String sanitizedVille);
				List<Car> findByVilleContaining(String sanitizedVille);
			    
				@Query("SELECT DISTINCT p FROM Propritaire p JOIN p.voitures v WHERE LOWER(TRIM(v.ville)) LIKE LOWER(CONCAT('%', TRIM(:ville), '%'))")
			    List<Propritaire> findProprietairesParVille(@Param("ville") String ville);
			    
			    // Méthode pour trouver tous les propriétaires
			    @Query("SELECT DISTINCT p FROM Propritaire p JOIN p.voitures v")
			    List<Propritaire> findAllProprietaires();
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
}
