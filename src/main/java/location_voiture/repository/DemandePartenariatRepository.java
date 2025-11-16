package location_voiture.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import location_voiture.persistence.model.DemandePartenariat;
import location_voiture.persistence.model.StatutDemande;
@Repository

public interface DemandePartenariatRepository extends JpaRepository<DemandePartenariat, Long> {
	long countByStatut(StatutDemande statut);  // Accepts StatutDemande enum
    List<DemandePartenariat> findByStatut(StatutDemande statut);


}
