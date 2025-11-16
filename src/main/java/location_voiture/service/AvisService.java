package location_voiture.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import location_voiture.persistence.model.Avis;
import ma.abisoft.persistence.model.User;

public interface AvisService {
    List<Avis> getAllAvis(); // Méthode déclarée mais sans corps dans l'interface

    Optional<Avis> findById(Long id);
    Avis save(Avis avis);
    void deleteById(Long id);

	List<Avis> findAll();

	long getTotalReviews();

	List<Avis> getAvisByUtilisateur(User utilisateur);

	List<Avis> findAvisByClient(Long id);

	void updateReviewStatus(Long reviewId, String string);

	Page<Avis> getFilteredReviews(String type, Integer ratingValue, PageRequest of);

	void deleteReview(Long reviewId);

	List<Avis> getFilteredAvis(String type, Integer note);

	boolean supprimerAvis(Long id);

	Avis publierAvis(Avis avis);

	boolean publierAvisById(Long id);

	Avis findByReservationId(Long id);


	
	
	
	
}
