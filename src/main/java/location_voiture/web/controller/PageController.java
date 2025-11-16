package location_voiture.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties.Request;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.dto.ClientDTO;
import location_voiture.persistence.dto.OwnerWithRating;
import location_voiture.persistence.dto.ProprietaireDTOS;
import location_voiture.persistence.dto.ReservationCreateDTO;
import location_voiture.persistence.dto.ResponseDTO;
import location_voiture.persistence.dto.UserDTO;
import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Disponibilite;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Gallery;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Message;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutDisponibilite;
import location_voiture.persistence.model.StatutReservation;
import location_voiture.persistence.model.TypeMessage;
import location_voiture.persistence.model.TypeReservation;
import location_voiture.repository.CarRepository;
import location_voiture.repository.DisponibiliteRepository;
import location_voiture.repository.GalleryRepository;
import location_voiture.repository.MessageRepository;
import location_voiture.repository.ProprietaireRepository;
import location_voiture.service.CarService;
import location_voiture.service.EmailService;
import location_voiture.service.FactureService;
import location_voiture.service.GalleryService;
import location_voiture.service.PaiementService;
import location_voiture.service.ProprietaireService;
import location_voiture.service.ReservationService;
import location_voiture.web.controller.OwnerController.ReservationResponse;
import ma.abisoft.persistence.dao.RoleRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/Siteoffeciel")
public class PageController {
	
	 @Autowired
	    private  UserRepository userRepository;
	 @Autowired
	    private   MessageRepository messageRepository;
	 @Autowired
	    private   RoleRepository roleRepository;

	    @Autowired
	    private ProprietaireService propritaireService;
	 
	 @Autowired
	private  EmailService emailService;
	 @Autowired
	 private PasswordEncoder passwordEncoder;

	                         
	        
    @Autowired
    private ReservationService reservationService;

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository utilisateurRepository;  
    @Autowired
    private GalleryRepository galleryRepository;
    @Autowired
    private DisponibiliteRepository disponibiliteRepository;
    @Autowired
    private ProprietaireRepository propritaireRepository;
    @Autowired
    private CarService carservice;

    @Autowired
    private PaiementService paiementService;
    @Autowired
    private GalleryService galleryService;
    @Autowired
    private FactureService factureService;  
    
    @Autowired
    private UserService userService; // Ajout du service User pour gérer les utilisateurs
    @Autowired
    private CarService carService; 
    @GetMapping("/car-details/{id}")
    public String carDetails(@PathVariable("id") Long voitureId, Model model) {
        Car voiture = carservice.getCarById(voitureId);
        if (voiture == null) {
            return "Voitures/404";
        }
        model.addAttribute("voiture", voiture);
        return "Voitures/car-details";
    }

    @GetMapping("/reservationcar/{id}")
    public String reservationFormForCar(@PathVariable("id") Long voitureId, Model model) {
        Car voiture = carservice.getCarById(voitureId);
        if (voiture == null) {
            return "Voitures/404";
        }
        model.addAttribute("voitureAReserver", voiture);
        return "Voitures/reservation";
    }
   
    @GetMapping("/VoitureDesp")
    public String listCars(
            @RequestParam(value = "ville", required = false) String ville,
            @RequestParam(value = "agenceId", required = false) Long agenceId,
            @RequestParam(value = "dateDebut", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin,
            Model model,HttpServletRequest request) {
        
        logger.info("=== PAGE DISPONIBILITÉS VOITURES ===");
        logger.info("Ville: {}, Agence ID: {}", ville, agenceId);
        
        String sanitizedVille = ville != null ? ville.trim() : "";
        List<Car> cars;
        boolean noCitySelected = sanitizedVille.isEmpty();
        
        // ========== INFO DÉBUT ==========
        logger.info("=== INFO DÉBUT VoitureDesp ===");
        logger.info("Paramètres reçus:");
        logger.info("   - Ville: '{}'", ville);
        logger.info("   - Ville nettoyée: '{}'", sanitizedVille);
        logger.info("   - Agence ID: {}", agenceId);
        logger.info("   - Date début: {}", dateDebut != null ? dateDebut.toString() : "null");
        logger.info("   - Date fin: {}", dateFin != null ? dateFin.toString() : "null");
        logger.info("=== DEBUG PARAMÈTRES URL ===");
        logger.info("URL complète: {}", request.getRequestURL() + "?" + request.getQueryString());
        logger.info("ville: {}", ville);
        logger.info("agenceId: {}", agenceId);
        logger.info("dateDebut: {}", dateDebut);
        logger.info("dateFin: {}", dateFin);
        // Formater les dates si elles existent
        if (dateDebut != null && dateFin != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            logger.info("   - Date début formatée: {}", sdf.format(dateDebut));
            logger.info("   - Date fin formatée: {}", sdf.format(dateFin));
        } else {
            logger.info("   - Date début: null");
            logger.info("   - Date fin: null");
            logger.info("   - Aucune date fournie dans l'URL");
        }
        // ========== INFO FIN ==========
        
        // Récupérer le propriétaire si agenceId est fourni
        Propritaire proprietaire = null;
        if (agenceId != null) {
            Optional<Propritaire> proprietaireOpt = propritaireRepository.findById(agenceId);
            if (proprietaireOpt.isPresent()) {
                proprietaire = proprietaireOpt.get();
                model.addAttribute("proprietaire", proprietaire);
                logger.info("Propriétaire trouvé: {}", proprietaire.getRaisonsociale());
                
                // Info propriétaire
                infoProprietaire(proprietaire);
            } else {
                logger.warn("Propriétaire NON trouvé pour ID: {}", agenceId);
            }
        } else {
            logger.info("Aucun agenceId fourni");
        }
        
        // Logique de recherche des voitures
        cars = rechercherVoitures(sanitizedVille, proprietaire, noCitySelected);
        
        // Filtrage par disponibilité selon la TABLE disponibilite
        if (dateDebut != null && dateFin != null) {
            logger.info("Filtrage par dates - Début: {}, Fin: {}", dateDebut, dateFin);
            logger.info("Voitures avant filtrage: {}", cars.size());
            
            List<Car> voituresDisponibles = new ArrayList<>();
            for (Car car : cars) {
                boolean disponible = estVoitureDisponible(car, dateDebut, dateFin);
                if (disponible) {
                    voituresDisponibles.add(car);
                }
                logger.info("Voiture {} - {} {}: {}", 
                    car.getId(), car.getMarque(), car.getModele(), 
                    disponible ? "DISPONIBLE" : "INDISPONIBLE");
            }
            cars = voituresDisponibles;
            logger.info("Voitures disponibles après vérification table disponibilite: {}", cars.size());
        } else {
            logger.info("Aucun filtrage par dates");
        }
        
        // ========== INFO FINAL ==========
        logger.info("=== INFO FIN VoitureDesp ===");
        logger.info("Résultat final: {} voitures à afficher", cars.size());
        logger.info("Ville sélectionnée: '{}'", sanitizedVille);
        logger.info("Aucune voiture trouvée: {}", !noCitySelected && cars.isEmpty());
        // ========== INFO FIN ==========
        
        // Ajout des attributs au modèle
        ajouterAttributsAuModele(model, cars, sanitizedVille, noCitySelected, dateDebut, dateFin);
        
        return "Siteoffeciel/VoitureDesp";
    }

    // Méthode pour rechercher les voitures selon les critères
    private List<Car> rechercherVoitures(String ville, Propritaire proprietaire, boolean noCitySelected) {
        if (!noCitySelected) {
            if (proprietaire != null) {
                logger.info("Recherche Ville + Agence - Propriétaire: {}, Ville: '{}'", 
                		
                    proprietaire.getRaisonsociale(), ville);
                
                List<Car> cars = carRepository.findByProprietaireAndVilleContaining(proprietaire, ville);
                logger.info("Voitures de l'agence {} contenant '{}': {}", 
                    proprietaire.getRaisonsociale(), ville, cars.size());
                
                infoResultatsRecherche(cars, "Ville + Agence");
                return cars;
                
            } else {
                logger.info("Recherche Ville seule - Ville: '{}'", ville);
                
                List<Car> cars = carRepository.findByVilleContaining(ville);
                logger.info("Voitures contenant '{}': {}", ville, cars.size());
                
                infoResultatsRecherche(cars, "Ville seule");
                return cars;
            }
        } else {
            if (proprietaire != null) {
                // Cas 3: Agence seulement (toutes ses voitures)
                List<Car> cars = carRepository.findByProprietaire(proprietaire);
                logger.info("Toutes les voitures de l'agence {}: {}", 
                    proprietaire.getRaisonsociale(), cars.size());
                return cars;
            } else {
                // Cas 4: Aucun filtre
                List<Car> cars = carRepository.findAll();
                logger.info("Toutes les voitures: {}", cars.size());
                return cars;
            }
        }
    }

    // Méthode pour vérifier la disponibilité d'une voiture
    private boolean estVoitureDisponible(Car car, Date dateDebut, Date dateFin) {
        try {
            // Convertir Date en LocalDate
            LocalDate debut = dateDebut.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fin = dateFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            logger.info("🔍 Vérification disponibilité voiture {}: {} à {}", 
                car.getId(), debut, fin);
            
            // Vérifier s'il existe AU MOINS UNE indisponibilité qui chevauche la période demandée
            List<Disponibilite> indisponibilites = disponibiliteRepository
                .findByCarAndStatut(car, "INDISPONIBLE");
            
            boolean estDisponible = true;
            
            for (Disponibilite dispo : indisponibilites) {
                // Si la période demandée chevauche une période d'indisponibilité
                if (datesSeChevauchent(debut, fin, dispo.getDateDebut(), dispo.getDateFin())) {
                    estDisponible = false;
                    logger.info("❌ VOITURE {} INDISPONIBLE - Conflit avec réservation du {} au {}", 
                        car.getId(), dispo.getDateDebut(), dispo.getDateFin());
                    break;
                }
            }
            
            if (estDisponible) {
                logger.info("✅ VOITURE {} DISPONIBLE - Aucune réservation conflictuelle", car.getId());
            }
            
            return estDisponible;
            
        } catch (Exception e) {
            logger.error("Erreur vérification disponibilité voiture {}", car.getId(), e);
            return false; // En cas d'erreur, on considère comme indisponible
        }
    }

    // Méthode pour vérifier le chevauchement des dates
    private boolean datesSeChevauchent(LocalDate debut1, LocalDate fin1, LocalDate debut2, LocalDate fin2) {
        // Il y a chevauchement si:
        // - La période 1 commence avant la fin de la période 2 ET
        // - La période 1 se termine après le début de la période 2
        return !debut1.isAfter(fin2) && !fin1.isBefore(debut2);
    }

    // Méthode pour ajouter les attributs au modèle
    private void ajouterAttributsAuModele(Model model, List<Car> cars, String ville, 
                                         boolean noCitySelected, Date dateDebut, Date dateFin) {
        model.addAttribute("cars", cars);
        model.addAttribute("selectedCity", ville);
        model.addAttribute("noCarsFound", !noCitySelected && cars.isEmpty());
        model.addAttribute("noCitySelected", noCitySelected);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        model.addAttribute("dateDebut", dateDebut != null ? sdf.format(dateDebut) : "");
        model.addAttribute("dateFin", dateFin != null ? sdf.format(dateFin) : "");
        
        // Pour le formulaire de recherche
        List<String> cities = carRepository.findDistinctVilles();
        model.addAttribute("cities", cities);
    }

    // Méthode pour info du propriétaire
    private void infoProprietaire(Propritaire proprietaire) {
        logger.info("Propriétaire trouvé:");
        logger.info("   - ID: {}", proprietaire.getId());
        logger.info("   - Nom: {}", proprietaire.getRaisonsociale());
        logger.info("   - ICE: {}", proprietaire.getIce());
        
        List<Car> toutesVoituresProprietaire = carRepository.findByProprietaire(proprietaire);
        logger.info("Toutes les voitures du propriétaire: {}", toutesVoituresProprietaire.size());
        
        if (toutesVoituresProprietaire.isEmpty()) {
            logger.info("Le propriétaire n'a AUCUNE voiture !");
        } else {
            toutesVoituresProprietaire.forEach(voiture -> {
                logger.info("Voiture {}: {} {} - Ville: '{}' - Prix: {}€ - Immatriculation: {}", 
                    voiture.getId(), voiture.getMarque(), voiture.getModele(), 
                    voiture.getVille(), voiture.getPrixJournalier(), voiture.getImmatriculation());
            });
        }
    }

    // Méthode pour info des résultats de recherche
    private void infoResultatsRecherche(List<Car> cars, String typeRecherche) {
        logger.info("Résultats recherche {}: {} voitures", typeRecherche, cars.size());
        
        if (cars.isEmpty()) {
            logger.info("AUCUNE voiture trouvée avec ces critères");
        } else {
            cars.forEach(car -> {
                logger.info("Trouvé: {} {} - Ville: '{}'", 
                    car.getMarque(), car.getModele(), car.getVille());
            });
        }
    }

    // Méthode utilitaire pour info des caractères
    private String getCharCodes(String input) {
        if (input == null) return "null";
        return input.chars()
            .mapToObj(Integer::toString)
            .collect(Collectors.joining(" "));
    }
    @GetMapping("/download-guide")
    public ResponseEntity<Resource> downloadGuide() throws IOException {
        // Chemin du fichier dans resources/static
        ClassPathResource resource = new ClassPathResource("static/guideuser.pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guide.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(resource.getInputStream()));}
    
    
    /*@GetMapping("/api/voitures/disponibles")
    public List<Car> getCarsDisponibles(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        return carService.getCarsDisponibles(start, end);
    }*/
    
    @PostMapping("/check-user")
    public ResponseEntity<Map<String, Boolean>> checkUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean exists = userService.userExistsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    @Transactional
    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userData) {
        try {
            // Vérification si l'email existe déjà
            if (utilisateurRepository.existsByEmail(userData.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO(false, "Email déjà utilisé"));
            }
            System.out.println(">>> Méthode createUser appelée !");
            System.out.println("UserDTO reçu : " + userData);
            System.out.println("getPermis() = " + userData.getPermis());
            System.out.println("getAdresse() = " + userData.getAdresse());
            // Génération du mot de passe
            String password = generateRandomPassword(8);

            // Création de l'utilisateur
            User user = new User();
            user.setFirstName(userData.getFirstName());
            user.setLastName(userData.getLastName());
            user.setEmail(userData.getEmail());
            user.setTel(userData.getTelephone());
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);
            System.out.println("Adresse DTO = " + userData.getAdresse());
            System.out.println("Permis DTO = " + userData.getPermis());
            System.out.println("tele DTO = " + userData.getTelephone());

            Role roleClient = roleRepository.findByName(RoleUtilisateur.ROLE_CLIENT.name());
            if (roleClient == null) throw new RuntimeException("Role CLIENT introuvable");
            
            if (user.getRoles() == null) {
                user.setRoles(new HashSet<>());
            }
            user.getRoles().add(roleClient);

            // Association utilisateur-rôle

            // Création du locataire
            Locataire locataire = new Locataire();
            locataire.setAdresse(userData.getAdresse());
            locataire.setNumeroPermis(userData.getPermis());
            locataire.setUser(user);

            user.setLocataire(locataire); // optionnel si mappedBy et cascade ALL

            utilisateurRepository.save(user);



            // Debug avant save
            System.out.println("User avant save : " + user);
            System.out.println("Locataire avant save : " + locataire);

            // Sauvegarde => Hibernate va persister User + Locataire en cascade
            utilisateurRepository.save(user);

            // Retour de la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("statut", true);
            response.put("note", "Compte créé avec succès.");
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(false, "Erreur lors de la création du compte : " + e.getMessage()));
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
    @PostMapping(value = "/create-reservation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReservation(
            @RequestPart("reservation") @Valid ReservationCreateDTO reservationCreateDTO,
            BindingResult bindingResult,
            @RequestPart(value = "virementFile", required = false) MultipartFile virementFile) {
    	logger.info("---- Début de createReservation ----");
        System.out.println("---- Début de createReservation ----");

        // Affiche le DTO reçu
        System.out.println("ReservationCreateDTO reçu :");
        System.out.println("CarId : " + reservationCreateDTO.getCarId());
        System.out.println("FirstName : " + reservationCreateDTO.getFirstName());
        System.out.println("LastName : " + reservationCreateDTO.getLastName());
        System.out.println("Email : " + reservationCreateDTO.getEmail());
        System.out.println("Phone : " + reservationCreateDTO.getPhone());
        System.out.println("PickupAddress : " + reservationCreateDTO.getPickupAddress());
        System.out.println("ReturnAddress : " + reservationCreateDTO.getReturnAddress());
        System.out.println("StartDate : " + reservationCreateDTO.getStartDate());
        System.out.println("EndDate : " + reservationCreateDTO.getEndDate());
        System.out.println("PaymentMethod : " + reservationCreateDTO.getPaymentMethod());

        if (virementFile != null) {
            System.out.println("Fichier virement reçu : " + virementFile.getOriginalFilename() + ", taille : " + virementFile.getSize());
        } else {
            System.out.println("Pas de fichier virement reçu.");
        }

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            System.out.println("Erreurs de validation : " + errorMsg);
            return ResponseEntity.badRequest().body("Erreurs de validation : " + errorMsg);
        }

        try {
            if (virementFile != null && !virementFile.isEmpty()) {
                reservationCreateDTO.setVirementFileName(virementFile.getOriginalFilename());
                System.out.println("Nom fichier virement stocké dans DTO : " + reservationCreateDTO.getVirementFileName());
                // TODO: sauvegarder le fichier sur disque ou cloud si besoin
            }
            reservationCreateDTO.setTypeReservation(TypeReservation.DISTANCE);
            Réservation reservation = reservationService.createReservation(reservationCreateDTO);
            
            System.out.println("Réservation créée avec ID : " + reservation.getId());

            Facture facture = factureService.creerFactureDepuisReservation(reservation);
            System.out.println("Facture créée avec ID : " + facture.getId());
         // Créer indisponibilité pour la voiture
            Disponibilite disp = new Disponibilite();
            disp.setCar(reservation.getVoiture()); // getter correct
            disp.setDateDebut(reservation.getDateDebut()); // ou .toLocalDate() si besoin
            disp.setDateFin(reservation.getDateFin());     // ou .toLocalDate() si besoin
            disp.setStatut(StatutDisponibilite.INDISPONIBLE.name());
            
            disponibiliteRepository.save(disp);



            return ResponseEntity.ok(new ReservationResponse(
                    reservation.getId(),
                    "Réservation créée avec succès.",
                    "/Siteoffeciel/factures/" + facture.getId() + "/pdf"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur interne.");
        }
    }


    public static class ReservationResponse {
        private Long reservationId;
        private String message;
        private String factureUrl;

        public ReservationResponse(Long reservationId, String message, String factureUrl) {
            this.reservationId = reservationId;
            this.message = message;
            this.factureUrl = factureUrl;
        }

        public Long getReservationId() {
            return reservationId;
        }

        public String getMessage() {
            return message;
        }

        public String getFactureUrl() {
            return factureUrl;
        }
    }
    
    
    
    
   
    
    
    
    // Création d'une nouvelle réservation (POST)
    @PostMapping("/client/reservations/new")
    @ResponseBody
    public ResponseEntity<?> createNewReservation(
            @RequestParam String adressePriseEnCharge,
            @RequestParam String adresseRestitution,
            @RequestParam String dateDebut,
            @RequestParam String dateFin,
            @RequestParam String typeVoiture,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }
        // Logique de réservation ici...
        return ResponseEntity.ok("Réservation créée");
    }

    // Page réservation avec filtre par ville et date
    @GetMapping("/reserver-voiture")
    public String showReserverVoiture(
            @RequestParam(required = false) String locPickup,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd,
            Model model) {

        List<Car> voitures;
        if (locPickup != null && !locPickup.isEmpty()) {
          //  voitures = carRepository.findAvailableCarsByVille(Car.ETAT_DISPONIBLE, locPickup);
        } else {
           // voitures = carRepository.findByDisponible(Car.ETAT_DISPONIBLE);
        }

        if (dateStart != null && dateEnd != null && !dateStart.isEmpty() && !dateEnd.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start = LocalDate.parse(dateStart, formatter);
                LocalDate end = LocalDate.parse(dateEnd, formatter);

                if (end.isBefore(start)) {
                    model.addAttribute("error", "La date de fin doit être après la date de début.");
                } else {
                    model.addAttribute("message", "Résultats pour " + locPickup + " du " + dateStart + " au " + dateEnd);
                }
            } catch (Exception e) {
                model.addAttribute("error", "Format de date invalide.");
            }
        }

       // model.addAttribute("voitures", voitures);
        model.addAttribute("section", "reserver");
        return "reserver-voiture";
    }
   

    @GetMapping("/api/client/current")
    public ResponseEntity<ClientDTO> getCurrentClient(Principal principal) {
    	
        System.out.println("Entrée dans getCurrentClient");

        if (principal == null) {
            System.out.println("Principal est null — utilisateur non connecté");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Cas où principal contient directement un objet User
        if (principal instanceof Authentication) {
            Object userObj = ((Authentication) principal).getPrincipal();

            if (userObj instanceof User) {
                User user = (User) userObj;
                System.out.println("Utilisateur récupéré depuis principal : " + user.getEmail());
                System.out.println("First Name: " + user.getFirstName());
                System.out.println("Last Name: " + user.getLastName());
                System.out.println("Tel: " + user.getTel());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Permis: " + user.getNumeroPermis());

                return ResponseEntity.ok(new ClientDTO(user));
            } else {
                System.out.println("Le principal n'est pas de type User : " + userObj.getClass().getName());
            }
        }

        System.out.println("Impossible de récupérer l'utilisateur depuis principal");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/factures/{id}/pdf")
    public ResponseEntity<byte[]> telechargerFacturePDF(@PathVariable Long id) {
        Optional<Facture> optionalFacture = factureService.findById(id);

        if (!optionalFacture.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Facture facture = optionalFacture.get();

        if (facture.getFacturePdf() == null || facture.getFacturePdf().length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "attachment; filename=\"facture_" + id + ".pdf\"");

        return new ResponseEntity<>(facture.getFacturePdf(), headers, HttpStatus.OK);
    }
   

    @GetMapping("/index")
    public String index(@RequestParam(value = "ville", required = false) String ville, Model model) {
        logger.info("===> Contrôleur /index appelé !");
        
        // ========== NETTOYAGE INITIAL ==========
        if (ville != null) {
            ville = ville.trim().replaceAll("\\p{C}", "");
        }
        
        // ========== DEBUG DÉBUT ==========
        System.out.println("=== DEBUG DÉBUT ===");
        System.out.println("Ville recherchée nettoyée: '" + ville + "'");
        
        // Vérifiez tous les propriétaires AVANT la recherche
        List<Propritaire> tousProprietaires = carRepository.findAllProprietaires();
        System.out.println("👥 Total propriétaires avec voitures: " + tousProprietaires.size());
        
        // Nettoyage des villes en base
        tousProprietaires.forEach(p -> {
            if (p.getVoitures() != null && !p.getVoitures().isEmpty()) {
                p.getVoitures().forEach(v -> {
                    if (v.getVille() != null) {
                        String villeNettoyee = v.getVille().replaceAll("\\p{C}", "").trim();
                        v.setVille(villeNettoyee);
                        System.out.println("   🚗 " + v.getMarque() + " " + v.getModele() + " - Ville nettoyée: '" + villeNettoyee + "'");
                    }
                });
            }
        });
        
        // ========== LOGIQUE PRINCIPALE CORRIGÉE ==========
        List<Propritaire> agences;
        boolean rechercheEchouee = false;
        boolean aucuneVoiture = false;
        
        if (ville == null || ville.isEmpty()) {
            // CAS 1: Aucune ville spécifiée - afficher tout
            agences = carRepository.findAllProprietaires();
            System.out.println("🏠 Chargement TOUS les propriétaires: " + agences.size());
            aucuneVoiture = agences.isEmpty();
        } else {
            // CAS 2: Ville spécifiée - recherche exacte
            agences = carRepository.findProprietairesParVille(ville);
            System.out.println("🔍 Recherche pour '" + ville + "' - Résultats: " + agences.size());
            
            if (agences.isEmpty()) {
                // AUCUN RÉSULTAT POUR LA VILLE RECHERCHÉE
                rechercheEchouee = true;
                aucuneVoiture = true;
                agences = Collections.emptyList(); // ← NE PAS AFFICHER DE FALLBACK
                System.out.println("❌ AUCUN résultat pour '" + ville + "'");
            } else {
                // RÉSULTATS TROUVÉS
                aucuneVoiture = false;
                System.out.println("✅ Résultats trouvés pour '" + ville + "'");
            }
        }
        
        System.out.println("✅ aucuneVoiture = " + aucuneVoiture + ", agences = " + agences.size());
        System.out.println("=== DEBUG FIN ===");
        
        // ========== CONFIGURATION DU MODÈLE ==========
        model.addAttribute("aucuneVoiture", aucuneVoiture);
        model.addAttribute("rechercheEchouee", rechercheEchouee);
        model.addAttribute("villeSelectionnee", ville != null ? ville : "Toutes les villes");
        model.addAttribute("nombreAgences", agences.size());
        model.addAttribute("agences", agences);
        
        // ========== DONNÉES SUPPLÉMENTAIRES ==========
        // Chargement des voitures par catégorie
        model.addAttribute("marques", carservice.getDistinctMarques());
        model.addAttribute("modeles", carservice.getDistinctModeles());
        model.addAttribute("annees", carservice.getDistinctAnnees());
        model.addAttribute("villes", carservice.getDistinctVilles());
        
        // Owners with ratings
        List<OwnerWithRating> ownersWithRating = userRepository.findAllOwnersWithRatings();
        model.addAttribute("ownersWithRating", ownersWithRating);
        
        // Calcul du prix minimum par agence
        Map<Long, Double> prixMinParAgence = new HashMap<>();
        for (Propritaire agence : agences) {
            List<Car> voitures = agence.getVoitures();
            if (voitures != null && !voitures.isEmpty()) {
                Double minPrix = voitures.stream()
                                         .mapToDouble(Car::getPrixJournalier)
                                         .min()
                                         .orElse(0);
                prixMinParAgence.put(agence.getId(), minPrix);
            } else {
                prixMinParAgence.put(agence.getId(), 0.0);
            }
        }
        model.addAttribute("prixMinParAgence", prixMinParAgence);
     // Calcul de la note moyenne par agence
        Map<Long, Double> moyenneNoteParAgence = new HashMap<>();
        for (Propritaire agence : agences) {
            double moyenne = ownersWithRating.stream()
                .filter(o -> o.getOwner() != null && o.getOwner().getId().equals(agence.getId()))
                .mapToDouble(OwnerWithRating::getMoyenneNote)
                .findFirst()
                .orElse(0.0);
            moyenneNoteParAgence.put(agence.getId(), moyenne);
        }
        model.addAttribute("moyenneNoteParAgence", moyenneNoteParAgence);

        // Récupération des propriétaires filtrés
        List<Propritaire> proprietaires = userService.getProprietairesAvecDescription();
        model.addAttribute("proprietaires", proprietaires);
        
        return "Siteoffeciel/index";
    }
    

    // Ajoutez cette méthode pour debug des caractères
  

    @GetMapping("/about")
    public String about(Model model) {
        return "Siteoffeciel/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
    	
        return "Siteoffeciel/contact";
    }
    @PostMapping("/contact/envoyer")
    @ResponseBody
    public ResponseEntity<?> envoyerMessageDepuisContact(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String tel,
            @RequestParam String sujet,
            @RequestParam String contenu) {

        // 1. Vérifie si l'utilisateur existe déjà
        User utilisateur = userRepository.findByEmail(email);
        if (utilisateur == null) {
            utilisateur = new User();
            utilisateur.setFirstName(firstName);
            utilisateur.setLastName(lastName);
            utilisateur.setEmail(email);
            utilisateur.setTel(tel);
            utilisateur.setEnabled(false);
            utilisateur.setPassword(""); // pas de mot de passe à ce stade

            // Affecter rôle VISITOR
            Role roleVisitor = roleRepository.findByName("ROLE_VISITOR");
            utilisateur.setRoles(List.of(roleVisitor));

            userRepository.save(utilisateur);
            
            
            
            
        }

        // 2. Créer et sauvegarder le message
        Message message = new Message();
        message.setContent(contenu);
        message.setType(TypeMessage.CONTACT);
        message.setDateEnvoi(LocalDateTime.now());
        message.setUtilisateur(utilisateur);

        messageRepository.save(message);
     // Envoi de l'email à l'utilisateur
        String sujetMail = "Confirmation de réception de votre message";
        String contenuMail = "Bonjour " + utilisateur.getFirstName() + ",\n\n" +
                "Nous avons bien reçu votre message :\n\n\"" + contenu + "\"\n\n" +
                "Notre équipe vous contactera bientôt.\n\nCordialement,\nL'équipe Support";

        emailService.envoyerEmail(utilisateur.getEmail(), sujetMail, contenuMail);


        return ResponseEntity.ok("Message envoyé avec succès !");
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "Voitures/login";
    }
   

    

    @GetMapping("/register")
    public String register(Model model) {
        return "Voitures/register";
    }

   

    @GetMapping("/faq")
    public String carListing(Model model) {
        return "Siteoffeciel/faq";
    }

   

   

    @GetMapping("/gallery")
    public String gallery(Model model) {
    	    List<Gallery> galleryImages = galleryService.findAll();
    	    model.addAttribute("galleryImages", galleryImages);
    	    List<Car> voitures = carRepository.findAll();
    	    
            model.addAttribute("voitures", voitures);
            
        return "Siteoffeciel/gallery";
    }
    @GetMapping("/gallery/{carId}")
    @ResponseBody
    public List<Gallery> getGallery(@PathVariable Long carId) {
        return carservice.getGalleryByCarId(carId);
    }

   

    @GetMapping("/mettrelocation")
    public String afficherFormulaireAjout(Model model) {
        return "Voitures/mettrelocation";
    }
  
    @PostMapping("/mettrelocation")
    public String ajouterVoiture(@RequestParam("marque") String marque,
                                 @RequestParam("modele") String modele,
                                 @RequestParam("description") String description,
                                 @RequestParam("ville") String ville,
                                 @RequestParam("Immatriculation") String immatriculation,
                                 @RequestParam("annee") int annee,
                                 @RequestParam("type") String type,
                                 @RequestParam("carburant") String carburant,
                                 @RequestParam("boite") String boite,
                                 @RequestParam("nombrePlaces") int nombrePlaces,
                                 @RequestParam("prix") Double prix,
                                 @RequestParam("kilometrage") int kilometrage,
                                 @RequestParam("disponibilite") String disponibilite,
                                 @RequestParam("categorie") String categorie,
                                 @RequestParam("image") MultipartFile imageFile,
                                 Model model) {

        Car car = new Car();
        car.setMarque(marque);
        car.setModele(modele);
        car.setDescription(description);
        car.setVille(ville);
        car.setImmatriculation(immatriculation);
        car.setAnnee(annee);
        car.setType(type);
        car.setCarburant(carburant);
        car.setBoite(boite);
        car.setPlaces(nombrePlaces);
        car.setPrixJournalier(prix);
        car.setKilometrage(kilometrage);
       // car.setDisponible(disponibilite);
        car.setCategorie(categorie);

        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/";
                String fileName = imageFile.getOriginalFilename();
                File saveFile = new File(uploadDir + fileName);
                saveFile.getParentFile().mkdirs();
                imageFile.transferTo(saveFile);
                car.setImagePrincipaleURL(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        carRepository.save(car);
        return "redirect:/Voitures/car-listing";
    }

    @GetMapping("/reservation")
    public String reservation(Model model) {
        return "Voitures/reservations";
    }

    @GetMapping("/message")
    public String message(Model model) {
        return "Voitures/message";
    }

  
    @GetMapping("/politique")
    public String shoppingCart(Model model) {
        return "Siteoffeciel/politique";
    }

   

    @GetMapping("/conditions")
    public String blog(Model model) {
        return "Siteoffeciel/conditions";
    }

    @GetMapping("/single-blog")
    public String blogDetails(Model model) {
        return "Voitures/single-blog";
    }
    @GetMapping("/{id}/gallery")
    @ResponseBody
    public List<Gallery> getCarGallery(@PathVariable Long id) {
        return galleryService.findByVoitureId(id);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Car>> filterCars(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        List<Car> cars = filterCarsInRepository(search, category);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getVoitureById(@PathVariable Long id) {
        Car voiture = carservice.getCarById(id);
        if (voiture != null) {
            return ResponseEntity.ok(voiture);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateVoiture(@ModelAttribute Car voiture) {
        carservice.updateCar(voiture);
        return ResponseEntity.ok().build();
    }

    private List<Car> filterCarsInRepository(String search, String category) {
        if (search == null && category == null) {
            return carRepository.findAll();
        }
        if (search != null && category != null) {
            return carRepository.findByMarqueContainingOrModeleContainingOrImmatriculationContainingAndType(
                    search, search, search, category);
        }
        if (search != null) {
            return carRepository.findByMarqueContainingOrModeleContainingOrImmatriculationContaining(
                    search, search, search);
        }
        return carRepository.findByType(category);
    }

    @GetMapping("/images/{ownerId}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String ownerId, @PathVariable String filename) {
        try {
            Long proprietaireId = Long.parseLong(ownerId);  // Conversion pour validation
            // Optionnel : Appelle init si besoin (mais pas nécessaire à chaque GET)
            // fileService.initProprietaireFolders(proprietaireId);

            String fullPath = "static/uploads/proprietaire_" + ownerId + "/images/" + filename;
            Resource resource = new ClassPathResource(fullPath);
            
            if (!resource.exists()) {
                logger.warn("Fichier non trouvé : {}", fullPath);  // Log
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Ou détecte dynamiquement via filename.endsWith(".png") etc.
                    .body(resource);
        } catch (NumberFormatException e) {
            logger.error("ID propriétaire invalide : {}", ownerId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'image", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/logos/{ownerId}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getLogo(@PathVariable String ownerId, @PathVariable String filename) {
        try {
            Long proprietaireId = Long.parseLong(ownerId);  // Conversion pour validation
            // Optionnel : Appelle init si besoin (mais pas nécessaire à chaque GET)
            // fileService.initProprietaireFolders(proprietaireId);

            String fullPath = "static/uploads/proprietaire_" + ownerId + "/logo/" + filename;
            Resource resource = new ClassPathResource(fullPath);
            
            if (!resource.exists()) {
                logger.warn("Fichier non trouvé : {}", fullPath);  // Log
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Ou détecte dynamiquement via filename.endsWith(".png") etc.
                    .body(resource);
        } catch (NumberFormatException e) {
            logger.error("ID propriétaire invalide : {}", ownerId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'image", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/search")
    public String search(@RequestParam String query, Model model) {
        model.addAttribute("searchQuery", query);
        return "Voitures/search-results";
    }

    @GetMapping("/testimonials")
    public String testimonials(Model model) {
        return "Voitures/testimonials";
    }

  

    // Ajout pour gérer la réservation via AJAX
    @PostMapping("/car-booking")
    @ResponseBody
    public Map<String, Object> handleBooking(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        String action = (String) requestData.get("action");
        @SuppressWarnings("unchecked")
        Map<String, String> personalInfo = (Map<String, String>) requestData.get("personalInfo");
        @SuppressWarnings("unchecked")
        Map<String, Object> bookingDetails = (Map<String, Object>) requestData.get("bookingDetails");
        String paymentMethod = (String) requestData.get("paymentMethod");
        Double prixTotal = requestData.containsKey("prixTotal") ? 
                           Double.valueOf(requestData.get("prixTotal").toString()) : null;

        logger.info("Appel à /car-booking avec action={}, personalInfo={}, bookingDetails={}, paymentMethod={}, prixTotal={}", 
                    action, personalInfo, bookingDetails, paymentMethod, prixTotal);

        if (!"verify-and-book".equals(action)) {
            response.put("success", false);
            response.put("error", "Action non valide: " + action);
            logger.error("Action non valide: {}", action);
            return response;
        }

        if (personalInfo == null || bookingDetails == null) {
            response.put("success", false);
            response.put("error", "Données personnelles ou de réservation manquantes");
            logger.error("Données manquantes: personalInfo={}, bookingDetails={}", personalInfo, bookingDetails);
            return response;
        }

        String lastName = personalInfo.get("lastName");
        String firstName = personalInfo.get("firstName");
        String email = personalInfo.get("email");
        String tel = personalInfo.get("tel");

        if (lastName == null || firstName == null || email == null || tel == null) {
            response.put("success", false);
            response.put("error", "Informations personnelles incomplètes");
            logger.error("Informations personnelles manquantes: lastName={}, firstName={}, email={}, tel={}", 
                         lastName, firstName, email, tel);
            return response;
        }

        User user = null;
        try {
            user = userService.findByEmailAndNameAndTel(email, lastName, firstName, tel);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Erreur lors de la recherche de l'utilisateur: " + e.getMessage());
            logger.error("Erreur lors de la recherche de l'utilisateur: {}", e.getMessage(), e);
            return response;
        }

        if (user == null) {
            response.put("exists", false);
            logger.info("Utilisateur non trouvé pour email={}, name={}, tel={}", email, firstName + " " + lastName, tel);
            return response;
        }
        response.put("exists", true);
        response.put("userId", user.getId() != null ? user.getId().toString() : null);

        if (prixTotal != null && prixTotal > 0) {
            try {
                Long userId = user.getId() != null ? user.getId() : null;
                if (userId == null) {
                    throw new IllegalArgumentException("ID de l'utilisateur invalide");
                }

                Object carIdObj = bookingDetails.get("carId");
                String carIdStr = carIdObj != null ? carIdObj.toString() : null;
                if (carIdStr == null) {
                    throw new IllegalArgumentException("carId manquant ou invalide");
                }
                Long carId = Long.valueOf(carIdStr);

                String pickupAddress = (String) bookingDetails.get("pickupAddress");
                String returnAddress = (String) bookingDetails.get("returnAddress");
                String carType = (String) bookingDetails.get("carType");
                Object daysObj = bookingDetails.get("days");
                String daysStr = daysObj != null ? daysObj.toString() : null;
                String pickupDateStr = (String) bookingDetails.get("pickupDate");
                String pickupTime = (String) bookingDetails.get("pickupTime");
                String returnDateStr = (String) bookingDetails.get("returnDate");
                String returnTime = (String) bookingDetails.get("returnTime");
                String comments = (String) bookingDetails.get("comments");

                if (carIdStr == null || pickupAddress == null || returnAddress == null || 
                    carType == null || daysStr == null || pickupDateStr == null || returnDateStr == null) {
                    throw new IllegalArgumentException("Champs de réservation manquants");
                }

                int days = Integer.parseInt(daysStr);
                LocalDate pickupDate = LocalDate.parse(pickupDateStr);
                LocalDate returnDate = LocalDate.parse(returnDateStr);

                if (days <= 0 || pickupDate.isAfter(returnDate)) {
                    throw new IllegalArgumentException("Dates ou nombre de jours invalides");
                }

                Car car = carservice.getCarById(carId);
                if (car == null) {
                    throw new RuntimeException("Voiture non trouvée avec ID: " + carId);
                }

                Optional<User> existingUserOpt = userService.findById(userId);
                if (existingUserOpt.isEmpty()) {
                    throw new RuntimeException("Utilisateur non trouvé avec ID: " + userId);
                }
                User existingUser = existingUserOpt.get();
                
                if (existingUser == null || existingUser.getId() == null) {
                    response.put("success", false);
                    response.put("error", "Utilisateur non trouvé ou invalide");
                    return response;
                }

                Double prixJournalier = car.getPrixJournalier();
                if (prixJournalier == null || prixJournalier <= 0) {
                    throw new IllegalArgumentException("Prix journalier de la voiture invalide");
                }
                
                double calculatedPrixTotal = prixJournalier * days;
                if (Math.abs(prixTotal - calculatedPrixTotal) > 0.01) {
                    logger.warn("Prix total envoyé ({}) ne correspond pas au calcul ({} * {} = {})", 
                                prixTotal, prixJournalier, days, calculatedPrixTotal);
                    prixTotal = calculatedPrixTotal;
                }
               

                Réservation reservation = new Réservation();
                reservation.setDateDebut(pickupDate);
                reservation.setDateFin(returnDate);
                reservation.setPrixTotal(prixTotal);
                reservation.setStatut(StatutReservation.EN_ATTENTE);
                reservation.setAdressePriseEnCharge(pickupAddress);
                reservation.setAdresseRestitution(returnAddress);
                reservation.setVoiture(car);
                reservation.setUtilisateur(existingUser);

                reservation = reservationService.saveReservation(reservation);

                Avis avis = new Avis();
                avis.setVoiture(car);
                avis.setAuteur(existingUser);
                avis.setNote(null);
                avis.setCommentaire(comments);
                avis.setDate(LocalDate.now());
                avis.setReservationId(reservation.getId());
                reservationService.saveAvis(avis);

                logger.info("Réservation créée avec succès - reservationId={}, carId={}, prixTotal={}", 
                            reservation.getId(), carId, prixTotal);

                response.put("success", true);
                response.put("reservationId", reservation.getId());
                response.put("carId", carId);
                response.put("prixTotal", prixTotal);
                response.put("paymentMethod", paymentMethod);
                logger.info("Réponse JSON générée: {}", response);
            } catch (Exception e) {
                String errorMessage = e.getMessage() != null ? e.getMessage() : "Erreur inconnue lors de la création de la réservation";
                response.put("success", false);
                response.put("error", errorMessage);
                logger.error("Erreur lors de la réservation: {}", errorMessage, e);
            }
        } else {
            response.put("success", false);
            response.put("error", "Prix total invalide ou non fourni");
            logger.error("Prix total invalide ou absent: {}", prixTotal);
        }

        return response;
    }
    // Ajout pour afficher la page de confirmation
    @GetMapping("/confirmation")
    public String showConfirmation(@RequestParam("reservationId") Long reservationId,
                                   @RequestParam(required = false) Long factureId,
                                   Model model) {
        Optional<Réservation> reservationOpt = reservationService.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            return "Voitures/404";
        }
        model.addAttribute("reservation", reservationOpt.get());

        if (factureId != null) {
            Optional<Facture> factureOpt = factureService.findById(factureId);
            if (factureOpt.isPresent()) {
                model.addAttribute("facture", factureOpt.get());
            }
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("carId", reservationOpt.get().getVoiture().getId());

        }
        // Sinon ne rien mettre ou mettre facture à null

        return "Siteoffeciel/confirmation";
    }

    
    @PostMapping("/update-reservation-status")
    @ResponseBody
    public Map<String, Object> updateReservationStatus(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long reservationId = Long.parseLong(requestData.get("reservationId").toString());
            String status = requestData.get("status").toString();

            Optional<Réservation> reservationOpt = reservationService.findById(reservationId);
            if (reservationOpt.isEmpty()) {
                throw new RuntimeException("Réservation non trouvée");
            }

            Réservation reservation = reservationOpt.get();
            reservation.setStatut(StatutReservation.valueOf(status));
            reservationService.saveReservation(reservation);

            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            logger.error("Erreur lors de la mise à jour du statut de la réservation : {}", e.getMessage());
        }
        return response;
    }
    @PostMapping("/api/voitures/recherche")
    @ResponseBody
    public List<Map<String, Object>> rechercherVoitures(@RequestBody Map<String, String> params) {
        String marque = params.get("marque");
        String modele = params.get("modele");
        String anneeStr = params.get("annee");
        String ville = params.get("ville");

        Integer annee = null;
        if (anneeStr != null && !anneeStr.isEmpty()) {
            try {
                annee = Integer.parseInt(anneeStr);
            } catch (NumberFormatException e) {
                // Optionnel : gérer l'erreur de conversion, ici on ignore et garde annee = null
                annee = null;
            }
        }

        List<Car> resultats = carRepository.rechercherParCritere(marque, modele, annee, ville);

        return resultats.stream().map(car -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", car.getId());
            data.put("marque", car.getMarque());
            data.put("modele", car.getModele());
            data.put("annee", car.getAnnee());
            data.put("image", car.getImagePrincipaleURL());
            data.put("latitude", car.getLatitude());
            data.put("longitude", car.getLongitude());
            return data;
        }).collect(Collectors.toList());
    }
    @GetMapping("/details/{ownerId}")
    public String getVoituresByOwner(@PathVariable Long ownerId, Model model) {
        List<Car> cars = carservice.findByOwnerId(ownerId);
        Optional<User> optionalOwner = userService.findById(ownerId);

        if (optionalOwner.isEmpty()) {
            return "Voitures/404";
        }

        User owner = optionalOwner.get();
        model.addAttribute("cars", cars);
        model.addAttribute("owner", owner);

        return "Voitures/details";
    }
    @GetMapping("/{id}/images")
    @ResponseBody
    public List<String> getCarImages(@PathVariable("id") Long carId) {
        System.out.println("Requête reçue pour carId = " + carId);

        // Récupération de la voiture pour obtenir le propriétaire
        Optional<Car> optionalCar = carRepository.findById(carId);  // Assume @Autowired private CarRepository carRepository;
        if (optionalCar.isEmpty()) {
            System.out.println("Voiture non trouvée pour ID : " + carId);
            return new ArrayList<>();  // Retourne liste vide si voiture non trouvée
        }

        Car car = optionalCar.get();
        Propritaire proprietaire = car.getProprietaire();
        if (proprietaire == null) {
            System.out.println("Propriétaire non associé à la voiture ID : " + carId);
            return new ArrayList<>();  // Liste vide si proprio null
        }

        Long proprietaireId = proprietaire.getId();
        if (proprietaireId == null) {
            System.out.println("ID propriétaire null pour voiture ID : " + carId);
            return new ArrayList<>();
        }

        List<Gallery> images = galleryService.getImagesByCarId(carId);  // Assume @Autowired private GalleryService galleryService;
        System.out.println("Nombre d'images trouvées : " + images.size());

        // Base path qui correspond à ton mapping /images/{ownerId}/{filename}
        String imageBasePath = "/Siteoffeciel/images/" + proprietaireId + "/";

        List<String> urls = images.stream()
                .filter(img -> img.getUrlImage() != null && !img.getUrlImage().isEmpty())  // Filtre null/vides
                .map(img -> {
                    String fullPath = imageBasePath + img.getUrlImage();
                    System.out.println("Image générée : " + fullPath);
                    return fullPath;
                })
                .collect(Collectors.toList());

        System.out.println("Liste finale des URLs : " + urls);

        return urls;
    }

    @GetMapping("/api/agences")
    @ResponseBody
    public List<ProprietaireDTOS> getAgencesTriees(
            @RequestParam(value = "ville", required = false) String ville,
            @RequestParam(value = "sort", defaultValue = "price") String sort) {

        List<Propritaire> agences;
        if (ville == null || ville.isEmpty()) {
            agences = carRepository.findAllProprietaires();
        } else {
            agences = carRepository.findProprietairesParVille(ville);
        }

        // Calcul du prix minimum par agence
        Map<Long, Double> prixMinParAgence = new HashMap<>();
        for (Propritaire agence : agences) {
            List<Car> voitures = agence.getVoitures();
            if (voitures != null && !voitures.isEmpty()) {
                Double minPrix = voitures.stream()
                    .mapToDouble(Car::getPrixJournalier)
                    .min()
                    .orElse(0);
                prixMinParAgence.put(agence.getId(), minPrix);
            } else {
                prixMinParAgence.put(agence.getId(), null);
            }
        }

        // Tri selon paramètre
        switch (sort) {
            case "price":
                agences.sort(Comparator.comparing(a -> prixMinParAgence.getOrDefault(a.getId(), Double.MAX_VALUE)));
                break;
            case "price-desc":
                agences.sort(Comparator.comparing((Propritaire a) -> prixMinParAgence.getOrDefault(a.getId(), 0.0)).reversed());
                break;
            case "name":
                agences.sort(Comparator.comparing(Propritaire::getRaisonSociale));
                break;
            case "rating":
                // Ici, tu peux faire un tri personnalisé par note moyenne si tu as la data
                // Sinon trier par ordre alphabétique par défaut
                agences.sort(Comparator.comparing(Propritaire::getRaisonSociale));
                break;
            default:
                break;
        }

        List<ProprietaireDTOS> dtos = agences.stream()
        	    .map((Propritaire a) -> new ProprietaireDTOS(a, prixMinParAgence.get(a.getId())))
        	    .collect(Collectors.toList());
		return dtos;

    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  

}