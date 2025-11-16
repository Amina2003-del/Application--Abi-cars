package location_voiture.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import location_voiture.persistence.dto.PanneDTO;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Panne;
import location_voiture.persistence.model.StatutTechnique;
import location_voiture.repository.CarRepository;
import location_voiture.repository.PanneRepository;

@Service
public class PanneService {

    @Autowired
    private PanneRepository panneRepository;

    @Autowired
    private CarRepository carRepository;

    public Panne createPanne(Panne panne, Long carId) {
        Optional<Car> carOptional = carRepository.findById(carId);
        
        if (carOptional.isEmpty()) {
            throw new IllegalArgumentException("Voiture non trouvée pour l'ID: " + carId);
        }

        Car voiture = carOptional.get();

        // Mettre à jour le statut de la voiture
        voiture.setStatutTechnique(StatutTechnique.EN_PANNE);
        carRepository.save(voiture);

        // Lier la panne à la voiture
        panne.setCar(voiture);

        return panneRepository.save(panne);
    }

    public List<PanneDTO> getAllPannes() {
        List<Panne> pannes = panneRepository.findAll();

        return pannes.stream().map(p -> {
            PanneDTO dto = new PanneDTO();
            dto.setCarId(p.getId());
            dto.setCarId(p.getCar().getId());
            dto.setCarFullName(p.getCar().getMarque() + " " + p.getCar().getModele());
            dto.setDateDebut(p.getDateDebut());
            dto.setDateFin(p.getDateFin());
            dto.setDescription(p.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }
}
