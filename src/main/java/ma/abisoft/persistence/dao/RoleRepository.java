package ma.abisoft.persistence.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import location_voiture.persistence.model.RoleUtilisateur;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    @Override
    void delete(Role role);

	
	
}
