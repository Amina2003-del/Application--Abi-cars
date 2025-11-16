package ma.abisoft.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import location_voiture.persistence.dto.UserDTO;
import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.DemandePartenariat;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutDemande;
import location_voiture.repository.AvisRepository;
import location_voiture.repository.CarRepository;
import location_voiture.repository.DemandePartenariatRepository;
import location_voiture.repository.LocataireRepository;
import location_voiture.repository.ProprietaireRepository;
import location_voiture.web.controller.ClientsController;
import ma.abisoft.persistence.dao.PasswordResetTokenRepository;
import ma.abisoft.persistence.dao.RoleRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.dao.VerificationTokenRepository;
import ma.abisoft.persistence.model.PasswordResetToken;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;
import ma.abisoft.persistence.model.VerificationToken;
import ma.abisoft.web.controller.RegistrationController;
import ma.abisoft.web.dto.UserDto;
import ma.abisoft.web.error.UserAlreadyExistException;

@Service
@Transactional
public class UserService implements IUserService {
    private final Logger logger = LoggerFactory.getLogger(ClientsController.class);


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocataireRepository locataireRepository;
    
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
   
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProprietaireRepository proprietaireRepository;
    @Autowired
    private AvisRepository avisRepository;
    @Autowired
    private SessionRegistry sessionRegistry;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "SpringRegistration";

    // API
   
    @Override
    public User registerNewUserAccount(final UserDto accountDto) {
        final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

        if (emailExists(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email adress: " + accountDto.getEmail());
        }
       

        String roleName = accountDto.getRole(); // Exemple : "ROLE_OWNER" ou "ROLE_CLIENT"
        User user; // Déclaration unique ici

        if ("ROLE_OWNER".equals(roleName)) {
            // 1️⃣ Création du User
            User users = new User();

            users.setFirstName(accountDto.getFirstName());
            users.setLastName(accountDto.getLastName());
            users.setEmail(accountDto.getEmail());
            users.setTel(accountDto.getTel());
            users.setPassword(passwordEncoder.encode(accountDto.getPassword()));
            users.setEnabled(true);

            Role ownerRole = roleRepository.findByName("ROLE_OWNER");
            if (ownerRole == null) {
                throw new RuntimeException("Le rôle ROLE_OWNER est manquant !");
            }
            users.setRoles(Arrays.asList(ownerRole));

            LOGGER.info("Avant save User : " + users);
            user = userRepository.save(users);
            LOGGER.info("Après save User, id = " + user.getId());

            // 2️⃣ Création du Propritaire et association avec le User
            Propritaire proprietaire = new Propritaire();
            proprietaire.setUser(user); // lien vers le User
            proprietaire.setRaisonsociale(accountDto.getRaisonsociale());
            proprietaire.setIce(accountDto.getIce());
            proprietaire.setDescriptionAgence(accountDto.getDescriptionAgence());

            if (accountDto.getLogovoitureFileName() != null && !accountDto.getLogovoitureFileName().isEmpty()) {
                LOGGER.info("Logo Voiture reçu (nom fichier) : " + accountDto.getLogovoitureFileName());
                proprietaire.setLogovoiturePath(accountDto.getLogovoitureFileName());
            } else {
                LOGGER.warn("⚠️ Aucun fichier logo reçu ou fichier vide !");
            }

            LOGGER.info("Avant save Propritaire : " + proprietaire);
            proprietaireRepository.save(proprietaire);
            LOGGER.info("Propriétaire créé et lié au User id = " + user.getId());
        
        } else if ("ROLE_CLIENT".equals(roleName)) {
            System.out.println(">>> RÉCEPTION DONNÉES FORMULAIRE CLIENT");
            System.out.println("FirstName: " + accountDto.getFirstName());
            System.out.println("LastName: " + accountDto.getLastName());
            System.out.println("Email: " + accountDto.getEmail());
            System.out.println("Tel: " + accountDto.getTel());
            System.out.println("Adresse: " + accountDto.getAdresse());
            System.out.println("Permis: " + accountDto.getNumeroPermis());

            // 1️⃣ Création du User
            User users = new User();
            users.setFirstName(accountDto.getFirstName());
            users.setLastName(accountDto.getLastName());
            users.setEmail(accountDto.getEmail());
            users.setTel(accountDto.getTel());
            users.setPassword(passwordEncoder.encode(accountDto.getPassword()));
            users.setRoles(Arrays.asList(roleRepository.findByName("ROLE_CLIENT")));

            user = userRepository.save(users); // Sauvegarde le User
            LOGGER.info("Après save user, id = " + user.getId());

            // 2️⃣ Création du Locataire lié au User
            Locataire locataire = new Locataire();
            locataire.setUser(user);                    // Lien avec le User
            locataire.setAdresse(accountDto.getAdresse());
            locataire.setNumeroPermis(accountDto.getNumeroPermis());

            locataireRepository.save(locataire);       // Sauvegarde le Locataire
            LOGGER.info("Locataire créé pour user id = " + user.getId());
        

        } else {
            user = new User();
            user.setFirstName(accountDto.getFirstName());
            user.setLastName(accountDto.getLastName());
            user.setEmail(accountDto.getEmail());
            user.setTel(accountDto.getTel());
            user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
            user.setUsing2FA(accountDto.isUsing2FA());
            user.setRoles(Arrays.asList(roleRepository.findByName(roleName)));

            LOGGER.info("Avant save user : " + user);
            user = userRepository.save(user);
            LOGGER.info("Après save user, id = " + user.getId());
        }

        return user;

        
    
    }
    @Transactional
    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé : " + userId));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

   

    @Override
    public User getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(final User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(final User user) {
        final VerificationToken verificationToken = tokenRepository.findByUser(user);

        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        final PasswordResetToken passwordToken = passwordTokenRepository.findByUser(user);

        if (passwordToken != null) {
            passwordTokenRepository.delete(passwordToken);
        }

        userRepository.delete(user);
    }

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID()
            .toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public User getUserByPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token)
            .getUser();
    }

    @Override
    public Optional<User> getUserByID(final long id) {
        return userRepository.findById(id);
    }

    @Override
    public void changeUserPassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
            .getTime()
            - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        // tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public String generateQRUrl(User user) {
        return QR_PREFIX + URLEncoder.encode(
            String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                APP_NAME,
                user.getEmail(),
                user.getSecret(),
                APP_NAME
            ),
            StandardCharsets.UTF_8 // nouvelle API Java 11
        );
    }

    @Override
    public User updateUser2FA(boolean use2FA) {
        final Authentication curAuth = SecurityContextHolder.getContext()
            .getAuthentication();
        User currentUser = (User) curAuth.getPrincipal();
        currentUser.setUsing2FA(use2FA);
        currentUser = userRepository.save(currentUser);
        final Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), curAuth.getAuthorities());
        SecurityContextHolder.getContext()
            .setAuthentication(auth);
        return currentUser;
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals()
            .stream()
            .filter((u) -> !sessionRegistry.getAllSessions(u, false)
                .isEmpty())
            .map(o -> {
                if (o instanceof User) {
                    return ((User) o).getEmail();
                } else {
                    return o.toString();
                }
            })
            .collect(Collectors.toList());

    }
    public User findByEmailAndPhone(String email, String tele) {
        return userRepository.findByEmailAndTel(email,tele);
    }
    public User findByEmailAndNameAndTel(String email, String lastName, String firstName, String tel) {
        return userRepository.findByEmailAndLastNameAndFirstNameAndTel(email, lastName, firstName, tel);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User utilisateur) {
        return userRepository.save(utilisateur);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) return null;
        return userRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
    }

	 
	  public User enregistrerUtilisateur(User u) {
	        return userRepository.save(u);
	    }
	  public List<User> findAllClients() {
		    return userRepository.findAll(); // ou avec filtre selon rôle: client uniquement
		}
	  public User findByEmailIgnoreCase(String email) {
		    return userRepository.findByEmailIgnoreCase(email).orElse(null);
		}




	public UserDTO getUserById(Long id) {
	    Optional<User> userOpt = userRepository.findById(id);
	    if (userOpt.isPresent()) {
	        User user = userOpt.get();
	        return new UserDTO(
	            user.getId(),
	            user.getNom(),
	            user.getEmail(),
	            user.getTel(),
	            mapStatut(user.getEnabled()),
	            user.getVoitures() != null ? user.getVoitures().size() : 0,
	            user.getReservations() != null ? user.getReservations().size() : 0
	        );
	    }
	    return null;
	}
	


	public List<UserDTO> getOwnersDTO() {
	    List<User> owners = userRepository.findByRoles_Name(RoleUtilisateur.ROLE_OWNER.name());
	    if (owners == null) {
	        owners = Collections.emptyList();
	    }
	    
	    System.out.println("🔍 " + owners.size() + " propriétaires trouvés");
	    
	    return owners.stream()
	            .map(user -> {
	                // Conversion de Long en int
	                Long countLong = countVoituresForUser(user.getId());
	                int voituresCount = countLong != null ? countLong.intValue() : 0;
	                
	                return new UserDTO(
	                    user.getId(),
	                    user.getFirstName(),
	                    user.getLastName(),
	                    user.getEmail(),
	                    user.getTel(),
	                    mapStatut(user.getEnabled()),
	                    voituresCount,  // ← int
	                    0               // ← int
	                );
	            })
	            .collect(Collectors.toList());
	    }
	// AJOUTEZ cette méthode
	private Long countVoituresForUser(Long userId) {
	    try {
	        // Méthode 1: Via CarRepository
	        Long count = carRepository.countByUserId(userId);
	        System.out.println("   -> countByUserId(" + userId + ") = " + count);
	        
	        // Méthode 2: Si toujours 0, essayez une requête native
	        if (count == 0) {
	            Long nativeCount = carRepository.countByUserIdNative(userId);
	            System.out.println("   -> countByUserIdNative(" + userId + ") = " + nativeCount);
	            return nativeCount;
	        }
	        
	        return count;
	    } catch (Exception e) {
	        System.err.println("❌ Erreur comptage voitures pour user " + userId + ": " + e.getMessage());
	        return 0L;
	    }
	}
	public List<UserDTO> getClientsDTO() {
	    List<User> clients = userRepository.findByRoles_Name(RoleUtilisateur.ROLE_CLIENT.name());
	    return clients.stream()
	        .map(user -> new UserDTO(
	            user.getId(),
	            user.getFirstName(),
	            user.getLastName(),
	            user.getEmail(),
	            user.getTel(),
	            mapStatut(user.getEnabled()),
	            0,
	            user.getReservations() != null ? user.getReservations().size() : 0
	        ))
	        .collect(Collectors.toList());
	}
	public List<UserDTO> getVisitorsDTO() {
	    List<User> visitors = userRepository.findByRoles_Name(RoleUtilisateur.ROLE_VISITOR.name());
	    if (visitors == null) {
	        visitors = Collections.emptyList();
	    }
	    return visitors.stream()
	        .map(user -> new UserDTO(
	            user.getId(),
	            user.getFirstName(),
	            user.getLastName(),
	            user.getEmail(),
	            user.getTel(),
	            mapStatut(user.getEnabled()),
	            0,
	            0
	        ))
	        .collect(Collectors.toList());
	}


	
	private String mapStatut(Boolean enabled) {
	    if (enabled == null) return "Inconnu";
	    return enabled ? "Actif" : "Inactif";  // ← Changez "En Attente Vérif." en "Inactif"
	}
	// Exemple dans UserService
	public boolean blockUser(Long id) {
	    Optional<User> userOpt = userRepository.findById(id);
	    if (userOpt.isPresent()) {
	        User user = userOpt.get();
	        user.setEnabled(false); // bloquer
	        userRepository.save(user);
	        return true;
	    }
	    return false;
	}


	public Optional<User> findByIdOptional(Long id) {
		// TODO Auto-generated method stub
		return null;
	}


	public User getCurrentUser() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !authentication.isAuthenticated() 
	        || authentication instanceof AnonymousAuthenticationToken) {
	        return null;
	    }

	    Object principal = authentication.getPrincipal();
	    if (principal instanceof User) { 
	        return (User) principal;
	    }
	    
	    // Si le principal est un UserDetails, adapter selon ta classe
	    // Par exemple si tu as un UserDetails personnalisé qui référence User
	    // return ((CustomUserDetails) principal).getUser();

	    return null;
	}
	public List<User> getOwners() {
	    return userRepository.findByRoles(RoleUtilisateur.ROLE_OWNER);
	}
	@Override
	public VerificationToken getVerificationTokenForUser(User user) {
	    return tokenRepository.findByUser(user);
	}

	
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    User user = userRepository.findByEmail(email);
	    if (user == null) {
	        throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
	    }
	    return new CustomUserDetails(user);
	}


	 public void blockClient(Long id) {
	        User client = userRepository.findById(id).orElse(null);
	        if (client != null) {
	            client.setEnabled(false);
	            userRepository.save(client);
	        }
	    }

	    // Débloquer un client : mettre enabled à true
	    public void unblockClient(Long id) {
	        User client = userRepository.findById(id).orElse(null);
	        if (client != null) {
	            client.setEnabled(true);
	            userRepository.save(client);
	        }
	    }

	    // Récupérer les avis d’un client
	    

	    // Trouver un client par son ID
	    public User findClientById(Long id) {
	        return userRepository.findById(id).orElse(null);
	    }

	    // Récupérer le profil complet d’un client (utilisé dans ton contrôleur)
	    public User getClientProfile(Long utilisateurId) {
	        return userRepository.findById(utilisateurId).orElse(null);
	    }

	    // Récupérer les avis d’un client (idem getClientReviews)
	    public List<Avis> getAvisByClient(Long utilisateurId) {
	        return getClientReviews(utilisateurId);
	    }

	    // Nombre de réservations notées par un client (avec note non nulle)
	    public Long getNombreReservationsNotees(Long utilisateurId) {
	        List<Avis> avisList = getAvisByClient(utilisateurId);
	        if (avisList == null) return 0L; // sécurité
	        
	        return avisList.stream()
	                       .filter(a -> a.getNote() != null)
	                       .count();
	    }

	    public List<Avis> getClientReviews(Long utilisateurId) {
	        return avisRepository.findAvisByClientId(utilisateurId);
	    }
	    // Liste des clients avec réservations (exemple avec requête personnalisée)
	    public List<User> findClientsWithReservations(Long id) {
	        return userRepository.findDistinctClientsWithReservations(id);
	    }
		public List<Réservation> findReservationsByUserId(Long id) {
			// TODO Auto-generated method stub
			return null;
		}
		public void suspendUser(Long id) {
			// TODO Auto-generated method stub
			
		}

		public User findByUsername(String username) {
		    return userRepository.findByEmail(username);  // si c’est email qui correspond
		}
		public boolean toggleBlockUser(Long id) {
		    Optional<User> optionalUser = userRepository.findById(id);
		    if (!optionalUser.isPresent()) {
		        return false; // utilisateur non trouvé
		    }

		    User user = optionalUser.get();
		    boolean currentStatus = user.isEnabled(); // récupère le statut actuel

		    user.setEnabled(!currentStatus); // inverse le statut (true => false, false => true)

		    userRepository.save(user);
		    return true;
		}

		public List<Map<String, String>> getUserHistory(Long id) {
		    Optional<User> optionalUser = userRepository.findById(id);
		    if (optionalUser.isEmpty()) {
		        return Collections.emptyList();
		    }

		    User user = optionalUser.get();

		    List<Map<String, String>> historyList = new ArrayList<>();

		    for (Réservation reservation : user.getReservations()) {
		        Map<String, String> entry = new HashMap<>();
		        
		        // Utilise dateDebut car tu n'as pas dateReservation
		        entry.put("date", reservation.getDateDebut() != null ? reservation.getDateDebut().toString() : "N/A");
		        
		        entry.put("action", "Réservation");
		        
		        // Précaution : vérifier si voiture n'est pas null
		        String modeleVoiture = (reservation.getVoiture() != null && reservation.getVoiture().getModele() != null)
		                ? reservation.getVoiture().getModele()
		                : "Inconnu";

		        entry.put("details", "Voiture : " + modeleVoiture);

		        historyList.add(entry);
		    }

		    return historyList;
		}
		public boolean resetPassword(String oldPassword, String newPassword) {
		    User currentUser = getCurrentUser();
		    if (currentUser == null) {
		        throw new UsernameNotFoundException("Utilisateur non connecté");
		    }

		    // Vérifier l'ancien mot de passe (exemple avec Spring Security)
		    if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
		        return false;  // ancien mot de passe incorrect
		    }

		    // Encoder et mettre à jour le nouveau mot de passe
		    currentUser.setPassword(passwordEncoder.encode(newPassword));
		    userRepository.save(currentUser);
		    return true;
		}


	    @Autowired
	    private ProprietaireRepository propritaireRepository;

	    public List<Propritaire> getProprietairesAvecDescription() {
	        return propritaireRepository.findPropritaireWithDescription("ROLE_OWNER");
	    }
	    public User getUserByEmail(String email) {
	        return userRepository.findByEmailIgnoreCase(email)
	                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
	    }

	    public void updateUserProfile(String email, UserDTO updatedUserDto) {
	        User existingUser = getUserByEmail(email);

	        existingUser.setFirstName(updatedUserDto.getFirstName());
	        existingUser.setLastName(updatedUserDto.getLastName());
	        existingUser.setTelephone(updatedUserDto.getTelephone());
	        existingUser.setEmail(updatedUserDto.getEmail());

	        if (updatedUserDto.getPassword() != null && !updatedUserDto.getPassword().isEmpty()) {
	            existingUser.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
	        }

	        userRepository.save(existingUser);
	    }

	    public boolean userExistsByEmail(String email) {
	        return userRepository.findByEmail(email) != null;
	    }

}
