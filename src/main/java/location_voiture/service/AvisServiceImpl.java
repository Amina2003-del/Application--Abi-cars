package location_voiture.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import location_voiture.persistence.model.Avis;
import location_voiture.repository.AvisRepository;
import ma.abisoft.persistence.model.User;

@Service
public class AvisServiceImpl implements AvisService {

    private final AvisRepository avisRepository;

    // Injection de dépendance via le constructeur
    @Autowired
    public AvisServiceImpl(AvisRepository avisRepository) {
        this.avisRepository = avisRepository;
    }
    public long getTotalReviews() {
        return avisRepository.countAllReviews();
    }
    public List<Avis> getAvisByUtilisateur(User user) {
        // votre logique ici, par exemple :
        return avisRepository.findByUtilisateur(user);
    }
    // Méthode pour récupérer tous les avis
    @Override
    @Transactional(readOnly = true)
    public List<Avis> findAll() {
        return avisRepository.findAll();
    }
    @Override
    public List<Avis> findAvisByClient(Long utilisateurId) {
        return avisRepository.findByUtilisateur_Id(utilisateurId);
    }

    // Publier un avis (création)
    public Avis publierAvis(Avis avis) {
        // Tu peux ajouter ici des validations si besoin
        return avisRepository.save(avis);
    }

    // Supprimer un avis par son ID
    public boolean supprimerAvis(Long id) {
        Optional<Avis> avis = avisRepository.findById(id);
        if (avis.isPresent()) {
            avisRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Récupérer tous les avis (utile pour affichage)
    public List<Avis> getAllAvis() {
        return avisRepository.findAll();
    }

    // Récupérer un avis par son ID
    public Optional<Avis> getAvisById(Long id) {
        return avisRepository.findById(id);
    }
   
    public List<Avis> getFilteredAvis(String type, Integer note) {
        List<Avis> all = avisRepository.findAll();

        return all.stream()
                .filter(a -> (type == null || type.equalsIgnoreCase("Tous") || a.getType().equalsIgnoreCase(type)))
                .filter(a -> (note == null || note == 0 || a.getNote().equals(note)))
                .collect(Collectors.toList());
    }


    
    public void updateReviewStatus(Long reviewId) {
        Avis avis = avisRepository.findById(reviewId).orElseThrow();
        avisRepository.save(avis);
    }

    // Méthode pour récupérer un avis par son ID
    @Override
    @Transactional(readOnly = true)
    public Optional<Avis> findById(Long id) {
        return avisRepository.findById(id);
    }

    // Méthode pour enregistrer ou mettre à jour un avis
    @Override
    @Transactional
    public Avis save(Avis avis) {
        return avisRepository.save(avis);
    }

    // Méthode pour supprimer un avis par son ID
    @Override
    @Transactional
    public void deleteById(Long id) {
        avisRepository.deleteById(id);
    }

 

	@Override
	public void deleteReview(Long reviewId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateReviewStatus(Long reviewId, String string) {
		// TODO Auto-generated method stub
		
	}
	public Page<Avis> getFilteredReviews(String type, PageRequest of) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Page<Avis> getFilteredReviews(String type, Integer ratingValue, PageRequest of) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean publierAvisById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Avis findByReservationId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
