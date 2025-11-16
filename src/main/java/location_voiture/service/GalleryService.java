package location_voiture.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Gallery;
import location_voiture.repository.GalleryRepository;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    public Gallery saveGalleryImage(Gallery gallery) {
        return galleryRepository.save(gallery);
    }

    public List<Gallery> findAll() {
        return galleryRepository.findAll();
    }

    public List<Gallery> findByVoitureId(Long voitureId) {
        return galleryRepository.findByVoitureId(voitureId);
    }

	

	  public List<Gallery> getImagesByCarId(Long carId) {
	        return galleryRepository.findByVoiture_Id(carId);
	    }
}