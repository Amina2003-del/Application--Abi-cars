/*package location_voiture.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import location_voiture.persistence.model.Administrateur;
import location_voiture.repository.AdministrateurRepository;
import ma.abisoft.persistence.dao.RoleRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final AdministrateurRepository administrateurRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(UserRepository userRepository,
                        AdministrateurRepository administrateurRepository,
                        RoleRepository roleRepository,
                        BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.administrateurRepository = administrateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void creerAdmin(String firstName,
                           String lastName,
                           String email,
                           String rawPassword,
                           int niveauAcces) {

    	 Role adminRole = roleRepository.findByName("ROLE_ADMIN");
         if (adminRole == null) {
             throw new IllegalStateException("Le rôle ADMIN n'existe pas en base");
         }

        Administrateur admin = new Administrateur();
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setEnabled(true);
        admin.setNiveauAcces(niveauAcces);
        admin.setRoles(Set.of(adminRole));

        administrateurRepository.save(admin);
        administrateurRepository.flush(); // force l'exécution SQL
        System.out.println("Administrateur ajouté avec ID = " + admin.getId());
    }

}*/
