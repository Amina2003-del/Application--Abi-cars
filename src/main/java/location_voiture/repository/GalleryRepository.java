package location_voiture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.model.Gallery;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

	List<Gallery> findByVoitureId(Long voitureId);

    List<Gallery> findByVoiture_Id(Long voitureId);


	

	

}
