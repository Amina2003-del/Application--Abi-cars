package location_voiture.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import location_voiture.persistence.dto.DisponibiliteDTO;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Disponibilite;
import location_voiture.repository.CarRepository;
import location_voiture.repository.DisponibiliteRepository;

@Service
public class DisponibiliteService {
	@Autowired
    private DisponibiliteRepository disponibiliteRepository;

    @Autowired
    private CarRepository carRepository; // injecte le repository des voitures

    public Disponibilite saveDisponibilite(DisponibiliteDTO dto) {
        Disponibilite dispo = new Disponibilite();

        // Récupérer la voiture depuis la BDD
        Optional<Car> carOpt = carRepository.findById(dto.getVoitureId());
        if (carOpt.isEmpty()) {
            throw new RuntimeException("Voiture introuvable avec l'id : " + dto.getVoitureId());
        }

        dispo.setCar(carOpt.get());
        dispo.setDateDebut(LocalDate.parse(dto.getDateDebut()));
        dispo.setDateFin(LocalDate.parse(dto.getDateFin()));
        dispo.setStatut(dto.getStatut());

        return disponibiliteRepository.save(dispo);
    }}