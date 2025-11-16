package location_voiture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import location_voiture.persistence.model.Localisation;

public interface VilleRepository extends JpaRepository<Localisation, Long> {
    List<Localisation> findByNomStartingWithIgnoreCase(String nom);

    List<Localisation> findByNom(String nom); }// Remplacez par une liste}
