package location_voiture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import location_voiture.persistence.model.Panne;

public interface PanneRepository extends JpaRepository<Panne, Long> {

	List<Panne> findByCarId(Long carId);}
