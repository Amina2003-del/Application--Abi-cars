package location_voiture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Propritaire;
import ma.abisoft.persistence.model.User;

public interface ProprietaireRepository extends JpaRepository<Propritaire, Long> {

	

	public interface LocataireRepository extends JpaRepository<Locataire, Long> {
	    Locataire findByEmail(String email);
	}

	 
	  
	  Propritaire findByUserEmail(String email);

	    // Requête pour récupérer les propritaires avec description et rôle OWNER
	    @Query("SELECT p FROM Propritaire p JOIN p.user.roles r " +
	           "WHERE r.name = :roleName AND p.descriptionAgence IS NOT NULL AND p.descriptionAgence <> ''")
	    List<Propritaire> findPropritaireWithDescription(@Param("roleName") String roleName);

	    Propritaire findByUser(User user);


		@Query("SELECT p FROM Propritaire p WHERE p.user.id = :userId")
		Propritaire findByUserId(@Param("userId") Long userId);

		@Query("SELECT DISTINCT p FROM Propritaire p JOIN p.voitures v " +
			       "WHERE LOWER(TRIM(v.ville)) LIKE LOWER(CONCAT('%', TRIM(:ville), '%'))")
			List<Propritaire> findProprietairesParVille(@Param("ville") String ville);

	
	
	    


}
