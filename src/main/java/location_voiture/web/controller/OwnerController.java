package location_voiture.web.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.data.domain.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import location_voiture.persistence.dto.AvisDTO;
import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.dto.ClientDTO;
import location_voiture.persistence.dto.DisponibiliteDTO;
import location_voiture.persistence.dto.EntretienUpdateDTO;
import location_voiture.persistence.dto.LitigeDTO;
import location_voiture.persistence.dto.MessageDTO;
import location_voiture.persistence.dto.PaiementDTO;
import location_voiture.persistence.dto.PanneDTO;
import location_voiture.persistence.dto.ReservationCreateDTO;
import location_voiture.persistence.dto.ReservationDTO;
import location_voiture.persistence.dto.ResponseDTO;
import location_voiture.persistence.dto.RevenuParVoitureDTO;
import location_voiture.persistence.dto.UserDTO;
import location_voiture.persistence.dto.UserProfileDTO;
import location_voiture.persistence.model.Alert;
import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Disponibilite;
import location_voiture.persistence.model.Entretien;
import location_voiture.persistence.model.EntretienType;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Gallery;
import location_voiture.persistence.model.Litige;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Message;
import location_voiture.persistence.model.NoteRequest;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Panne;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutDisponibilite;
import location_voiture.persistence.model.StatutEntretien;
import location_voiture.persistence.model.StatutLitige;
import location_voiture.persistence.model.StatutReservation;
import location_voiture.persistence.model.StatutTechnique;
import location_voiture.persistence.model.TypeAlert;
import location_voiture.persistence.model.TypeReservation;
import location_voiture.repository.AlertRepository;
import location_voiture.repository.AvisRepository;
import location_voiture.repository.CarRepository;
import location_voiture.repository.DisponibiliteRepository;
import location_voiture.repository.EntretienRepository;
import location_voiture.repository.LitigeRepository;
import location_voiture.repository.LocataireRepository;
import location_voiture.repository.MessageRepository;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.PanneRepository;
import location_voiture.repository.ProprietaireRepository;
import location_voiture.repository.ReservationRepository;
import location_voiture.service.AlertService;
import location_voiture.service.AvisService;
import location_voiture.service.CarService;
import location_voiture.service.DisponibiliteService;
import location_voiture.service.EmailService;
import location_voiture.service.EntretienService;
import location_voiture.service.FactureService;
import location_voiture.service.LitigeService;
import location_voiture.service.MessageService;
import location_voiture.service.PaiementService;
import location_voiture.service.PanneService;
import location_voiture.service.ProprietaireFileService;
import location_voiture.service.ReservationService;
import location_voiture.web.controller.PageController.ReservationResponse;
import ma.abisoft.persistence.dao.RoleRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.UserService;
import org.springframework.data.domain.Pageable;


@Controller
@RequestMapping("/Owner")

public class OwnerController {
	@Autowired
	private PaiementService paiementService;
    @Autowired private MessageRepository messageRepository;
    @Autowired private UserRepository utilisateurRepository;
    @Autowired private LitigeRepository litigeRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private AvisRepository avisRepository;
    private final ReservationService reservationService;
    @Autowired
    private DisponibiliteRepository disponibiliteRepository;
  
    @Autowired
    private LocataireRepository locataireRepository; 
    @Autowired
    private CarService carService;

    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);
    @Autowired
    private ProprietaireFileService fileService;
@Autowired
private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private LitigeService litigeService;
    @Autowired
    private PanneService panneService;
    @Autowired
    private EntretienService entretienService;
    @Autowired
    private ProprietaireRepository proprietaireRepository;
    @Autowired
    private FactureService factureService;
    @Autowired
    private EntretienRepository entretienRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PaiementRepository paiementRepository; 
    @Autowired
    private PanneRepository panneRepository; 
	    @Autowired
	    private CarRepository carRepository;
	   


	    @Autowired
	    private AlertRepository alertRepository;
	    
	    @Autowired
	// Corrigé : pas de final ici
	    private CarService carservice; // Corrigé : pas de final ici
	    @Autowired
	    private AvisService avisService; 
	    @Autowired
	    private UserService userService;
	    @Autowired
	    private MessageService messageService;
	    
	    @Autowired
	    private DisponibiliteService disponibiliteService;
	    
	  

	   /* @PostMapping("/uploadImage")
	    public String uploadProprietaireImage(@RequestParam("image") MultipartFile image,
	                                          @RequestParam("proprietaireId") Long proprietaireId,
	                                          RedirectAttributes redirectAttributes) {
	        try {
	            String relativePath = fileService.saveImage(image, proprietaireId);
	            // ici : save dans la base si besoin
	            redirectAttributes.addFlashAttribute("success", "Image uploadée !");
	        } catch (Exception e) {
	            e.printStackTrace();
	            redirectAttributes.addFlashAttribute("error", "Erreur upload image : " + e.getMessage());
	        }
	        return "redirect:/Siteoffeciel/index";
	    }

	    @PostMapping("/uploadFacture")
	    public String uploadFacture(@RequestParam("facture") MultipartFile facture,
	                                @RequestParam("proprietaireId") Long proprietaireId,
	                                @RequestParam("annee") String annee,
	                                @RequestParam("mois") String mois,
	                                RedirectAttributes redirectAttributes) {
	        try {
	            String relativePath = fileService.saveFacture(facture, proprietaireId, annee, mois);
	            // ici : save dans la base si besoin
	            redirectAttributes.addFlashAttribute("success", "Fascture uploadée !");
	        } catch (Exception e) {
	            e.printStackTrace();
	            redirectAttributes.addFlashAttribute("error", "Erreur upload facture : " + e.getMessage());
	        }
	        return "redirect:/Siteoffeciel/index";
	    }
	    
	    
	    */
	    
	    
	    // Pages principales
	    @GetMapping("/message")
		public String messagerecu(Model model,Authentication authentication) {
			if (authentication != null && authentication.isAuthenticated()) {
		        Object principal = authentication.getPrincipal();
		        String email = null;

		        if (principal instanceof User) {
		            email = ((User) principal).getEmail();
		        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
		            email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
		        } else if (principal instanceof String) {
		            email = (String) principal;
		        }

		        System.out.println("EMAIL = " + email);

		        if (email != null) {
		            User user = userService.findByEmail(email);
		            System.out.println("USER = " + user);
		            model.addAttribute("currentUser", user);
		        } else {
		            model.addAttribute("currentUser", null);
		        }
		    } else {
		        model.addAttribute("currentUser", null);
		    }
		    return "Owner/messagerecu";
		}
	    @GetMapping("/dashbord")
	    public String dashboard(Model model,Authentication authentication) {
	    	  if (authentication != null && authentication.isAuthenticated()) {
	    	        Object principal = authentication.getPrincipal();
	    	        String email = null;

	    	        if (principal instanceof User) {
	    	            email = ((User) principal).getEmail();
	    	        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
	    	            email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
	    	        } else if (principal instanceof String) {
	    	            email = (String) principal;
	    	        }

	    	        System.out.println("EMAIL = " + email);

	    	        if (email != null) {
	    	            User user = userService.findByEmail(email);
	    	            System.out.println("USER = " + user);
	    	            model.addAttribute("currentUser", user);
	    	        } else {
	    	            model.addAttribute("currentUser", null);
	    	        }
	    	    } else {
	    	        model.addAttribute("currentUser", null);
	    	    }

	        model.addAttribute("messagesCount", messageRepository.count());
	        model.addAttribute("clientsLoueCount", utilisateurRepository.countClientsAyantLoue());
	        model.addAttribute("litigesCount", litigeRepository.count());
	        model.addAttribute("litigesEnAttente", litigeRepository.countByStatut(StatutLitige.EN_ATTENTE));
	        model.addAttribute("reservationsCount", reservationRepository.count());
	        model.addAttribute("avisCount", avisRepository.count());

	        Double moyenne = avisRepository.moyenneAvis();
	        model.addAttribute("moyenneAvis", moyenne != null ? String.format("%.1f", moyenne) : "N/A");
	        
	        List<Car> vehicules = carservice.getAllVehicules(); // Récupère tous les véhicules de la base de données
	        model.addAttribute("vehicules", vehicules);
	        
	        List<Avis> avis = avisService.findAll(); // Récupère tous les avis
	        model.addAttribute("avis", avis); 
	        
		
	        List<ReservationDTO> reservations = reservationService.getAllReservations();
	        model.addAttribute("reservations", reservations);
	        return "Owner/dashbord";
	    }
	    @Autowired
	    public OwnerController(ReservationService reservationService) {
	        this.reservationService = reservationService;
			this.emailService = new EmailService();
	    }
	   
	    
	    

	    
	    @GetMapping("/reservations-mensuelles")
	    public Map<String, Long> getReservationsParMois() {
	        Map<String, Long> data = new LinkedHashMap<>();
	        LocalDate currentYear = LocalDate.now().withDayOfYear(1); // Commencer au début de l'année en cours

	        for (int i = 1; i <= 12; i++) {
	            // Calculer la date de début et la date de fin pour le mois
	            LocalDate startOfMonth = currentYear.withMonth(i).withDayOfMonth(1);
	            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

	            // Format du mois en français
	            String mois = YearMonth.of(LocalDate.now().getYear(), i).getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH);

	            // Appeler countByMonth pour chaque mois
	            Long count = reservationRepository.countByMonth(startOfMonth, endOfMonth);
	            data.put(mois, count != null ? count : 0); // Assurez-vous que null est remplacé par 0
	        }
	        return data;
	    }


	    @GetMapping("/avis-performance")
	    public Map<String, Double> getAvisPerformance() {
	        Map<String, Double> data = new LinkedHashMap<>();
	        for (int i = 1; i <= 12; i++) {
	            String mois = YearMonth.of(LocalDate.now().getYear(), i).getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH);
	            Double moyenne = avisRepository.averageRatingByMonth(i);
	            data.put(mois, moyenne != null ? moyenne : 0.0);
	        }
	        return data;
	    }
	    
	    @GetMapping("/reservations-par-jour")
	    public Map<String, Long> getReservationsParJour() {
	        Map<String, Long> data = new LinkedHashMap<>();
	        LocalDate today = LocalDate.now();

	        // Obtenir le lundi de la semaine actuelle
	        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);

	        for (int i = 0; i < 7; i++) {
	            LocalDate date = startOfWeek.plusDays(i);
	            String jour = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH);
	            Long count = reservationRepository.countByDay(date);
	            data.put(jour, count != null ? count : 0);
	        }

	        return data;
	    }

	    
	    
	    
	    
	    
	    
	    @GetMapping("/clientespace")
	    public String clientespace(Model model, Authentication authentication) {
	        if (authentication != null && authentication.isAuthenticated()) {
	            String email = null;

	            Object principal = authentication.getPrincipal();
	            if (principal instanceof User) {
	                email = ((User) principal).getEmail();
	            } else if (principal instanceof org.springframework.security.core.userdetails.User) {
	                email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
	            } else if (principal instanceof String) {
	                email = (String) principal;
	            }

	            if (email != null) {
	                User proprietaire = userService.findByEmail(email);
	                model.addAttribute("currentUser", proprietaire);

	                // ✅ 1. Récupérer les voitures appartenant au propriétaire
	                List<Car> voitures = carRepository.findByProprietaireId(proprietaire.getId());

	                // ✅ 2. Récupérer les réservations associées à ces voitures
	                List<Reservation> reservations = reservationRepository.findByVoitureIn(voitures);

	                // ✅ 3. Extraire les utilisateurs clients
	                Set<User> clients = reservations.stream()
	                    .map(Reservation::getUtilisateur)
	                    .filter(user -> user.getRoles().stream()
	                        .anyMatch(role -> role.getName().equalsIgnoreCase("CLIENT")))
	                    .collect(Collectors.toSet());

	                // ✅ 4. Associer chaque client à sa liste de réservations
	                Map<Long, List<Reservation>> reservationsMap = new HashMap<>();
	                for (User client : clients) {
	                    List<Reservation> resClient = reservations.stream()
	                        .filter(r -> r.getUtilisateur().getId().equals(client.getId()))
	                        .collect(Collectors.toList());
	                    reservationsMap.put(client.getId(), resClient);
	                }

	                model.addAttribute("utilisateurs", clients);
	                model.addAttribute("reservationsMap", reservationsMap);
	            } else {
	                model.addAttribute("currentUser", null);
	            }
	        } else {
	            model.addAttribute("currentUser", null);
	        }

	        return "Owner/clientespace";
	    }
	    @GetMapping("/list")
	    @ResponseBody
	    public List<Map<String, Object>> getAllClients(Principal principal) {
	        
	        try {
	            // Vérification du principal
	            if (principal == null) {
	                System.out.println("Erreur : principal est null");
	                return Collections.emptyList();
	            }
	            
	            if (!(principal instanceof Authentication)) {
	                System.out.println("Erreur : le principal n'est pas une instance d'Authentication.");
	                return Collections.emptyList();
	            }

	            // Récupération de l'utilisateur connecté
	            Object principalObj = ((Authentication) principal).getPrincipal();
	            if (!(principalObj instanceof User)) {
	                System.out.println("Erreur : principal n'est pas une instance de User");
	                return Collections.emptyList();
	            }

	            // Récupération de l'utilisateur connecté
	            User connectedUser = (User) principalObj;
	            System.out.println("Utilisateur connecté: " + connectedUser.getEmail());

	            // Récupération du propriétaire associé à l'utilisateur
	            Propritaire proprietaire = proprietaireRepository.findByUserId(connectedUser.getId());
	            if (proprietaire == null) {
	                System.out.println("Propriétaire non trouvé pour l'utilisateur ID: " + connectedUser.getId());
	                return Collections.emptyList();
	            }

	            System.out.println("🔍 ID du proprietaire: " + proprietaire.getId());

	            // Récupérer toutes les réservations des voitures du propriétaire
	            System.out.println("🔍 Appel de findReservationsByOwner avec ID...");
	            List<Reservation> reservations = reservationService.findReservationsByOwner(proprietaire.getId());
	            System.out.println("✅ Reservations trouvées: " + (reservations != null ? reservations.size() : "null"));
	            
	            // Debug détaillé des réservations
	            if (reservations != null && !reservations.isEmpty()) {
	                System.out.println("📋 Détail des réservations trouvées:");
	                for (Reservation res : reservations) {
	                    System.out.println("   - Reservation ID: " + res.getId() + 
	                                     ", Client: " + (res.getClient() != null ? res.getClient().getEmail() : "NULL") +
	                                     ", Utilisateur: " + (res.getUtilisateur() != null ? res.getUtilisateur().getEmail() : "NULL") +
	                                     ", Locataire: " + (res.getLocataire() != null ? "présent" : "NULL") +
	                                     ", Voiture: " + (res.getVoiture() != null ? res.getVoiture().getMarque() + " " + res.getVoiture().getModele() : "null") +
	                                     ", Statut: " + res.getStatut());
	                }
	            }

	            // Vérifier si des réservations existent
	            if (reservations == null || reservations.isEmpty()) {
	                System.out.println("Aucune réservation trouvée pour le propriétaire");
	                return Collections.emptyList();
	            }

	            // 🔥 CORRECTION : Récupérer les clients de différentes manières
	            Map<Long, User> clientsMap = new HashMap<>();
	            for (Reservation res : reservations) {
	                if (res != null) {
	                    User client = null;
	                    
	                    // Essayer différentes méthodes pour récupérer le client
	                    if (res.getClient() != null) {
	                        client = res.getClient();
	                        System.out.println("👤 Client trouvé via getClient() pour réservation " + res.getId());
	                    } else if (res.getUtilisateur() != null) {
	                        client = res.getUtilisateur();
	                        System.out.println("👤 Client trouvé via getUtilisateur() pour réservation " + res.getId());
	                    } else if (res.getLocataire() != null && res.getLocataire().getUser() != null) {
	                        client = res.getLocataire().getUser();
	                        System.out.println("👤 Client trouvé via getLocataire().getUser() pour réservation " + res.getId());
	                    }
	                    
	                    if (client != null) {
	                        clientsMap.put(client.getId(), client);
	                        System.out.println("✅ Client ajouté: " + client.getEmail() + " (ID: " + client.getId() + ")");
	                    } else {
	                        System.out.println("❌ Aucun client trouvé pour la réservation ID: " + res.getId());
	                    }
	                }
	            }

	            // Vérifier si des clients ont été trouvés
	            if (clientsMap.isEmpty()) {
	                System.out.println("Aucun client trouvé dans les réservations");
	                return Collections.emptyList();
	            }

	            System.out.println("👥 Clients distincts trouvés: " + clientsMap.size());

	            // Préparer la réponse
	            List<Map<String, Object>> response = new ArrayList<>();
	            for (User client : clientsMap.values()) {
	                if (client == null) continue;
	                
	                Map<String, Object> data = new HashMap<>();

	                // Infos utilisateur avec vérifications null
	                data.put("id", client.getId());
	                data.put("firstName", client.getFirstName() != null ? client.getFirstName() : "");
	                data.put("lastName", client.getLastName() != null ? client.getLastName() : "");
	                data.put("email", client.getEmail() != null ? client.getEmail() : "");
	                data.put("tel", client.getTel() != null ? client.getTel() : "");
	                data.put("enabled", client.isEnabled() ? 1 : 0);

	                // Infos locataire avec vérifications null
	                String numeroPermis = null;
	                String adresse = null;
	                if (client.getLocataire() != null) {
	                    numeroPermis = client.getLocataire().getNumeroPermis();
	                    adresse = client.getLocataire().getAdresse();
	                }
	                data.put("numeroPermis", numeroPermis);
	                data.put("adresse", adresse);

	                // Dernière réservation
	                Reservation lastRes = null;
	                try {
	                    // Essayer avec différentes méthodes
	                    lastRes = reservationService.findLastReservationByClientAndOwner(client.getId(), proprietaire.getId());
	                    if (lastRes == null) {
	                        // Essayer une autre méthode si disponible
	                        lastRes = reservationRepository.findTopByClientAndOwner(client, proprietaire);
	                    }
	                } catch (Exception e) {
	                    System.out.println("Erreur lors de la récupération de la dernière réservation: " + e.getMessage());
	                }
	                
	                Map<String, Object> voitureData = null;
	                if (lastRes != null && lastRes.getVoiture() != null) {
	                    Car car = lastRes.getVoiture();
	                    voitureData = new HashMap<>();
	                    voitureData.put("marque", car.getMarque() != null ? car.getMarque() : "");
	                    voitureData.put("modele", car.getModele() != null ? car.getModele() : "");
	                    voitureData.put("immatriculation", car.getImmatriculation() != null ? car.getImmatriculation() : "");
	                }
	                data.put("voitureReservee", voitureData);

	                // Note moyenne des avis
	                List<Avis> avisList = null;
	                try {
	                    avisList = avisService.findAvisByClient(client.getId());
	                } catch (Exception e) {
	                    System.out.println("Erreur lors de la récupération des avis: " + e.getMessage());
	                }
	                
	                Double moyenne = null;
	                if (avisList != null && !avisList.isEmpty()) {
	                    moyenne = avisList.stream()
	                        .filter(avis -> avis != null && avis.getNote() != null)
	                        .mapToDouble(Avis::getNote)
	                        .average()
	                        .orElse(Double.NaN);
	                    if (Double.isNaN(moyenne)) {
	                        moyenne = null;
	                    }
	                }
	                data.put("note", moyenne);

	                response.add(data);
	            }

	            System.out.println("✅ Réponse préparée avec " + response.size() + " clients");
	            return response;
	            
	        } catch (Exception e) {
	            System.out.println("❌ Erreur générale dans getAllClients: " + e.getMessage());
	            e.printStackTrace();
	            return Collections.emptyList();
	        }
	    }
	    @PutMapping("/voitures/modifier/{id}")
	    public ResponseEntity<Map<String, Object>> modifierVoiture(
	            @PathVariable("id") Long id,
	            @RequestParam("marque") String marque,
	            @RequestParam("modele") String modele,
	            @RequestParam("immatriculation") String immatriculation,
	            @RequestParam("annee") Integer annee,
	            @RequestParam("prixJournalier") Double prixJournalier,
	            @RequestParam(value = "description", required = false) String description,
	            //@RequestParam("disponible") String disponible,
	            @RequestParam(value = "imagePrincipale", required = false) MultipartFile imagePrincipale
	    ) {
	        try {
	            Car car = carService.findById(id);
	            if (car == null) {
	                return ResponseEntity.status(404).body(Map.of("message", "Voiture introuvable"));
	            }

	            // Mise à jour des champs simples
	            car.setMarque(marque);
	            car.setModele(modele);
	            car.setImmatriculation(immatriculation);
	            car.setAnnee(annee);
	            car.setPrixJournalier(prixJournalier);
	            car.setDescription(description);
	           // car.setDisponible(disponible);

	            // Gestion de l'image principale
	            // Gestion de l'image principale

	            if (imagePrincipale != null && !imagePrincipale.isEmpty()) {
	                try {
	                    // Récupère l'ID du propriétaire (assume que car.proprietaire est chargé ; sinon, ajoute un fetch)
	                    if (car.getProprietaire() == null) {
	                        throw new IllegalArgumentException("La voiture doit être associée à un propriétaire pour uploader une image");
	                    }
	                    Long proprietaireId = car.getProprietaire().getId();
	                    
	                    // Utilise le service pour sauvegarder l'image (crée le dossier si besoin)
	                    String filename = fileService.saveImage(imagePrincipale, proprietaireId);
	                    
	                    // Stocke SEULEMENT le nom du fichier en BD (pas le chemin complet)
	                    car.setImagePrincipaleURL(filename);
	                    
	                } catch (IOException e) {
	                    // Log l'erreur et renvoie une réponse spécifique
	                    logger.error("Erreur lors de l'upload de l'image principale pour la voiture ID {}", car.getId(), e);
	                    return ResponseEntity.status(400).body(Map.of("message", "Erreur upload image", "error", e.getMessage()));
	                }
	            }

	            // Sauvegarde l'entité mise à jour
	            carService.save(car);

	            return ResponseEntity.ok(Map.of("message", "Voiture modifiée avec succès", "car", car));

	        } catch (Exception e) {
	            return ResponseEntity.status(500).body(Map.of("message", "Erreur serveur", "error", e.getMessage()));
	        }
	    }

	    @PutMapping("/voitures/{id}/masquer")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> masquerVoiture(@PathVariable Long id) {
	        try {
	            Car car = carService.findById(id);
	            if (car == null) {
	                return ResponseEntity.status(404).body(Map.of("message", "Voiture introuvable"));
	            }

	            // Mettre supprimer = 1
	            car.setSupprimer(1);
	            carService.save(car);

	            return ResponseEntity.ok(Map.of("message", "Voiture masquée avec succès"));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(Map.of("message", "Erreur serveur"));
	        }
	    }


	    
	    @GetMapping("/voituremasq")
	    @ResponseBody
	    public List<Car> getVoituresActives() {
	        return carService.findAll().stream()
	           // .filter(car -> !car.getDisponible().equals("Indisponible")) // seulement celles non masquées
	            .collect(Collectors.toList());
	    }

	    
	    @GetMapping("/litige")
	    public String litige(Model model,Authentication authentication) {
			if (authentication != null && authentication.isAuthenticated()) {
		        Object principal = authentication.getPrincipal();
		        String email = null;

		        if (principal instanceof User) {
		            email = ((User) principal).getEmail();
		        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
		            email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
		        } else if (principal instanceof String) {
		            email = (String) principal;
		        }

		        System.out.println("EMAIL = " + email);

		        if (email != null) {
		            User user = userService.findByEmail(email);
		            System.out.println("USER = " + user);
		            model.addAttribute("currentUser", user);
		        } else {
		            model.addAttribute("currentUser", null);
		        }
		    } else {
		        model.addAttribute("currentUser", null);
		    }
			
	        return "Owner/litige";
	    }
	   
	    @GetMapping("/reservation")
	    public String suivereservation(Model model, Authentication authentication) throws JsonProcessingException {
	        System.out.println("🔍 [DEBUG] Accès à /reservation");

	        if (authentication == null || !authentication.isAuthenticated()) {
	            System.out.println("❌ Utilisateur non authentifié");
	            return "redirect:/login";
	        }

	        // Extraction de l'email utilisateur
	        String email = null;
	        Object principal = authentication.getPrincipal();

	        if (principal instanceof User) {
	            email = ((User) principal).getEmail();
	        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
	            email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
	        } else if (principal instanceof String) {
	            email = (String) principal;
	        }

	        System.out.println("✅ Email extrait : " + email);
	        
	        // Récupération de l'utilisateur
	        User user = userService.findByEmail(email);
	        if (user == null) {
	            System.out.println("⚠️ Aucun utilisateur trouvé avec l'email : " + email);
	            return "redirect:/login";
	        }

	        model.addAttribute("currentUser", user);
	        
	        // 🔥 CORRECTION : Récupérer le Propritaire, pas utiliser user.getId()
	        Propritaire proprietaire = proprietaireRepository.findByUserId(user.getId());
	        if (proprietaire == null) {
	            System.out.println("❌ Aucun propriétaire trouvé pour l'utilisateur ID: " + user.getId());
	            model.addAttribute("reservations", new ArrayList<>());
	            model.addAttribute("reservationsJson", "[]");
	            return "Owner/suivereservation";
	        }

	        System.out.println("🏠 Propriétaire trouvé - ID: " + proprietaire.getId());

	        // Récupération des réservations avec l'ID du propriétaire
	        List<ReservationDTO> reservationDTOs = reservationService.findByOwner(); // Vérifiez si cette méthode utilise le bon ID
	        String jsonReservations = new ObjectMapper().writeValueAsString(reservationDTOs);
	        model.addAttribute("reservationsJson", jsonReservations);

	        // 🔥 Utiliser l'ID du propriétaire, pas de l'utilisateur
	        List<Reservation> reservations = reservationService.getReservationsByProprietaire(proprietaire.getId());
	        if (reservations == null) {
	            System.out.println("⚠️ Liste de réservations est null !");
	            reservations = new ArrayList<>();
	        }
	        System.out.println("📦 Nombre de réservations trouvées : " + reservations.size());
	        
	        // Debug des réservations
	        if (!reservations.isEmpty()) {
	            System.out.println("🔍 Détail des réservations:");
	            for (Reservation res : reservations) {
	                System.out.println("   - Reservation ID: " + res.getId() + 
	                                 ", Client: " + (res.getUtilisateur() != null ? res.getUtilisateur().getEmail() : "null") +
	                                 ", Voiture: " + (res.getVoiture() != null ? res.getVoiture().getMarque() + " " + res.getVoiture().getModele() : "null") +
	                                 ", Statut: " + res.getStatut());
	            }
	        }
	        
	        model.addAttribute("reservations", reservations);

	        return "Owner/suivereservation";
	    }

	    
	    @GetMapping("/reservations/all")
	    @ResponseBody
	    public List<ReservationDTO> getAllReservation() {
	        // Récupération des DTOs existants
	        List<ReservationDTO> list = reservationService.getAllReservations();

	        // Debug pour vérifier le contenu
	        list.forEach(r -> {
	            System.out.println("ID: " + r.getId() +
	                               " | Voiture: " + r.getCarFullName() +
	                               " | Début: " + r.getDateDebut() +
	                               " | Fin: " + r.getDateFin());
	        });

	        return list;
	    }
	    @GetMapping("/pannes/all")
	    @ResponseBody
	    public List<PanneDTO> getAllPannes() {
	        return panneService.getAllPannes();
	    }

	   
	    
	    @GetMapping("/paiement")
	    public String paiement(Model model, Authentication authentication) {
	        System.out.println("🔍 [DEBUG] Accès à /paiement");
	        
	        if (authentication != null && authentication.isAuthenticated()) {
	            Object principal = authentication.getPrincipal();
	            String email = null;

	            if (principal instanceof User) {
	                email = ((User) principal).getEmail();
	            } else if (principal instanceof org.springframework.security.core.userdetails.User) {
	                email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
	            } else if (principal instanceof String) {
	                email = (String) principal;
	            }

	            System.out.println("✅ Email utilisateur: " + email);

	            if (email != null) {
	                User owner = userService.findByEmail(email);
	                model.addAttribute("currentUser", owner);

	                // 🔥 CORRECTION : Récupérer le propriétaire, pas l'utilisateur
	                Propritaire proprietaire = proprietaireRepository.findByUserId(owner.getId());
	                if (proprietaire == null) {
	                    System.out.println("❌ Aucun propriétaire trouvé pour l'utilisateur ID: " + owner.getId());
	                    model.addAttribute("paiements", Collections.emptyList());
	                    model.addAttribute("revenus", Collections.emptyList());
	                    return "Owner/Paiement";
	                }

	                System.out.println("🏠 Propriétaire trouvé - ID: " + proprietaire.getId());

	                // Filtrer les paiements pour ce propriétaire
	                List<Paiement> paiements = paiementService.getPaiementsByProprietaireId(proprietaire.getId());
	                System.out.println("💰 Paiements trouvés: " + paiements.size());
	                
	                // 🔥 CORRECTION : Convertir en DTOs pour éviter la récursion infinie
	                List<PaiementDTO> paiementDTOs = convertToDTOs(paiements);
	                
	                // Debug des paiements
	                if (!paiementDTOs.isEmpty()) {
	                    System.out.println("📋 Détail des paiements (DTO):");
	                    for (PaiementDTO p : paiementDTOs) {
	                        System.out.println("   - Paiement ID: " + p.getId() + 
	                                         ", Montant: " + p.getMontant() + 
	                                         ", Statut: " + p.getStatut() +
	                                         ", Voiture: " + p.getVoitureMarque() +
	                                         ", Client: " + p.getClientNom());
	                    }
	                }

	                // 🔥 CORRECTION : Utiliser les DTOs au lieu des entités
	                model.addAttribute("paiements", paiementDTOs);

	                // Revenus par voiture
	                List<RevenuParVoitureDTO> revenus = paiementService.getRevenusParVoiture(proprietaire.getId());
	                model.addAttribute("revenus", revenus);

	                return "Owner/Paiement";
	            }
	        }

	        System.out.println("❌ Utilisateur non authentifié");
	        model.addAttribute("currentUser", null);
	        model.addAttribute("paiements", Collections.emptyList());
	        model.addAttribute("revenus", Collections.emptyList());
	        return "Owner/Paiement";
	    }

	    // 🔥 NOUVELLE MÉTHODE : Conversion des entités en DTOs
	    private List<PaiementDTO> convertToDTOs(List<Paiement> paiements) {
	        return paiements.stream()
	            .map(this::convertToDTO)
	            .collect(Collectors.toList());
	    }

	    // 🔥 NOUVELLE MÉTHODE : Conversion d'une entité en DTO
	 // 🔥 CORRECTION : Changer le format de date
	    private PaiementDTO convertToDTO(Paiement paiement) {
	        PaiementDTO dto = new PaiementDTO();
	        
	        // Informations de base
	        dto.setId(paiement.getId());
	        dto.setMontant(paiement.getMontant());
	        dto.setStatut(paiement.getStatut().name());
	        dto.setMethode(paiement.getMethodePaiement() != null ? paiement.getMethodePaiement().toString() : "N/A");        
	        
	        // Format de date
	        if (paiement.getDatePaiement() != null) {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	            dto.setDate(paiement.getDatePaiement().format(formatter));
	        } else {
	            dto.setDate("Date non définie");
	        }
	        
	        if (paiement.getReservation() != null) {
	            Reservation reservation = paiement.getReservation();
	            
	            // Créer le ReservationDTO spécifique
	            ReservationDTO reservationDTO = new ReservationDTO();
	            reservationDTO.setId(reservation.getId());
	            
	            // Dates de réservation
	            if (reservation.getDateDebut() != null) {
	                reservationDTO.setDateDebut(reservation.getDateDebut().toString());
	            }
	            if (reservation.getDateFin() != null) {
	                reservationDTO.setDateFin(reservation.getDateFin().toString());
	            }
	            
	            reservationDTO.setStatutReservation(reservation.getStatut() != null ? reservation.getStatut().name() : "Inconnu");
	            reservationDTO.setPrix(reservation.getPrixTotal());
	            reservationDTO.setAdressePriseEnCharge(reservation.getAdressePriseEnCharge());
	            reservationDTO.setAdresseRestitution(reservation.getAdresseRestitution());
	            
	         // 🔥 CORRECTION : Récupération du nom du client
	          
	          
	            // Informations client dans ReservationDTO
	            if (reservation.getLocataire() != null && reservation.getLocataire().getUser() != null) {
	                User user = reservation.getLocataire().getUser();
	                reservationDTO.setNomClient(user.getFirstName() + " " + user.getLastName());
	                reservationDTO.setEmailClient(user.getEmail());
	                reservationDTO.setClientFirstName(user.getFirstName());
	                reservationDTO.setClientLastName(user.getLastName());
	                reservationDTO.setClientEmail(user.getEmail());
	            }
	            
	            // Informations voiture dans ReservationDTO
	            if (reservation.getVoiture() != null) {
	                Car voiture = reservation.getVoiture();
	                reservationDTO.setMarque(voiture.getMarque());
	                reservationDTO.setModele(voiture.getModele());
	                reservationDTO.setImmatriculation(voiture.getImmatriculation());
	                reservationDTO.setCarModele(voiture.getMarque() + " " + voiture.getModele());
	                reservationDTO.setCarImmatriculation(voiture.getImmatriculation());
	                
	                // Pour PaiementDTO
	                dto.setVoitureMarque(voiture.getMarque() + " " + voiture.getModele());
	            } else {
	                dto.setVoitureMarque("Voiture inconnue");
	                reservationDTO.setMarque("Marque inconnue");
	                reservationDTO.setModele("Modèle inconnu");
	            }
	            
	            dto.setReservation(reservationDTO);
	        } else {
	            dto.setClientNom("Client inconnu");
	            dto.setVoitureMarque("Voiture inconnue");
	        }
	        
	        return dto;
	    }
	    
	    
	    @GetMapping("/factures/generer/{factureId}")
	    public void downloadFacture(@PathVariable Long factureId, 
	                               HttpServletResponse response) {
	        try {
	            Optional<Facture> factureOpt = factureService.findById(factureId);
	            if (factureOpt.isPresent()) {
	                Facture facture = factureOpt.get();
	                if (facture.getFacturePdf() != null) {
	                    response.setContentType("application/pdf");
	                    response.setHeader("Content-Disposition", 
	                        "attachment; filename=\"facture_" + factureId + ".pdf\"");
	                    response.getOutputStream().write(facture.getFacturePdf());
	                    response.getOutputStream().flush();
	                } else {
	                    response.sendError(HttpStatus.NOT_FOUND.value(), "PDF de la facture non trouvé");
	                }
	            } else {
	                response.sendError(HttpStatus.NOT_FOUND.value(), "Facture non trouvée");
	            }
	        } catch (Exception e) {
	            // Gérer l'erreur
	            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        }
	    }
	    @GetMapping("/avis")
	    public String avis(Model model,Authentication authentication) {
			if (authentication != null && authentication.isAuthenticated()) {
		        Object principal = authentication.getPrincipal();
		        String email = null;

		        if (principal instanceof User) {
		            email = ((User) principal).getEmail();
		        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
		            email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
		        } else if (principal instanceof String) {
		            email = (String) principal;
		        }

		        System.out.println("EMAIL = " + email);

		        if (email != null) {
		            User user = userService.findByEmail(email);
		            System.out.println("USER = " + user);
		            model.addAttribute("currentUser", user);
		        } else {
		            model.addAttribute("currentUser", null);
		        }
		    } else {
		        model.addAttribute("currentUser", null);
		    }
	        return "Owner/avisclient";
	    }
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

	        // Vérification manuelle pour CarId null (validation @Valid ne le catch pas toujours pour @RequestPart)
	        if (reservationCreateDTO.getCarId() == null) {
	            System.out.println("Erreur : CarId est requis et ne peut pas être null.");
	            return ResponseEntity.badRequest().body("Erreur : L'ID de la voiture (CarId) est requis.");
	        }

	        try {
	            if (virementFile != null && !virementFile.isEmpty()) {
	                reservationCreateDTO.setVirementFileName(virementFile.getOriginalFilename());
	                System.out.println("Nom fichier virement stocké dans DTO : " + reservationCreateDTO.getVirementFileName());
	                // TODO: sauvegarder le fichier sur disque ou cloud si besoin
	            }
	            reservationCreateDTO.setTypeReservation(TypeReservation.PRESENTIELLE);
	            Reservation reservation = reservationService.createReservation(reservationCreateDTO);
	            System.out.println("Reservation créée avec ID : " + reservation.getId());

	            Facture facture = factureService.creerFactureDepuisReservation(reservation);
	            System.out.println("Facture créée avec ID : " + facture.getId());
	            // Créer indisponibilité pour la voiture
	            Disponibilite disp = new Disponibilite();
	            disp.setCar(reservation.getVoiture()); // getter correct
	            disp.setDateDebut(reservation.getDateDebut()); // ou .toLocalDate() si besoin
	            disp.setDateFin(reservation.getDateFin()); // ou .toLocalDate() si besoin
	            disp.setStatut(StatutDisponibilite.INDISPONIBLE.name());
	            disponibiliteRepository.save(disp);

	            return ResponseEntity.ok(new ReservationResponse(
	                    reservation.getId(),
	                    "Reservation créée avec succès.",
	                    "/Siteoffeciel/factures/" + facture.getId() + "/pdf"
	            ));

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur interne.");
	        }
	    } static class ReservationResponse {
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
	    
	    
	    
	    
	   

	    // Créer une réservation pour une voiture
	 
	    
	    @GetMapping("/voitures")
	    public String voitures(Model model,Authentication authentication) {
			if (authentication != null && authentication.isAuthenticated()) {
		        Object principal = authentication.getPrincipal();
		        String email = null;

		        if (principal instanceof User) {
		            email = ((User) principal).getEmail();
		        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
		            email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
		        } else if (principal instanceof String) {
		            email = (String) principal;
		        }

		        System.out.println("EMAIL = " + email);

		        if (email != null) {
		            User user = userService.findByEmail(email);
		            System.out.println("USER = " + user);
		            model.addAttribute("currentUser", user);
		        } else {
		            model.addAttribute("currentUser", null);
		        }
		    } else {
		        model.addAttribute("currentUser", null);
		    }
			 List<Car> voitures = carService.findAll();
			    model.addAttribute("voitures", voitures);

			    // 3️⃣ Construire la map des états pour chaque voiture
			    Map<Long, String> etats = new HashMap<>();
			    for (Car voiture : voitures) {
			        List<Reservation> reservations = voiture.getReservations() != null
			                ? new ArrayList<>(Arrays.asList(voiture.getReservations()))
			                : new ArrayList<>();
			        etats.put(voiture.getId(), carService.getEtatActuel(voiture, LocalDate.now(), reservations));
			    }

			    model.addAttribute("etats", etats);

	        return "Owner/gestionvoiture";
	    } 
	    @GetMapping("/voitures/{id}")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> getVoitureById(@PathVariable Long id) {
	        try {
	            System.out.println("\n========== DEBUG: DÉBUT GET /Owner/voitures/" + id + " ==========");

	            // 1️⃣ Récupérer la voiture
	            Car car = carRepository.findById(id).orElse(null);
	            if (car == null) {
	                System.out.println("⚠️  Aucune voiture trouvée avec l’ID " + id);
	                return ResponseEntity.status(404).body(null);
	            }

	            System.out.println("✅ Voiture trouvée : " + car.getMarque() + " " + car.getModele() +
	                    " | Immatriculation=" + car.getImmatriculation());
	            System.out.println("StatutTechnique initial = " + car.getStatutTechnique());

	            // 2️⃣ Récupérer les réservations depuis le repository pour être sûr de les avoir toutes
	            List<Reservation> reservations = reservationRepository.findByVoiture_Id(id);
	            System.out.println("📘 Nombre de réservations trouvées : " + reservations.size());
	            reservations.forEach(r -> {
	                System.out.println("   - Reservation ID=" + r.getId() +
	                        " | Début=" + r.getDateDebut() +
	                        " | Fin=" + r.getDateFin() +
	                        " | Type=" + r.getTypeReservation() +
	                        " | Statut=" + r.getStatut());
	            });

	            // 3️⃣ Récupérer les pannes
	            List<Panne> pannes = panneRepository.findByCarId(id);
	            System.out.println("🔧 Nombre de pannes trouvées : " + pannes.size());
	            pannes.forEach(p -> System.out.println(
	                    "   - Panne ID=" + p.getId() +
	                            " | Début=" + p.getDateDebut() +
	                            " | Fin=" + p.getDateFin() +
	                            " | CarID=" + (p.getCar() != null ? p.getCar().getId() : "NULL"))
	            );

	            // 4️⃣ Calculer l'état actuel
	            String etat = carService.getEtatActuel(car, LocalDate.now(), reservations);

	            // 5️⃣ Construire la réponse
	            Map<String, Object> response = new HashMap<>();
	            response.put("id", car.getId());
	            response.put("marque", car.getMarque());
	            response.put("modele", car.getModele());
	            response.put("immatriculation", car.getImmatriculation());
	            response.put("etat", etat);

	            System.out.println("✅ État final renvoyé = " + etat);
	            System.out.println("========== DEBUG: FIN GET /Owner/voitures/" + id + " ==========\n");

	            return ResponseEntity.ok(response);

	        } catch (Exception e) {
	            System.out.println("❌ ERREUR pendant l’exécution de getVoitureById:");
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(null);
	        }
	    }



	    @PostMapping("/disponibilite")
	    public ResponseEntity<?> createDisponibilite(@RequestBody DisponibiliteDTO dto) {
	        try {
	            Disponibilite dispo = disponibiliteService.saveDisponibilite(dto);
	            return ResponseEntity.ok(dispo);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'enregistrement");
	        }
	    }

	    @GetMapping("/info/{id}")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> getVoituresById(@PathVariable Long id) {
	        try {
	            Car car = carService.findById(id); // Récupérer la voiture par ID
	            if (car == null) {
	                return ResponseEntity.status(404).body(Map.of("message", "Voiture introuvable"));
	            }

	            Map<String, Object> response = new HashMap<>();
	            response.put("id", car.getId());
	            response.put("marque", car.getMarque());
	            response.put("modele", car.getModele());
	            response.put("immatriculation", car.getImmatriculation());
	            response.put("annee", car.getAnnee());
	            response.put("proprietaireId", car.getOwner() != null ? car.getOwner().getId() : null);
	            response.put("prixJournalier", car.getPrixJournalier());
	            response.put("description", car.getDescription());
	           // response.put("disponible", car.getDisponible());

	            // Image principale
	         // Dans ton contrôleur ou service (ex. : où tu construis la réponse JSON pour une voiture)
	            if (car.getProprietaire() != null && car.getImagePrincipaleURL() != null) {
	                String imagePrincipaleURL = "/Siteoffeciel/images/" + car.getProprietaire().getId() + "/" + car.getImagePrincipaleURL();
	                response.put("imagePrincipaleURL", imagePrincipaleURL);
	            } else {
	                response.put("imagePrincipaleURL", null);  // Ou une URL par défaut si tu préfères
	            }


	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(Map.of("message", "Erreur serveur"));
	        }
	    }

	    @GetMapping("/voitures/{id}/disponibilites")
	    @ResponseBody
	    public List<Map<String, Object>> getDisponibilitesParVoiture(@PathVariable Long id) {
	        Car car = carService.findById(id);
	        if (car == null) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voiture non trouvée");
	        }

	        List<Map<String, Object>> events = new ArrayList<>();

	        // 1️⃣ Récupérer les réservations confirmées avec méthode de paiement
	        List<Reservation> reservations = reservationRepository.findByVoiture_IdAndStatut(id, StatutReservation.CONFIRMEE);

	        for (Reservation r : reservations) {
	        	Paiement paiement = paiementRepository.findByReservationId(r.getId());
	        	String statut;
	        	if (paiement != null) {
	        	    if ("paypal".equalsIgnoreCase(paiement.getMethode())) {
	        	        statut = "RESERVER_PAYPAL";
	        	    } else if ("virement".equalsIgnoreCase(paiement.getMethode())) {
	        	        statut = "RESERVER_VIREMENT";
	        	    } else {
	        	        statut = "RESERVER";
	        	    }
	        	} else {
	        	    statut = "RESERVER"; // Par défaut si aucun paiement
	        	}


	            Map<String, Object> event = new HashMap<>();
	            event.put("type", "reservation");
	            event.put("status", statut);
	            User client = r.getUtilisateur(); // Utilisation de l'utilisateur comme client
	            if (client != null) {
	                event.put("clientFirstName", client.getFirstName());
	                event.put("clientLastName", client.getLastName());
	            } else {
	                event.put("clientFirstName", "Inconnu");
	                event.put("clientLastName", "");
	            }
	            if (paiement != null) {
	                event.put("montant", paiement.getMontant()); // montant depuis la table Paiement
	            } else {
	                event.put("montant", 0); // ou null ou "N/A"
	            }
	            event.put("start", r.getDateDebut().toString());
	            event.put("end", r.getDateFin().plusDays(1).toString());
	            events.add(event);
	        }

	        // 2️⃣ Disponibilités simples
	        List<Disponibilite> disponibilites = disponibiliteRepository.findByCarId(id);
	        for (Disponibilite d : disponibilites) {
	            Map<String, Object> event = new HashMap<>();
	            event.put("type", "disponibilite");
	            event.put("status", d.getStatut());
	            event.put("start", d.getDateDebut().toString());
	            event.put("end", d.getDateFin().plusDays(1).toString());
	            events.add(event);
	        }

	        // 3️⃣ Pannes
	        List<Panne> pannes = panneRepository.findByCarId(id);
	        for (Panne p : pannes) {
	            Map<String, Object> event = new HashMap<>();
	            event.put("type", "panne");
	            event.put("status", "panne");
	            event.put("description", p.getDescription() != null ? p.getDescription() : "Aucune description");
	            event.put("start", p.getDateDebut().toString());
	            event.put("end", p.getDateFin().plusDays(1).toString());
	            events.add(event);
	        }

	        return events;
	    }

	    @GetMapping("/paiement/{id}")
	    public String getPaiementDetails(@PathVariable Long id, Model model) {
	        Paiement paiement = paiementService.findById(id);
	        model.addAttribute("paiement", paiement);
	        return "Owner/modal-detail"; // Ce fichier retournera un fragment HTML simple
	    }
	    @GetMapping("/disponibilites/{voitureId}")
	    @ResponseBody
	    public List<Map<String, String>> getDisponibilites(@PathVariable("voitureId") Long id) {
	        List<Disponibilite> disponibilites = disponibiliteRepository.findByCarId(id);
	        return disponibilites.stream().map(d -> {
	            Map<String, String> map = new HashMap<>();
	            map.put("dateDebut", d.getDateDebut().toString());
	            map.put("dateFin", d.getDateFin().toString());
	            return map;
	        }).collect(Collectors.toList());
	    }
	    @GetMapping("/litiges/api")
	    @ResponseBody  // Indique que la méthode renvoie directement le corps HTTP (JSON)
	    public List<LitigeDTO> getLitiges() {
	        List<Litige> litiges = litigeRepository.findAll();
	        return litiges.stream()
	                      .map(LitigeDTO::new)
	                      .collect(Collectors.toList());
	    }

	    @GetMapping("/entretiens")
	    public String afficherEntretiens(Model model) {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	        if (authentication == null || !authentication.isAuthenticated()) {
	            System.err.println("❌ Utilisateur non authentifié");
	            model.addAttribute("error", "Utilisateur non authentifié.");
	            return "Owner/entretiens";
	        }

	        Object principal = authentication.getPrincipal();
	        String email = null;

	        if (principal instanceof User) {
	            email = ((User) principal).getEmail();
	        } else if (principal instanceof UserDetails) {
	            email = ((UserDetails) principal).getUsername();
	        } else if (principal instanceof String) {
	            email = (String) principal;
	        }

	        System.out.println("🔐 Email connecté : " + email);

	        if (email == null) {
	            System.err.println("❌ Impossible de récupérer l'email de l'utilisateur.");
	            model.addAttribute("error", "Impossible de récupérer l'email utilisateur.");
	            model.addAttribute("currentUser", null); // <-- ajouté

	            return "Owner/entretiens";
	        }

	        // Récupérer l'utilisateur
	        User user = userService.findByEmail(email);

	        if (user == null) {
	            System.err.println("❌ Erreur : utilisateur introuvable par email !");
	            model.addAttribute("error", "Utilisateur introuvable.");
	            return "Owner/entretiens";
	        }
	        model.addAttribute("currentUser", user); // <-- essentiel

	        boolean estOwner = user.getRoles().stream()
	            .anyMatch(role -> "ROLE_OWNER".equals(role.getName()));

	        if (!estOwner) {
	            System.err.println("❌ Accès refusé : rôle OWNER requis.");
	            model.addAttribute("error", "Accès refusé.");
	            return "Owner/entretiens";
	        }

	        // Récupérer le Propritaire
	        Propritaire proprietaire = proprietaireRepository.findByUserId(user.getId());
	        if (proprietaire == null) {
	            System.err.println("❌ Aucun propriétaire trouvé pour l'utilisateur ID: " + user.getId());
	            model.addAttribute("error", "Propriétaire introuvable.");
	            model.addAttribute("entretiens", Collections.emptyList());
	            return "Owner/entretiens";
	        }

	        System.out.println("✅ Propriétaire trouvé : " + proprietaire.getUser().getFirstName() + " " + proprietaire.getUser().getLastName());

	        try {
	            // Récupérer les entretiens
	        	List<Entretien> entretiens = entretienRepository.findActiveByProprietaireWithCar(proprietaire.getId());
	        	System.out.println("🔧 Nombre d'entretiens trouvés : " + entretiens.size());

	        	for (Entretien e : entretiens) {
	        	    if (e.getCar() != null) {
	        	        System.out.println("🚗 Entretien ID: " + e.getId() 
	        	            + " | Car ID: " + e.getCar().getId() 
	        	            + " | Marque: " + e.getCar().getMarque() 
	        	            + " | Modele: " + e.getCar().getModele());
	        	    } else {
	        	        System.out.println("⚠️ Entretien ID: " + e.getId() + " | Car est NULL !");
	        	    }
	        	}

	        	model.addAttribute("entretiens", entretiens);
	        	  entretiens.forEach(e -> {
	        	        if (e.getCar() != null) {
	        	            System.out.println("✅ JSON Car: " + e.getCar().getMarque());
	        	        } else {
	        	            System.out.println("⚠️ JSON Car NULL pour Entretien ID: " + e.getId());
	        	        }
	        	    });

	            // 🔥 CORRECTION : Ajouter la liste des voitures du propriétaire
	            List<Car> cars = carRepository.findByProprietaireId(proprietaire.getId());
	            System.out.println("🚗 Nombre de voitures trouvées : " + cars.size());
	            model.addAttribute("cars", cars);

	            // 🔥 CORRECTION : Ajouter la liste des types d'entretien
	            List<EntretienType> types = Arrays.asList(EntretienType.values());
	            model.addAttribute("types", types);

	            // 🔥 CORRECTION : Ajouter les statuts d'entretien depuis l'enum
	            List<StatutEntretien> statuts = Arrays.asList(StatutEntretien.values());
	            model.addAttribute("statuts", statuts);
	            List<Entretien> entretiensEnRetard = entretienRepository.findByStatut(StatutEntretien.EN_RETARD);
	            
	            model.addAttribute("entretiensEnRetard", entretiensEnRetard);
	        } catch (Exception e) {
	            System.err.println("❌ Erreur lors de la récupération des données : " + e.getMessage());
	            e.printStackTrace();
	            model.addAttribute("error", "Erreur lors du chargement des données : " + e.getMessage());
	            model.addAttribute("entretiens", Collections.emptyList());
	            model.addAttribute("cars", Collections.emptyList());
	            model.addAttribute("types", Collections.emptyList());
	            model.addAttribute("statuts", Collections.emptyList());
	        }

	        return "Owner/entretiens";
	    }
	    @GetMapping("/filter")
	    public ResponseEntity<List<Entretien>> filter(
	            @RequestParam(defaultValue = "") String statut,
	            @RequestParam(defaultValue = "") String type,
	            @RequestParam(defaultValue = "") String voiture,
	            Authentication authentication) {
	        
	        System.out.println("🎯 Filtres reçus - Statut: '" + statut + "', Type: '" + type + "', Voiture: '" + voiture + "'");
	        
	        try {
	            User user = (User) authentication.getPrincipal();
	            Propritaire proprietaire = proprietaireRepository.findByUserId(user.getId());
	            
	            if (proprietaire == null) {
	                System.out.println("❌ Propriétaire non trouvé");
	                return ResponseEntity.ok(Collections.emptyList());
	            }

	            // 🔥 UTILISEZ LA NOUVELLE MÉTHODE
	            List<Entretien> entretiens = entretienRepository.findActiveWithDynamicFilters(
	                proprietaire.getId(), statut, type, voiture);
	            
	            System.out.println("📊 Entretiens actifs trouvés: " + entretiens.size());
	            
	            return ResponseEntity.ok(entretiens);
	        } catch (Exception e) {
	            System.err.println("❌ Erreur dans filter: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(null);
	        }
	    }
	    @PutMapping("/entretien/{id}")
	    public ResponseEntity<Entretien> updateEntretien(@PathVariable Long id, @RequestBody EntretienUpdateDTO dto) {
	        try {
	            Optional<Entretien> optEntretien = entretienRepository.findById(id);
	            if (optEntretien.isEmpty()) {
	                return ResponseEntity.notFound().build();
	            }
	            Entretien entretien = optEntretien.get();

	            // Mise à jour des champs
	            entretien.setCar(carRepository.findById(Long.parseLong(dto.getCarId()))
	                    .orElseThrow(() -> new RuntimeException("Voiture non trouvée")));
	            entretien.setType(dto.getType());
	            entretien.setDateDebut(LocalDate.parse(dto.getDateDebut(), DateTimeFormatter.ISO_LOCAL_DATE));
	            entretien.setDateFin(dto.getDateFin() != null && !dto.getDateFin().isEmpty() 
	                ? LocalDate.parse(dto.getDateFin(), DateTimeFormatter.ISO_LOCAL_DATE) 
	                : null);
	            entretien.setPeriodique(dto.isPeriodique());
	            entretien.setProchainKmEstime(dto.getProchainKmEstimes());
	            entretien.setProchaineDateEstimee(dto.getProchaineDateEstimees() != null && !dto.getProchaineDateEstimees().isEmpty()
	                ? LocalDate.parse(dto.getProchaineDateEstimees(), DateTimeFormatter.ISO_LOCAL_DATE)
	                : null);
	           ;

	            // Calcul du statut
	            entretien.setStatut(calculerStatut(entretien));

	            // Sauvegarde
	            entretienRepository.save(entretien);
	            return ResponseEntity.ok(entretien);
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body(null);
	        }
	    }
	    @DeleteMapping("/supp/{id}")
	    public ResponseEntity<?> supprimerEntretien(@PathVariable Long id) {
	        System.out.println("🗑️  Soft delete entretien ID: " + id);
	        
	        try {
	            Optional<Entretien> entretienOpt = entretienRepository.findById(id);
	            
	            if (entretienOpt.isPresent()) {
	                Entretien entretien = entretienOpt.get();
	                
	                // 🔥 SOFT DELETE : Marquer comme supprimé
	                entretien.setSupprimer(1);
	                entretienRepository.save(entretien);
	                
	                System.out.println("✅ Entretien ID " + id + " marqué comme supprimé (supprimer = 1)");
	                return ResponseEntity.ok().build();
	            } else {
	                System.out.println("❌ Entretien non trouvé");
	                return ResponseEntity.notFound().build();
	            }
	        } catch (Exception e) {
	            System.err.println("❌ Erreur soft delete: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(500).build();
	        }
	    }
	    @GetMapping("/entretienes/{id}")
	    public ResponseEntity<EntretienUpdateDTO> getEntretienById(@PathVariable Long id) {
	        Optional<Entretien> opt = entretienRepository.findById(id);

	        if (opt.isPresent()) {
	            Entretien e = opt.get();
	            System.out.println("Entretien trouvé : id=" + e.getId());

	            EntretienUpdateDTO dto = new EntretienUpdateDTO();
	            if (e.getCar() != null) {
	                System.out.println("Voiture liée : id=" + e.getCar().getId());
	                dto.setCarId(String.valueOf(e.getCar().getId()));
	                dto.setMarque(e.getCar().getMarque()); // Ajouté
	                dto.setModele(e.getCar().getModele()); // Ajouté
	            } else {
	                System.out.println("Voiture liée est null !");
	            }
	            dto.setType(e.getType());
	            System.out.println("Type : " + e.getType());

	            if (e.getDateDebut() != null) {
	                dto.setDateDebut(e.getDateDebut().toString());
	                System.out.println("Date début : " + e.getDateDebut().toString());
	            } else {
	                System.out.println("Date début est null !");
	            }

	            if (e.getDateFin() != null) {
	                dto.setDateFin(e.getDateFin().toString());
	                System.out.println("Date fin : " + e.getDateFin().toString());
	            } else {
	                dto.setDateFin(null);
	                System.out.println("Date fin est null");
	            }


	            dto.setProchainKmEstimes(e.getProchainKmEstime());
	            System.out.println("Prochain km estimé : " + e.getProchainKmEstime());

	            if (e.getProchaineDateEstimee() != null) {
	                dto.setProchaineDateEstimees(e.getProchaineDateEstimee().toString());
	                System.out.println("Prochaine date estimée : " + e.getProchaineDateEstimee().toString());
	                dto.setRemarks(e.getObservations() != null ? e.getObservations() : "Aucune"); // Ajouté
	                System.out.println("Remarques : " + e.getObservations());
	            } else {
	                dto.setProchaineDateEstimees(null);
	                System.out.println("Prochaine date estimée est null");
	            }

	            return ResponseEntity.ok(dto);

	        } else {
	            System.out.println("Entretien non trouvé pour l'id: " + id);
	            return ResponseEntity.notFound().build();
	        }
	    }

	    public StatutEntretien calculerStatut(Entretien entretien) {
	        LocalDate aujourdhui = LocalDate.now();
	        
	        // Si date fin existe ET prochaine date estimée est dépassée → EN RETARD
	        if (entretien.getDateFin() != null && 
	            entretien.getProchaineDateEstimee() != null && 
	            entretien.getProchaineDateEstimee().isBefore(aujourdhui)) {
	            return StatutEntretien.EN_RETARD;
	        }
	        
	        // Si date fin existe → TERMINÉ
	        if (entretien.getDateFin() != null) {
	            return StatutEntretien.TERMINE;
	        }
	        
	        // Si prochaine date estimée dépassée → EN RETARD
	        if (entretien.getProchaineDateEstimee() != null && 
	            entretien.getProchaineDateEstimee().isBefore(aujourdhui)) {
	            return StatutEntretien.EN_RETARD;
	        }
	        
	        // Si date début dépassée → EN RETARD
	        if (entretien.getDateDebut().isBefore(aujourdhui)) {
	            return StatutEntretien.EN_RETARD;
	        }
	        
	        return StatutEntretien.A_VENIR;
	    }
	  

	    @PostMapping("/api/entretiens")
	    public ResponseEntity<String> ajouterEntretien(
	            @RequestParam Long carId,
	            @RequestParam String type,
	            @RequestParam("dateDebut") String dateDebut,
	            @RequestParam(required = false) String dateFin,
	            @RequestParam Double cost,
	            @RequestParam(required = false) String remarks,
	            @RequestParam(required = false) Boolean periodique,
	            @RequestParam(required = false) Integer prochainKmEstime,
	            @RequestParam(required = false) String prochaineDateEstimee,
	            @RequestParam(required = false) MultipartFile invoice) {

	        try {
	            System.out.println("===== Début du traitement de l'entretien =====");
	            System.out.println("carId: " + carId);
	            System.out.println("type: " + type);
	            System.out.println("dateDebut: " + dateDebut);
	            System.out.println("dateFin: " + dateFin);
	            System.out.println("cost: " + cost);
	            System.out.println("remarks: " + remarks);
	            System.out.println("periodique: " + periodique);
	            System.out.println("prochainKmEstime: " + prochainKmEstime);
	            System.out.println("prochaineDateEstimee: " + prochaineDateEstimee);

	            if (invoice != null) {
	                System.out.println("Fichier reçu: " + invoice.getOriginalFilename());
	                System.out.println("Taille fichier: " + invoice.getSize());
	            } else {
	                System.out.println("Aucun fichier joint.");
	            }

	            entretienService.ajouterEntretien(
	                    carId, type, dateDebut, dateFin, cost,
	                    remarks, periodique, prochainKmEstime,
	                    prochaineDateEstimee, invoice);

	            System.out.println("===== Entretien enregistré avec succès =====");
	            return ResponseEntity.ok("Entretien enregistré !");
	        } catch (Exception e) {
	            System.err.println("===== ERREUR lors de l'enregistrement =====");
	            e.printStackTrace(); // Affiche la trace complète
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Erreur lors de l'enregistrement : " + e.getMessage());
	        }
	    }

	    
	    @PostMapping("/api/litiges/{id}/repondre")
	    public ResponseEntity<?> repondreLitige(@PathVariable Long id, @Valid @RequestBody ResponseDTO response) {
	        logger.info("Début de repondreLitige pour litige id = {}", id);

	        try {
	            StatutLitige nouveauStatut = StatutLitige.valueOf(response.getStatut());

	            Optional<Litige> litigeOpt = litigeService.getLitigeById(id);
	            if (litigeOpt.isEmpty()) {
	                logger.warn("Litige non trouvé avec l'id = {}", id);
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Litige non trouvé");
	            }
	            Litige litige = litigeOpt.get();

	            litige.setResolution(response.getNote());
	            litige.setStatut(nouveauStatut);

	            litigeService.save(litige);
	            logger.info("Litige sauvegardé avec succès.");

	            User client = litige.getReservation() != null ? litige.getReservation().getUtilisateur() : null;

	            if (client != null && client.getEmail() != null) {
	                logger.info("Envoi d'email à : {}", client.getEmail());
	                String sujet = "Mise à jour de votre litige n°" + litige.getId();
	                String resolution = (response.getNote() != null) ? response.getNote() : "Pas de résolution fournie.";
	                String prenomClient = (client.getFirstName() != null) ? client.getFirstName() : "Client";

	                String corps = "Bonjour " + prenomClient + ",\n\n"
	                        + "Votre litige a été mis à jour.\n"
	                        + "Statut actuel : " + nouveauStatut.name() + "\n"
	                        + "Résolution : " + resolution + "\n\n"
	                        + "Cordialement,\n"
	                        + "L'équipe support";

	                emailService.envoyerEmail(client.getEmail(), sujet, corps);
	                logger.info("Email envoyé avec succès.");
	            } else {
	                logger.warn("Aucun client ou email disponible pour envoi d'email.");
	            }

	            return ResponseEntity.ok().build();

	        } catch (IllegalArgumentException e) {
	            logger.error("Statut invalide reçu : {}", response.getStatut());
	            return ResponseEntity.badRequest().body("Statut invalide : " + response.getStatut());
	        } catch (Exception e) {
	            logger.error("Erreur serveur", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
	        }
	    }

	    @GetMapping("/litiges/api/{id}")
	    @ResponseBody
	    public ResponseEntity<LitigeDTO> getLitigeById(@PathVariable Long id) {
	        Optional<Litige> litige = litigeRepository.findById(id);

	        if (litige.isPresent()) {
	            return ResponseEntity.ok(new LitigeDTO(litige.get()));
	        } else {
	            return ResponseEntity.notFound().build();
	        }}
	    @GetMapping("/litiges/apis")
	    @ResponseBody
	    public List<LitigeDTO> getLitiges(
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String statut,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateStart,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateEnd
	    ) {
	        // Exemple d'implémentation à adapter selon ton repo/service
	        List<Litige> litiges = litigeService.findFiltered(search, statut, dateStart, dateEnd);
	        return litiges.stream().map(LitigeDTO::new).collect(Collectors.toList());
	    }

	    @GetMapping("/statuts")
	    @ResponseBody
	    public List<String> getStatuts() {
	        return litigeRepository.findDistinctStatuts();
	    }

	    // Messages envoyés
	    @PostMapping("/envoyer")
	    @ResponseBody
	    public ResponseEntity<String> envoyerMessage(@RequestBody MessageDTO dto) {
	        try {
	            User expediteur = userService.getCurrentUser(); // propriétaire connecté
	            if (expediteur == null) {
	                logger.warn("Utilisateur non connecté.");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non connecté.");
	            }
	            logger.info("Utilisateur connecté : id={} email={}", expediteur.getId(), expediteur.getEmail());
	            System.out.println("Appel de envoyerMessage avec dto: " + dto);

	            // Chercher le client destinataire en base par email
	            User destinataire = utilisateurRepository.findByEmail(dto.getDestinataireEmail());
	            if (destinataire == null) {
	                logger.warn("Destinataire non trouvé pour email: {}", dto.getDestinataireEmail());
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Destinataire non trouvé.");
	            }
	            logger.info("Destinataire trouvé : id={} email={}", destinataire.getId(), destinataire.getEmail());

	            // Enregistrer le message avec les relations User
	            Message message = new Message();
	            message.setExpediteur(expediteur);
	            message.setDestinataire(destinataire);
	            message.setDestinataireEmail(destinataire.getEmail());
	            message.setDestinataireNom(destinataire.getFirstName() + " " + destinataire.getLastName());
	            message.setSujet(dto.getSujet());
	            message.setContent(dto.getContent());
	            message.setDateEnvoi(LocalDateTime.now());
	            message.setLu(true);

	            messageRepository.save(message);
	            logger.info("Message enregistré avec succès, id: {}", message.getId());

	            // Envoyer l'email
	            /*try {
	                emailService.envoyerEmail(destinataire.getEmail(), dto.getSujet(), dto.getContent());
	                logger.info("Email envoyé avec succès à {}", destinataire.getEmail());
	            } catch (Exception e) {
	                logger.error("Erreur lors de l'envoi de l'email à {} : {}", destinataire.getEmail(), e.getMessage());
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'email.");
	            }
*/
	            return ResponseEntity.ok("Message envoyé avec succès.");
	        } catch (Exception e) {
	            logger.error("Erreur inattendue dans envoyerMessage : ", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne serveur.");
	        }
	    }

	    @GetMapping("/recus")
	    @ResponseBody
	    public List<MessageDTO> getMessagesRecus() {
	        User currentUser = userService.getCurrentUser();
	        if (currentUser == null) {
	            logger.warn("Utilisateur courant introuvable lors de la récupération des messages reçus.");
	            return Collections.emptyList();
	        }
	        logger.info("Utilisateur courant : {} (id={})", currentUser.getUsername(), currentUser.getId());

	        List<Message> messages = messageRepository.findByDestinataire(currentUser);
	        logger.info("Nombre de messages reçus récupérés : {}", messages.size());
	        
	        // LOG DÉTAILLÉ DES TYPES
	        logger.info("=== DÉTAILS DES TYPES DE MESSAGES ===");
	        messages.forEach(m -> {
	            Object type = m.getType();
	            String typeStr = String.valueOf(type);
	            String typeUpper = typeStr.toUpperCase();
	            boolean isAlerte = "ALERTE".equals(typeUpper);
	            
	            logger.info("Message id={}, type={}, typeString={}, typeUpper={}, isAlerte={}", 
	                m.getId(), type, typeStr, typeUpper, isAlerte);
	        });
	        logger.info("=== FIN DÉTAILS ===");

	        // FILTRAGE DES ALERTES
	        List<Message> messagesFiltres = messages.stream()
	                .filter(m -> {
	                    Object type = m.getType();
	                    String typeStr = String.valueOf(type);
	                    boolean isAlerte = "ALERTE".equals(typeStr.toUpperCase());
	                    logger.debug("Filtrage - Message {}: type={}, isAlerte={}", m.getId(), typeStr, isAlerte);
	                    return !isAlerte;
	                })
	                .collect(Collectors.toList());
	        
	        logger.info("Nombre de messages après filtrage ALERTE : {}", messagesFiltres.size());

	        return messagesFiltres.stream()
	                .map(MessageDTO::new)
	                .collect(Collectors.toList());
	    }
	    @PostMapping("/messages/{id}/lire")
	    @ResponseBody
	    public ResponseEntity<?> marquerMessageCommeLu(@PathVariable Long id) {
	        Optional<Message> messageOpt = messageRepository.findById(id);
	        if (messageOpt.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        Message message = messageOpt.get();
	        message.setLu(true);  // ou message.setLu(1) selon ta variable
	        messageRepository.save(message);

	        return ResponseEntity.ok().build();
	    }
	    @PostMapping("/messages/archive")
	    @ResponseBody
	    public ResponseEntity<?> archiverMessages(@RequestBody(required = false) Map<String, List<Long>> request) {

	    	    

	        try {
	            // 1. Vérifier si la requête est null
	            if (request == null) {
	                return ResponseEntity.badRequest().body("Requête JSON absente ou mal formée.");
	            }

	            // 2. Vérifier la présence de la clé "ids"
	            if (!request.containsKey("ids")) {
	                return ResponseEntity.badRequest().body("Clé 'ids' manquante dans la requête JSON.");
	            }

	            List<Long> ids = request.get("ids");

	            // 3. Vérifier si la liste est vide
	            if (ids == null || ids.isEmpty()) {
	                return ResponseEntity.badRequest().body("La liste des IDs est vide.");
	            }

	            // 4. Vérifier si tous les IDs existent
	            List<Long> notFound = new ArrayList<>();
	            for (Long id : ids) {
	                Optional<Message> optionalMessage = messageRepository.findById(id);
	                if (optionalMessage.isPresent()) {
	                    Message message = optionalMessage.get();
	                    message.setArchiver(true); // ou setArchive(true)
	                    messageRepository.save(message);
	                } else {
	                    notFound.add(id);
	                }
	            }

	            // 5. Si certains IDs sont introuvables
	            if (!notFound.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body("Certains messages n'ont pas été trouvés : " + notFound);
	            }

	            return ResponseEntity.ok("Messages archivés avec succès.");

	        } catch (Exception e) {
	            // 6. Erreur inattendue
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Erreur serveur : " + e.getMessage());
	        }
	    }
	    @GetMapping("/profile/{utilisateurId}")
	    @ResponseBody
	    public ResponseEntity<?> getClientProfile(@PathVariable Long utilisateurId) {
	        System.out.println("➡️ Requête reçue pour l'utilisateur ID : " + utilisateurId);

	        if (utilisateurId == null || utilisateurId <= 0) {
	            System.err.println("❌ ID utilisateur invalide : " + utilisateurId);
	            return ResponseEntity.badRequest().body("ID utilisateur invalide.");
	        }

	        User client = userService.getClientProfile(utilisateurId);
	        if (client == null) {
	            System.err.println("❌ Aucun utilisateur trouvé avec l'ID : " + utilisateurId);
	            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
	        }

	        System.out.println("✅ Utilisateur trouvé : " + client.getFirstName() + " " + client.getLastName());
	        System.out.println("Numéro Permis est null: " + (client.getNumeroPermis() == null));
	        Locataire locataire = locataireRepository.findByUserId(utilisateurId);
	        
	        String numeroPermis = null;
	        if (locataire != null) {
	            numeroPermis = locataire.getNumeroPermis();
	            System.out.println("Locataire trouvé - Numéro permis: " + numeroPermis);
	        } else {
	            System.out.println("Aucun locataire trouvé pour user ID: " + utilisateurId);
	        }

	        UserProfileDTO dto = new UserProfileDTO();
	        dto.setId(client.getId());
	        dto.setFirstName(client.getFirstName());
	        dto.setLastName(client.getLastName());
	        dto.setEmail(client.getEmail());
	        dto.setTel(client.getTel());

	        // ✅ CORRECTION : Utilisez numeroPermis du Locataire, pas du Client
	        dto.setNumeroPermis(numeroPermis != null ? numeroPermis : "Non renseigné");

	        dto.setEnabled(client.isEnabled());

	        try {
	            // Reservations
	            List<Reservation> reservations = Optional.ofNullable(client.getReservations()).orElse(Collections.emptyList());
	            System.out.println("ℹ️ Reservations trouvées : " + reservations.size());

	            List<ReservationDTO> reservationsDTO = reservations.stream()
	                .filter(Objects::nonNull)
	                .map(ReservationDTO::fromEntity)
	                .collect(Collectors.toList());
	            dto.setReservations(reservationsDTO);

	            // Avis
	            List<Avis> avis = Optional.ofNullable(client.getAvis()).orElse(Collections.emptyList());
	            System.out.println("ℹ️ Avis trouvés : " + avis.size());

	            List<AvisDTO> avisDTO = avis.stream()
	                .filter(Objects::nonNull)
	                .map(a -> {
	                    AvisDTO adto = new AvisDTO();
	                    adto.setId(a.getId());
	                    adto.setCommentaire(a.getCommentaire());
	                    adto.setNote(a.getNote());
	                    return adto;
	                })
	                .collect(Collectors.toList());
	            dto.setAvis(avisDTO);

	            // Litiges
	            List<LitigeDTO> litigesDTO = new ArrayList<>();
	            for (Reservation r : reservations) {
	                if (r == null) {
	                    System.err.println("⚠️ Reservation null ignorée");
	                    continue;
	                }

	                if (r.getLitiges() == null) {
	                    System.out.println("ℹ️ Pas de litiges pour la réservation ID : " + r.getId());
	                    continue;
	                }

	                for (Litige l : r.getLitiges()) {
	                    if (l == null) {
	                        System.err.println("⚠️ Litige null ignoré");
	                        continue;
	                    }

	                    if (l.getStatut() == null) {
	                        System.err.println("⚠️ Statut null pour litige ID : " + l.getId());
	                    }

	                    LitigeDTO ldto = new LitigeDTO();
	                    ldto.setId(l.getId());
	                    ldto.setDescription(l.getDescription());
	                    ldto.setStatut(l.getStatut() != null ? l.getStatut().name() : "INCONNU");

	                    litigesDTO.add(ldto);
	                    System.out.println("✅ Litige ajouté : ID " + l.getId() + ", statut = " + ldto.getStatut());
	                }
	            }
	            dto.setLitiges(litigesDTO);

	            // Voitures réservées
	            List<CarDTO> voituresDTO = reservations.stream()
	                .map(Reservation::getVoiture)
	                .filter(Objects::nonNull)
	                .distinct()
	                .map(v -> {
	                    CarDTO cdto = new CarDTO();
	                    cdto.setId(v.getId());
	                    cdto.setMarque(v.getMarque());
	                    cdto.setModele(v.getModele());
	                    cdto.setImmatriculation(v.getImmatriculation()); // ✅ Ajouté
	                    cdto.setBoite(v.getBoite()); // optionnel
	                    cdto.setVille(v.getVille()); // optionnel
	                    cdto.setImagePrincipaleURL(v.getImagePrincipaleURL()); // optionnel
	                    return cdto;
	                
	                })
	                .collect(Collectors.toList());
	            dto.setVoituresReservees(voituresDTO);

	        } catch (Exception e) {
	            System.err.println("❌ Erreur lors du traitement du profil : " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("Erreur lors du chargement du profil.");
	        }

	        return ResponseEntity.ok(dto);
	    }
	    // POST bloquer client (enabled = false)
	    @PostMapping("/block/{utilisateurId}")
	    @ResponseBody

	    public ResponseEntity<?> blockClient(@PathVariable Long utilisateurId) {
	       userService.blockClient(utilisateurId);
	        return ResponseEntity.ok(Map.of("message", "Client bloqué avec succès"));
	    }

	    // POST débloquer client (enabled = true)
	    @PostMapping("/unblock/{utilisateurId}")
	    @ResponseBody

	    public ResponseEntity<?> unblockClient(@PathVariable Long utilisateurId) {
	    	userService.unblockClient(utilisateurId);
	        return ResponseEntity.ok(Map.of("message", "Client débloqué avec succès"));
	    }

	    @GetMapping("/avis/{utilisateurId}")
	    @ResponseBody
	    public ResponseEntity<?> getAvisWithCount(@PathVariable Long utilisateurId) {
	        try {
	            if (utilisateurId == null || utilisateurId <= 0) {
	                System.err.println("Erreur: utilisateurId invalide -> " + utilisateurId);
	                return ResponseEntity.badRequest().body("ID utilisateur invalide");
	            }

	            List<Avis> avis = userService.getAvisByClient(utilisateurId);
	            if (avis == null) {
	                System.err.println("Aucun avis trouvé pour utilisateurId: " + utilisateurId);
	                avis = new ArrayList<>();
	            }

	            Long countReservationsNotees = userService.getNombreReservationsNotees(utilisateurId);
	            if (countReservationsNotees == null) {
	                System.err.println("Nombre de réservations notées nul pour utilisateurId: " + utilisateurId);
	                countReservationsNotees = 0L;
	            }

	            // Transformation en DTO
	            List<AvisDTO> avisDTOs = avis.stream()
	                .map(a -> new AvisDTO(a.getNote(), a.getCommentaire()))
	                .collect(Collectors.toList());

	            Map<String, Object> response = new HashMap<>();
	            response.put("avis", avisDTOs);
	            response.put("nombreReservationsNotees", countReservationsNotees);

	            return ResponseEntity.ok(response);

	        } catch (Exception e) {
	            System.err.println("Exception dans getAvisWithCount pour utilisateurId " + utilisateurId);
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Erreur serveur : " + e.getMessage());
	        }
	    }
	    @GetMapping("/clientsWithReservations")
	    @ResponseBody
	    public List<Map<String, Object>> getClientsWithReservations(
	            @RequestParam(required = false) String search,
	            Principal principal) {

	        System.out.println("➡️ Début de getClientsWithReservations - Recherche: " + search);

	        try {
	            // 1. Récupérer l'utilisateur connecté
	            User userPrincipal = (User) ((Authentication) principal).getPrincipal();
	            System.out.println("👤 Utilisateur principal récupéré : " + userPrincipal.getEmail());

	            // 2. Récupérer le propriétaire
	            Propritaire proprietaire = proprietaireRepository.findByUserId(userPrincipal.getId());
	            if (proprietaire == null) {
	                System.err.println("❌ Propriétaire non trouvé pour user ID : " + userPrincipal.getId());
	                return Collections.emptyList();
	            }

	            System.out.println("🏠 Propriétaire trouvé : " + userPrincipal.getFirstName() + " " + userPrincipal.getLastName());

	            // 3. Récupérer les réservations du propriétaire
	            List<Reservation> reservations = reservationService.findReservationsByOwner(proprietaire.getId());
	            System.out.println("📦 Reservations récupérées : " + reservations.size());

	            // 4. Extraire les clients distincts
	            Set<User> clients = reservations.stream()
	                    .map(réservation -> {
	                        if (réservation.getUtilisateur() != null) {
	                            return réservation.getUtilisateur();
	                        } else if (réservation.getClient() != null) {
	                            return réservation.getClient();
	                        } else if (réservation.getLocataire() != null && réservation.getLocataire().getUser() != null) {
	                            return réservation.getLocataire().getUser();
	                        }
	                        return null;
	                    })
	                    .filter(Objects::nonNull)
	                    .collect(Collectors.toSet());

	            System.out.println("👥 Clients distincts extraits : " + clients.size());

	            // 5. Filtrer par recherche si nécessaire
	            if (search != null && !search.trim().isEmpty()) {
	                String lowerSearch = search.toLowerCase();
	                clients = clients.stream()
	                        .filter(c -> (c.getFirstName() != null && c.getFirstName().toLowerCase().contains(lowerSearch)) ||
	                                    (c.getLastName() != null && c.getLastName().toLowerCase().contains(lowerSearch)) ||
	                                    (c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerSearch)))
	                        .collect(Collectors.toSet());
	                System.out.println("🔍 Clients après filtrage par '" + search + "' : " + clients.size());
	            }

	            // 🔥 CORRECTION : Retourner les mêmes données que /Owner/list
	            List<Map<String, Object>> response = new ArrayList<>();
	            for (User client : clients) {
	                Map<String, Object> data = new HashMap<>();

	                // Infos utilisateur de base
	                data.put("id", client.getId());
	                data.put("firstName", client.getFirstName());
	                data.put("lastName", client.getLastName());
	                data.put("email", client.getEmail());
	                data.put("tel", client.getTel()); // 🔥 IMPORTANT
	                data.put("enabled", client.isEnabled() ? 1 : 0);

	                // Infos locataire
	                String numeroPermis = null;
	                String adresse = null;
	                if (client.getLocataire() != null) {
	                    numeroPermis = client.getLocataire().getNumeroPermis(); // 🔥 IMPORTANT
	                    adresse = client.getLocataire().getAdresse(); // 🔥 IMPORTANT
	                }
	                data.put("numeroPermis", numeroPermis);
	                data.put("adresse", adresse);

	                // Dernière réservation
	                Reservation lastRes = null;
	                try {
	                    lastRes = reservationService.findLastReservationByClientAndOwner(client.getId(), proprietaire.getId());
	                } catch (Exception e) {
	                    System.out.println("Erreur dernière réservation: " + e.getMessage());
	                }
	                
	                Map<String, Object> voitureData = null;
	                if (lastRes != null && lastRes.getVoiture() != null) {
	                    Car car = lastRes.getVoiture();
	                    voitureData = new HashMap<>();
	                    voitureData.put("marque", car.getMarque());
	                    voitureData.put("modele", car.getModele());
	                    voitureData.put("immatriculation", car.getImmatriculation());
	                }
	                data.put("voitureReservee", voitureData);

	                // Note moyenne
	                List<Avis> avisList = null;
	                try {
	                    avisList = avisService.findAvisByClient(client.getId());
	                } catch (Exception e) {
	                    System.out.println("Erreur avis: " + e.getMessage());
	                }
	                
	                Double moyenne = null;
	                if (avisList != null && !avisList.isEmpty()) {
	                    moyenne = avisList.stream()
	                        .filter(avis -> avis != null && avis.getNote() != null)
	                        .mapToDouble(Avis::getNote)
	                        .average()
	                        .orElse(Double.NaN);
	                    if (Double.isNaN(moyenne)) {
	                        moyenne = null;
	                    }
	                }
	                data.put("note", moyenne);

	                response.add(data);
	            }

	            System.out.println("✅ Données retournées avec " + response.size() + " clients");
	            return response;

	        } catch (Exception e) {
	            System.err.println("❌ Erreur dans getClientsWithReservations: " + e.getMessage());
	            e.printStackTrace();
	            return Collections.emptyList();
	        }
	    }
	    @PostMapping("/{id}/read")
	    @ResponseBody

	    public ResponseEntity<?> markAsRead(@PathVariable Long id, Principal principal) {
	        Optional<Message> optional = messageRepository.findById(id);
	        if (optional.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        Message message = optional.get();

	        // Vérification de sécurité
	        User user = (User) ((Authentication) principal).getPrincipal();
	        if (!message.getDestinataire().getEmail().equals(user.getEmail())) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	        }

	        message.setLu(true);
	        messageRepository.save(message);

	        return ResponseEntity.ok().build();
	    }
	    @GetMapping("/proprietaire")
	    @ResponseBody
	    public ResponseEntity<?> getReservationsProprietaire(Authentication authentication) {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            System.out.println("Aucun utilisateur authentifié.");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
	        }

	        Object principal = authentication.getPrincipal();
	        String username;

	        if (principal instanceof UserDetails) {
	            username = ((UserDetails) principal).getUsername();
	        } else if (principal instanceof String) {
	            username = (String) principal;
	        } else if (principal instanceof User) {  // <-- ajout ici
	            username = ((User) principal).getEmail();  // ou getUsername() si tu as cette méthode
	        } else {
	            System.out.println("Type principal non reconnu : " + principal);
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
	        }

	        System.out.println("Propriétaire connecté (username): " + username);
	        List<Reservation> reservations = reservationService.findByProprietaireEmail(username);

	        if (reservations == null) {
	            System.out.println("Aucune réservation trouvée pour l'utilisateur : " + username);
	            return ResponseEntity.ok(Collections.emptyList());
	        } else {
	            System.out.println("Nombre de réservations trouvées : " + reservations.size());
	            return ResponseEntity.ok(reservations);
	        }
	    }

	 

	    @PostMapping("/reservation/updateStatus")
	    @ResponseBody
	    public Reservation updateReservationStatus(@RequestParam Long id, @RequestParam String status) {
	        logger.info("Requête updateReservationStatus reçue avec id = {} et status = {}", id, status);

	        try {
	            StatutReservation statut = StatutReservation.valueOf(status);
	            Reservation updatedReservation = reservationService.updateReservationStatus(id, statut);
	            logger.info("Statut de réservation mis à jour avec succès pour id = {}", id);
	            return updatedReservation;
	        } catch (IllegalArgumentException e) {
	            logger.error("Statut invalide fourni : {}", status, e);
	            throw e;  // ou gérer l'exception selon ta logique
	        } catch (Exception e) {
	            logger.error("Erreur lors de la mise à jour du statut de réservation pour id = {}", id, e);
	            throw e;  // ou gérer l'exception selon ta logique
	        }
	    }
	    @GetMapping("/findByName")
	    public ResponseEntity<UserDTO> findUserByName(
	            @RequestParam String firstName,
	            @RequestParam String lastName) {

	        System.out.println("Recherche utilisateur avec :");
	        System.out.println(" - Prénom (firstName) : '" + firstName + "'");
	        System.out.println(" - Nom (lastName)     : '" + lastName + "'");

	        if (firstName == null || firstName.trim().isEmpty() ||
	            lastName == null || lastName.trim().isEmpty()) {
	            System.err.println("Paramètres invalides : prénom ou nom vide.");
	            return ResponseEntity.badRequest().body(null); // Ou un DTO vide selon besoin
	        }

	        return utilisateurRepository.findByFirstNameAndLastName(firstName.trim(), lastName.trim())
	            .map(user -> {
	                System.out.println("Utilisateur trouvé : " + user.getEmail());
	                return ResponseEntity.ok(new UserDTO(user));  // utilise bien le constructeur UserDTO(User user)
	            })
	            .orElseGet(() -> {
	                System.err.println("Aucun utilisateur trouvé pour : " + firstName + " " + lastName);
	                return ResponseEntity.notFound().build();  // ou ResponseEntity.ok(null) selon logique
	            });
	    }
	    @PostMapping("/messages/{id}/supprimer")
	    @ResponseBody
	    public ResponseEntity<?> supprimerMessage(@PathVariable Long id) {
	        Optional<Message> messageOpt = messageRepository.findById(id);
	        if (messageOpt.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }
	        Message message = messageOpt.get();
	        message.setDeleted(true);
	        messageRepository.save(message);
	        return ResponseEntity.ok().build();
	    }
	    @GetMapping("/messagedetail/{id}")
	    @ResponseBody
	    public ResponseEntity<?> getMessageById(@PathVariable Long id) {
	        Optional<Message> messageOpt = messageRepository.findById(id);
	        if (messageOpt.isPresent()) {
	            MessageDTO dto = new MessageDTO(messageOpt.get());
	            return ResponseEntity.ok(dto);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message non trouvé");
	        }
	    }
	    @GetMapping("/notifications")
	    public String getNotifications(Model model, Authentication authentication) {
	        User currentUser = (User) authentication.getPrincipal();
	        model.addAttribute("currentUser", currentUser);
	        model.addAttribute("notificationCount", alertService.countUnreadByUser(currentUser));
	        model.addAttribute("recentAlerts", alertService.findRecentAlerts(currentUser, 5));
	        return "Owner/notification";
	    }
	    
	 // Dans FileController ou LitigeController (injectez @Autowired ProprietaireFileService fileService;)
	    @GetMapping("/attachments/{ownerId}/{reservationId}/{filename:.+}")
	    @ResponseBody
	    public ResponseEntity<Resource> getLitigeAttachment(@PathVariable String ownerId, 
	                                                        @PathVariable String reservationId, 
	                                                        @PathVariable String filename) {
	        try {
	            Long proprietaireId = Long.parseLong(ownerId); // Validation ID

	            // Récupère la ressource via service
	            Resource resource = fileService.getAttachmentResource(proprietaireId, reservationId, filename);
	            
	            if (resource == null || !resource.exists()) {
	                logger.warn("Attachment non trouvé pour proprio {} / résa {} : {}", ownerId, reservationId, filename);
	                return ResponseEntity.notFound().build();
	            }
	            
	            // Content-Type dynamique
	            String contentType = fileService.getAttachmentContentType(proprietaireId, reservationId, filename);
	            
	            logger.info("Attachment servi avec succès : {} (type: {})", filename, contentType);
	            
	            return ResponseEntity.ok()
	                    .contentType(MediaType.parseMediaType(contentType))
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"") // Inline pour PDF/img
	                    .body(resource);
	                    
	        } catch (NumberFormatException e) {
	            logger.error("ID proprio invalide : {}", ownerId);
	            return ResponseEntity.badRequest().build();
	        } catch (IOException e) {
	            logger.error("Erreur IO pour attachment : {}", filename, e);
	            return ResponseEntity.internalServerError().build();
	        } catch (Exception e) {
	            logger.error("Erreur serveur pour attachment : {}", filename, e);
	            return ResponseEntity.internalServerError().build();
	        }
	    }
	    
	    
	    

	    @GetMapping("/notification")
	    @ResponseBody
	    public Page<Alert> getNotificationsAjax(
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "5") int size,
	            @RequestParam(defaultValue = "all") String filter,
	            Authentication authentication) {

	        User currentUser = (User) authentication.getPrincipal();
	        Pageable pageable = PageRequest.of(page, size, Sort.by("dateEnvoi").descending());

	        switch (filter.toLowerCase()) {
	            case "received":
	                return alertService.findByUserAndType(currentUser, TypeAlert.NOTIFICATION, pageable);
	            case "pending":
	                return alertService.findByUserAndType(currentUser, TypeAlert.RESERVATION, pageable); // ✅ Corrigé ici
	            case "failed":
	                return alertService.findByUserAndType(currentUser, TypeAlert.LITIGE, pageable);
	            default:
	                return alertService.findByUser(currentUser, pageable);
	        }
	    }



	    @PostMapping("/{id}/reads")
	    @ResponseBody
	    public void markAsRead(@PathVariable Long id, Authentication authentication) {
	        User currentUser = (User) authentication.getPrincipal();
	        alertService.markAsRead(id, currentUser);
	    }

	    @PostMapping("/mark-all-read")
	    @ResponseBody
	    public void markAllAsRead(Authentication authentication) {
	        User currentUser = (User) authentication.getPrincipal();
	        alertService.markAllAsRead(currentUser);
	    }

	    private String extractEmailFromPrincipal(Object principal) {
	        if (principal instanceof User) {
	            return ((User)principal).getEmail();
	        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
	            return ((org.springframework.security.core.userdetails.User) principal).getUsername();
	        } else if (principal instanceof String) {
	            return (String) principal;
	        }
	        return null;
	    }

	    private Long getCurrentUserId(Authentication authentication) {
	        if (authentication != null && authentication.isAuthenticated()) {
	            String email = extractEmailFromPrincipal(authentication.getPrincipal());
	            System.out.println("Email extrait: " + email);
	            if (email != null) {
	                User user = userService.findByEmail(email);
	                if (user != null) {
	                    System.out.println("Utilisateur trouvé: " + user.getId());
	                    return user.getId();
	                } else {
	                    System.out.println("Utilisateur non trouvé pour l'email: " + email);
	                }
	            }
	        }
	        System.out.println("Authentification échouée ou absente");
	        return null;
	    }
	    @GetMapping("/messages/search")
	    public ResponseEntity<?> searchMessages(
	            @RequestParam(name = "q", required = false) String query,
	            @RequestParam(name = "status", required = false) String status,
	            @RequestParam(name = "sortField", defaultValue = "dateEnvoi") String sortField,
	            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

	        // Récupérer l'email de l'utilisateur connecté
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
	        }

	        Object principal = auth.getPrincipal();
	        String email;

	        if (principal instanceof UserDetails) {
	            email = ((UserDetails) principal).getUsername();  // Récupère username qui est email
	        } else if (principal instanceof User) {
	            email = ((User) principal).getEmail();  // Ta classe User personnalisée
	        } else {
	            email = principal.toString(); // fallback
	        }

	        // Normaliser le paramètre status
	        String stat = (status == null || status.trim().isEmpty()) ? null : status.trim().toLowerCase();

	        // Nettoyer la query
	        String q = (query == null || query.trim().isEmpty()) ? null : query.trim().toLowerCase();

	        // Définir la direction du tri
	        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
	        Sort sort;
	        switch (sortField) {
	            case "sender":
	                sort = Sort.by(direction, "expediteur.displayName");
	                break;
	            case "subject":
	                sort = Sort.by(direction, "sujet");
	                break;
	            case "status":
	                sort = Sort.by(direction, "lu"); // Assure-toi que c'est bien "lu" et non "Lu"
	                break;
	            case "date":
	            default:
	                sort = Sort.by(direction, "dateEnvoi");
	                break;
	        }

	        System.out.println("Recherche messages pour email = " + email + ", query = " + q + ", status = " + stat);

	        // Appeler la méthode repository pour chercher les messages par email utilisateur
	        List<Message> messages = messageRepository.searchMessagesByEmail(email, q, stat, sort);

	        // Formatter la date
	        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");



	        // Construire la réponse
	        List<Map<String, Object>> result = messages.stream().map(m -> {
	            Map<String, Object> map = new HashMap<>();
	            map.put("id", m.getId());
	            map.put("sujet", m.getSujet());
	            map.put("expediteur", m.getExpediteur() != null ? m.getExpediteur().getDisplayName() : "Inconnu");
		        map.put("dateEnvoiFormatted", m.getDateEnvoi() != null ? dtf.format(m.getDateEnvoi()) : "");
	            map.put("isLu", m.isLu());
	            map.put("destinataireEmail", m.getDestinataire() != null ? m.getDestinataire().getEmail() : "");
	            map.put("destinataireNom", m.getDestinataire() != null ? m.getDestinataire().getDisplayName() : "");
	            return map;
	        }).collect(Collectors.toList());

	        return ResponseEntity.ok(result);
	    }
	    
	    
	    
	    @PostMapping("/pannes")
	    public ResponseEntity<?> createPanne(@Validated @RequestBody PanneDTO panneDTO) {
	        try {
	            Panne panne = new Panne();
	            panne.setDateDebut(panneDTO.getDateDebut());
	            panne.setDateFin(panneDTO.getDateFin());
	            panne.setDescription(panneDTO.getDescription());

	            Panne savedPanne = panneService.createPanne(panne, panneDTO.getCarId());
	            return ResponseEntity.ok(savedPanne);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().body(new ErrorResponse("Erreur de validation: " + e.getMessage()));
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body(new ErrorResponse("Erreur serveur: " + e.getMessage()));
	        }
	    }
	}

	class ErrorResponse {
	    private String message;
	    public ErrorResponse(String message) { this.message = message; }
	    public String getMessage() { return message; }
	    public void setMessage(String message) { this.message = message; }
	    
	    
	    
	  
}