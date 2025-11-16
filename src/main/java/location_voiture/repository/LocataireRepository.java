package location_voiture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Message;

public interface LocataireRepository extends JpaRepository<Locataire, Long> {

    Optional<Locataire> findByNumeroPermis(String numeroPermis);
    
   
    @Query("SELECT l FROM Locataire l WHERE l.user.id = :userId")
    Locataire findByUserId(@Param("userId") Long userId);
    

}
