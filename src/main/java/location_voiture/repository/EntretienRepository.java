package location_voiture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Entretien;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.StatutEntretien;

public interface EntretienRepository extends JpaRepository<Entretien, Long> {
    List<Entretien> findByCar(Car car);

    List<Entretien> findByStatutAndTypeAndCarId(StatutEntretien statut, String type, Long carId);
    List<Entretien> findByStatutAndType(StatutEntretien statut, String type);
    List<Entretien> findByStatutAndCarId(StatutEntretien statut, Long carId);
    List<Entretien> findByTypeAndCarId(String type, Long carId);
    List<Entretien> findByStatut(StatutEntretien statut);
    List<Entretien> findByType(String type);
    List<Entretien> findByCarId(Long carId);


    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId")
    List<Entretien> findByVoitureProprietaireId(@Param("proprietaireId") Long proprietaireId);
    
    // Ou mieux, renommez la méthode pour plus de clarté :
    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId")
    List<Entretien> findByCarProprietaireId(@Param("proprietaireId") Long proprietaireId);
    
    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId AND e.statut = :statut AND e.type = :type AND e.car.id = :carId")
    List<Entretien> findByCarProprietaireIdAndStatutAndTypeAndCarId(
        @Param("proprietaireId") Long proprietaireId, 
        @Param("statut") StatutEntretien statut, 
        @Param("type") String type, 
        @Param("carId") Long carId);
 
    @Query("SELECT e FROM Entretien e JOIN FETCH e.car c WHERE c.proprietaire.id = :proprietaireId AND e.supprimer = 0")
    List<Entretien> findActiveByProprietaireWithCar(@Param("proprietaireId") Long proprietaireId);

    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId AND e.statut = :statut AND e.type = :type")
    List<Entretien> findByCarProprietaireIdAndStatutAndType(
        @Param("proprietaireId") Long proprietaireId, 
        @Param("statut") StatutEntretien statut, 
        @Param("type") String type);
    
    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId AND e.statut = :statut AND e.car.id = :carId")
    List<Entretien> findByCarProprietaireIdAndStatutAndCarId(
        @Param("proprietaireId") Long proprietaireId, 
        @Param("statut") StatutEntretien statut, 
        @Param("carId") Long carId);
    
    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId AND e.type = :type AND e.car.id = :carId")
    List<Entretien> findByCarProprietaireIdAndTypeAndCarId(
        @Param("proprietaireId") Long proprietaireId, 
        @Param("type") String type, 
        @Param("carId") Long carId);
    
    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId AND e.statut = :statut")
    List<Entretien> findByCarProprietaireIdAndStatut(
        @Param("proprietaireId") Long proprietaireId, 
        @Param("statut") StatutEntretien statut);
    
    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId AND e.type = :type")
    List<Entretien> findByCarProprietaireIdAndType(
        @Param("proprietaireId") Long proprietaireId, 
        @Param("type") String type);
    
    @Query("SELECT e FROM Entretien e WHERE e.car.proprietaire.id = :proprietaireId AND e.car.id = :carId")
    List<Entretien> findByCarProprietaireIdAndCarId(
        @Param("proprietaireId") Long proprietaireId, 
        @Param("carId") Long carId);

    @Query("SELECT e FROM Entretien e WHERE " +
    	       "e.car.proprietaire.id = :proprietaireId AND " +
    	       "(:statut = '' OR :statut = 'Filtrer par statut' OR e.statut = :statut) AND " +
    	       "(:type = '' OR :type = 'Filtrer par type' OR e.type = :type) AND " +
    	       "(:voiture = '' OR :voiture = 'Filtrer par voiture' OR e.car.id = :voiture)")
    	List<Entretien> findWithDynamicFilters(
    	    @Param("proprietaireId") Long proprietaireId,
    	    @Param("statut") String statut,
    	    @Param("type") String type,
    	    @Param("voiture") String voiture);    
    // Méthode de base par propriétaire

	    
	    // 🔥 NOUVELLE MÉTHODE AVEC FILTRE SOFT DELETE
    @Query("SELECT e FROM Entretien e " +
    	       "JOIN FETCH e.car c " +
    	       "WHERE c.proprietaire.id = :proprietaireId " +
    	       "AND e.supprimer = 0 " +
    	       "AND (:statut = '' OR :statut = 'Filtrer par statut' OR e.statut = :statut) " +
    	       "AND (:type = '' OR :type = 'Filtrer par type' OR e.type = :type) " +
    	       "AND (:voiture = '' OR :voiture = 'Filtrer par voiture' " +
    	       "      OR LOWER(c.marque) LIKE LOWER(CONCAT('%', :voiture, '%')) " +
    	       "      OR LOWER(c.modele) LIKE LOWER(CONCAT('%', :voiture, '%')))")
    	List<Entretien> findActiveWithDynamicFilters(
    	    @Param("proprietaireId") Long proprietaireId,
    	    @Param("statut") String statut,
    	    @Param("type") String type,
    	    @Param("voiture") String voiture);

	

}

