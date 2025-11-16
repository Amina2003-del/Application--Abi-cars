package location_voiture.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.model.Alert;
import location_voiture.persistence.model.TypeAlert;
import ma.abisoft.persistence.model.User;


@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
	Page<Alert> findByUtilisateurOrderByDateEnvoiDesc(User user, Pageable pageable);

	    Page<Alert> findByUtilisateur(User utilisateur, Pageable pageable);
	 // ✅ Bon type de Pageable :
	    Page<Alert> findByUtilisateurAndType(User user, TypeAlert type, Pageable pageable);


	    List<Alert> findByUtilisateurAndEnvoyeAvecSuccesFalse(User user);

	    Optional<Alert> findByIdAndUtilisateur(Long id, User user);

	    // Soit Page avec Pageable
	    int countByUtilisateurAndEnvoyeAvecSuccesFalse(User utilisateur);


	    // Ou liste fixe sans Pageable
	    // List<Alert> findTop10ByUtilisateurOrderByDateEnvoiDesc(User user);


    List<Alert> findByOrderByDateEnvoiDesc(Pageable pageable);

    List<Alert> findByEnvoyeAvecSucces(boolean envoyeAvecSucces);

    @Query("SELECT a FROM Alert a ORDER BY a.dateEnvoi DESC")
    List<Alert> findRecentAlerts(Pageable pageable);

    Page<Alert> findByUtilisateurId(Long userId, Pageable pageable);


    Page<Alert> findByUtilisateurIdAndType(Long userId, TypeAlert type, Pageable pageable);

    long countByUtilisateurIdAndEnvoyeAvecSuccesFalse(Long userId);

	List<Alert> findByUtilisateurId(Long userId);
	
	 List<Alert> findByUtilisateurOrderByDateEnvoiDesc(User utilisateur);

	    List<Alert> findByUtilisateurAndEnvoyeAvecSuccesTrueOrderByDateEnvoiDesc(User utilisateur);

	    List<Alert> findByUtilisateurAndEnvoyeAvecSuccesFalseOrderByDateEnvoiDesc(User utilisateur);



    Page<Alert> findByUtilisateurAndEnvoyeAvecSucces(User user, boolean envoyeAvecSucces, Pageable pageable);

    List<Alert> findTop5ByUtilisateurOrderByDateEnvoiDesc(User user);

    long countByUtilisateurAndEnvoyeAvecSucces(User user, boolean envoyeAvecSucces);

    Page<Alert> findByUtilisateurAndEnvoyeAvecSuccesFalse(User utilisateur, Pageable pageable);

	Page<Alert> findByType(String string, PageRequest of);


	List<Alert> findTop5ByOrderByDateEnvoiDesc();

	 List<Alert> findBySujet(String sujet);
	    List<Alert> findBySujetAndMessageStartingWith(String sujet, String prefix);
	

}
