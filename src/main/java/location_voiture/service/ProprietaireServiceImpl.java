package location_voiture.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import location_voiture.persistence.dto.UserDTO;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import location_voiture.repository.ProprietaireRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;

@Service
public class ProprietaireServiceImpl implements ProprietaireService {

    @Autowired
    private ProprietaireRepository proprietaireRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Propritaire getById(Long id) {
        return proprietaireRepository.findById(id).orElse(null); // 👈 retourne le propriétaire ou null
    }

	@Override
	public List<UserDTO> getUsersByRole(RoleUtilisateur proprietaire) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserDTO> getClientsDTO() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<UserDTO> getOwnersDTO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
    }

	@Transactional
	public User suspendUser(Long userId) {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
	    
	    // Inverse la valeur boolean enabled
	    user.setEnabled(!user.isEnabled());
	    
	    return userRepository.save(user);
	}


   

    @Override
    public User getUserById(Long userId) {
        return findById(userId); // Réutilise findById pour la récupération
    }

	@Override
	public List<Propritaire> getAllProprietaires() {
		// TODO Auto-generated method stub
		return null;
	}
}
