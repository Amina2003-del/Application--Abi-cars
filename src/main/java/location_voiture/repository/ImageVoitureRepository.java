package location_voiture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import location_voiture.persistence.model.Gallery;


public interface ImageVoitureRepository extends JpaRepository<Gallery, Long> {

	List<Gallery> findByVoitureId(Long voitureId);
    void deleteByVoitureId(Long voitureId);


}