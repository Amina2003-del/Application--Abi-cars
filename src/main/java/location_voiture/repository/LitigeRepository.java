package location_voiture.repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Litige;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutLitige;
import ma.abisoft.persistence.model.User;

public interface LitigeRepository extends JpaRepository<Litige, Long> {
    long count();
    long countByStatut(StatutLitige statut);
	List<Litige> findByUtilisateurId(Long id);
	List<Litige> findByReservationIn(List<Reservation> reservations);
	List<Litige> findByReservationUtilisateur(User utilisateur);
	@Query("SELECT FUNCTION('MONTH', l.dateCreation) AS mois, COUNT(l) " +
	           "FROM Litige l " +
	           "WHERE FUNCTION('YEAR', l.dateCreation) = :annee " +
	           "GROUP BY FUNCTION('MONTH', l.dateCreation)")
	    List<Object[]> countLitigesParMois(@Param("annee") int annee);
	    
	    @Query("SELECT DISTINCT l.statut FROM Litige l")
	    List<String> findDistinctStatuts();
	    @Query("SELECT l FROM Litige l WHERE l.statut = :statut AND l.dateCreation BETWEEN :start AND :end")
	    List<Litige> findLitigesByStatutBetween(
	        @Param("statut") StatutLitige statut,
	        @Param("start") LocalDateTime start,
	        @Param("end") LocalDateTime end
	    );

}
