package location_voiture.service;

import java.util.List;

import location_voiture.persistence.dto.UserDTO;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import ma.abisoft.persistence.model.User;

public interface ProprietaireService {
    Propritaire getById(Long id); // Ajoute cette méthode

	List<UserDTO> getUsersByRole(RoleUtilisateur proprietaire);

	List<UserDTO> getClientsDTO();

	List<UserDTO> getOwnersDTO();

	User findById(Long id);

	User suspendUser(Long userId);

	User getUserById(Long userId);

	List<Propritaire> getAllProprietaires();
	
}
