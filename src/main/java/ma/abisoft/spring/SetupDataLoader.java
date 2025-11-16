package ma.abisoft.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Localisation;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.StatutApprobationVoiture;
import location_voiture.persistence.model.StatutDemande;
import location_voiture.persistence.model.StatutTechnique;
import location_voiture.repository.CarRepository;
import location_voiture.repository.LocataireRepository;
import location_voiture.repository.ProprietaireRepository;
import location_voiture.repository.VilleRepository;
import ma.abisoft.persistence.dao.PrivilegeRepository;
import ma.abisoft.persistence.dao.RoleRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.Privilege;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.UserService;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final ProprietaireRepository proprietaireRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private LocataireRepository locataireRepository; 
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private CarRepository carRepository; 
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VilleRepository localisationRepository;

    SetupDataLoader(ProprietaireRepository proprietaireRepository) {
        this.proprietaireRepository = proprietaireRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> visiteurPrivileges = new ArrayList<>(Arrays.asList(readPrivilege));
        final List<Privilege> proprietairePrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_CLIENT", userPrivileges);
        createRoleIfNotFound("ROLE_OWNER", proprietairePrivileges);

        // == create initial user
        createUserIfNotFound("chakirfst@gmail.com", "Loqman", "Chakir", "Optimisation1,", "0687654324",new ArrayList<>(Arrays.asList(adminRole)));

        // == create initial proprietaires
        createProprietairesIfNotExists();
        createLocalisationsIfNotExists();
        createLocataireIfNotExists();
        // Récupérer le propriétaire depuis la base
        User user = userRepository.findByEmail("hilalli.hassania@gmail.com");
        if (user != null) {
            Propritaire proprietaire = proprietaireRepository.findByUser(user);
            if (proprietaire != null) {
                createCarForOwner(proprietaire);
                createSecondCarForOwner(proprietaire);
            } else {
                logger.warn("Propriétaire non trouvé pour créer les voitures !");
            }
        } else {
            logger.warn("Utilisateur pour le propriétaire non trouvé !");
        }

        alreadySetup = true;
    }


	@Transactional
    public void createProprietairesIfNotExists() {
        // Vérifier si l'utilisateur existe déjà
       
        // Même logique pour le deuxième propriétaire
        User existingUser2 = userRepository.findByEmail("hilalli.hassania@gmail.com");
        if (existingUser2 == null) {
            User user2 = new User();
            user2.setFirstName("Layla");
            user2.setLastName("Mansoure");
            user2.setEmail("hilalli.hassania@gmail.com");
            user2.setTel("0712345678");
            user2.setPassword(passwordEncoder.encode("Motdepasse3!"));
            user2.setTel("0712345678");
            user2.setEnabled(true);
            Role ownerRole = roleRepository.findByName("ROLE_OWNER");
            if (ownerRole == null) {
                throw new RuntimeException("Rôle ROLE_OWNER manquant");
            }
            user2.setRoles(new ArrayList<>(Arrays.asList(ownerRole)));
            userRepository.save(user2);

            Propritaire proprietaire2 = new Propritaire();
            proprietaire2.setUser(user2);
            proprietaire2.setRaisonsociale("Majesté Auto Location");
            proprietaire2.setIce("0098765432109");
            proprietaire2.setLogovoiturePath("agence2.jpg");
            proprietaire2.setDescriptionAgence("Agence dédiée à la location économique et familiale"
);
            proprietaireRepository.save(proprietaire2);

            logger.info("Propriétaire créé et lié à l'utilisateur : {}", user2.getEmail());
        } else {
            logger.info("Utilisateur déjà existant : {}", existingUser2.getEmail());
        }
    

        logger.info("Fin de la méthode createProprietairesIfNotExists");
        
    }
	@Transactional
	public void createLocataireIfNotExists() {
	    String email = "ammina.ramii@gmail.com";
	    
	    // Vérifier si l'utilisateur existe déjà
	    User existingUser = userRepository.findByEmail(email);
	    
	    if (existingUser == null) {
	        // Créer l'utilisateur
	        User user = new User();
	        user.setFirstName("Amina");
	        user.setLastName("Rami");
	        user.setEmail(email);
	        user.setTel("0712345678");
	        user.setPassword(passwordEncoder.encode("Nrpf4528@"));
	        user.setEnabled(true);

	        // Assigner le rôle CLIENT
	        Role clientRole = roleRepository.findByName("ROLE_CLIENT");
	        if (clientRole == null) {
	            throw new RuntimeException("Rôle ROLE_CLIENT manquant");
	        }
	        user.setRoles(new ArrayList<>(List.of(clientRole)));

	        userRepository.save(user);

	        // Créer le locataire lié à l'utilisateur
	        Locataire locataire = new Locataire();
	        locataire.setUser(user);
	        locataire.setAdresse("N 31 Rue 6 bloc A 30000");
	        locataire.setNumeroPermis("A896789"); // Exemple
	        locataireRepository.save(locataire);

	        logger.info("Locataire créé et lié à l'utilisateur : {}", user.getEmail());
	    } else {
	        logger.info("Utilisateur déjà existant : {}", existingUser.getEmail());
	    }

	    logger.info("Fin de la méthode createLocataireIfNotExists");
	}
	@Transactional
	public void createCarForOwner(Propritaire proprietaire) {
	    // Vérifier si la voiture existe déjà (par immatriculation)
	    String immatriculation = "AD-3455";
	    Car existingCar = carRepository.findByImmatriculation(immatriculation);
	    
	    if (existingCar == null) {
	        Car car = new Car();
	        
	        // Informations générales
	        car.setAnnee(2023);
	        car.setBoite("Automatique");
	        car.setCarburant("Électrique");
	        car.setCategorie("Premium");
	        car.setDescription("Berline élégante, système audio premium");
	        car.setImagePrincipaleURL("1761871325356_mercedes-suv.jpeg");
	        car.setImmatriculation(immatriculation);
	        car.setKilometrage(6000);
	        car.setLatitude(34.0346534);
	        car.setLongitude(-5.0161926);
	        car.setMarque("BMW");
	        car.setModele("Yaris");
	        car.setPlaces(4);
	        car.setPrixJournalier(80.0);
	      // Enum ou type adapté
	        car.setType("Berline");
	        car.setValide(false); // 0 ou false selon votre logique
	        car.setVille("Fès");
	        car.setVisible(true);
	        car.setCommentaire(null);
	        car.setSupprimer(0); // 0 = actif
	        car.setProprietaire(proprietaire);

	        carRepository.save(car);
	        logger.info("Voiture créée pour le propriétaire {} : {} {}", proprietaire.getUser().getEmail(), car.getMarque(), car.getModele());
	    } else {
	        logger.info("La voiture existe déjà : {}", immatriculation);
	    }
	}
	@Transactional
	public void createSecondCarForOwner(Propritaire proprietaire) {
	    // Vérifier si la voiture existe déjà (par immatriculation)
	    String immatriculation = "BD-7890";
	    Car existingCar = carRepository.findByImmatriculation(immatriculation);

	    if (existingCar == null) {
	        Car car = new Car();

	        // Informations générales
	        car.setAnnee(2024);
	        car.setBoite("Manuelle");
	        car.setCarburant("Essence");
	        car.setCategorie("Standard");
	        car.setDescription("Compacte et économique, idéale pour la ville");
	        car.setImagePrincipaleURL("1762183461876_mercedes-benz orange.jpeg");
	        car.setImmatriculation(immatriculation);
	        car.setKilometrage(12000);
	        car.setLatitude(33.997654);
	        car.setLongitude(-5.010234);
	        car.setMarque("Toyota");
	        car.setModele("Corolla");
	        car.setPlaces(5);
	        car.setPrixJournalier(50.0);
	        car.setType("Compacte");
	        car.setValide(true); // 1 ou true selon votre logique
	        car.setVille("Fès");
	        car.setVisible(true);
	        car.setCommentaire("Voiture prête à la location");
	        car.setSupprimer(0); // 0 = actif

	        // Associer le propriétaire
	        car.setProprietaire(proprietaire);

	        carRepository.save(car);
	        logger.info("Deuxième voiture créée pour le propriétaire {} : {} {}", 
	                    proprietaire.getUser().getEmail(), car.getMarque(), car.getModele());
	    } else {
	        logger.info("La voiture existe déjà : {}", immatriculation);
	    }
	}

    @Transactional
    public void createLocalisationsIfNotExists() {
        if (localisationRepository.count() == 0) {
            List<Localisation> localisations = Arrays.asList(
                new Localisation("Casablanca", "Casablanca-Settat", "Maroc"),
                new Localisation("Fès", "Fès-Meknès", "Maroc"),
                new Localisation("Tanger", "Tanger-Tétouan-Al Hoceïma", "Maroc"),
                new Localisation("Agadir", "Souss-Massa", "Maroc"),
                new Localisation("Marrakech", "Marrakech-Safi", "Maroc"),
                new Localisation("Oujda", "Oriental", "Maroc"),
                new Localisation("Rabat", "Rabat-Salé-Kénitra", "Maroc")
            );

            localisationRepository.saveAll(localisations);
            logger.info("✅ Localisations marocaines insérées avec succès !");
        } else {
            logger.info("ℹ️ Les localisations sont déjà présentes en base.");
        }}
    @Transactional
    private final Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
        }
        role.setPrivileges(new ArrayList<>(privileges));
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    private final User createUserIfNotFound(
            final String email, 
            final String firstName, 
            final String lastName, 
            final String password, 
            final String tel,                 // <-- nouveau paramètre
            final Collection<Role> roles) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
            if(tel != null && !tel.isEmpty()) {
                user.setTel(tel);          // <-- assignation du téléphone
            }
        }

        user.setRoles(new ArrayList<>(roles));
        user = userRepository.save(user);
        return user;
    }
    

}
