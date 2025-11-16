package location_voiture.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import location_voiture.persistence.model.Avis;
import ma.abisoft.persistence.model.User;

public interface AvisRepository extends JpaRepository<Avis, Long> {
	
	
    @Query("SELECT AVG(a.note) FROM Avis a")
    Double moyenneAvis();
    long countByVoiture_ValideFalse();

    List<Avis> findTop5ByOrderByDateDesc();
    long count();

    List<Avis> findAll();
    // Recherche simple par type (voiture, propriétaire, client) et note
    List<Avis> findByVoiture_TypeContainingIgnoreCaseAndNote(String type, Integer note);
    
    // Méthode custom pour filtrer par type et note avec possibilité de null (tous)
    List<Avis> findByVoiture_TypeIgnoreCaseAndNote(String type, Integer note);


	@Query("SELECT AVG(a.note) FROM Avis a WHERE MONTH(a.date) = :month")
	Double averageRatingByMonth(@Param("month") int month);
	List<Avis> findByAuteur(User user);
    List<Avis> findByUtilisateurId(Long utilisateurId);


    @Query("SELECT COUNT(a) FROM Avis a")
    long countAllReviews();
    List<Avis> findByUtilisateur_Id(Long utilisateurId);
	List<Avis> findByUtilisateur(User user);

	
	 @Query("SELECT a FROM Avis a WHERE a.reservation.utilisateur.id = :id")
	    List<Avis> findAvisByClientId(@Param("id") Long id);
}

