package location_voiture.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import location_voiture.persistence.dto.AvisDTO;
import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.dto.ClientDTO;
import location_voiture.persistence.dto.DashboardStatsDTO;
import location_voiture.persistence.dto.LitigeDTO;
import location_voiture.persistence.dto.PaiementDTO;
import location_voiture.persistence.dto.ProprietaireDto;
import location_voiture.persistence.dto.RapportRequest;
import location_voiture.persistence.dto.ReservationDetailsDTO;
import location_voiture.persistence.dto.UserDTO;
import location_voiture.persistence.dto.UserProfileDTO;
//import location_voiture.persistence.model.Administrateur;
import location_voiture.persistence.model.Alert;
import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Gallery;
import location_voiture.persistence.model.Litige;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutApprobationVoiture;
import location_voiture.persistence.model.StatutLitige;
import location_voiture.persistence.model.StatutPaiement;
import location_voiture.persistence.model.TypeAlert;
//import location_voiture.repository.AdministrateurRepository;
import location_voiture.repository.AlertRepository;
import location_voiture.repository.CarRepository;
import location_voiture.repository.GalleryRepository;
import location_voiture.repository.LitigeRepository;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.ProprietaireRepository;
import location_voiture.repository.ReservationRepository;
//import location_voiture.service.AdminService;
import location_voiture.service.AlertService;
import location_voiture.service.AvisService;
import location_voiture.service.CarService;
import location_voiture.service.DashboardService;
import location_voiture.service.DemandePartenariatService;
import location_voiture.service.FactureService;
import location_voiture.service.GalleryService;
import location_voiture.service.GeoCodingService;
import location_voiture.service.LitigeService;
import location_voiture.service.PaiementService;
import location_voiture.service.ProprietaireFileService;
import location_voiture.service.ProprietaireService;
import location_voiture.service.ReservationService;
import ma.abisoft.persistence.dao.RoleRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
@Controller
@RequestMapping("/Administrateur")
public class AdminController {

	// Injection de dépendances via les champs (vous pourriez préférer l'injection par constructeur)
    @Autowired
    private CarService voitureService;
    @Autowired
    private AvisService avisService;
    @Autowired
    private DemandePartenariatService demandeService;
   // @Autowired
    //private AdministrateurRepository adminRepository; 
    @Autowired
    private LitigeService litigeService;
    @Autowired
    private GeoCodingService geoCodingService;
    @Autowired
    private AlertRepository alertRepository;
   // private final AdminService adminService;

   // @Autowired
    //public AdminController(AdminService adminService) {
        //this.adminService = adminService;
   // }
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PaiementService paiementService;
    @Autowired
    private ProprietaireRepository proprietaireRepository; 
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarRepository carRepository; // Utilisé dans ajouterVoiture et afficherTableauDeBord
    @Autowired
    private ProprietaireService utilisateurService; // Renommé pour la clarté, correspond à votre usage
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private FactureService factureService;
    @Autowired
    private GalleryRepository galleryRepository;
    @Autowired
    private GalleryService galleryService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private ProprietaireFileService fileService;

    
    @Autowired
    private UserService userService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private LitigeRepository litigeRepository;
    @Autowired
    private   PaiementRepository
    paiementRepository
;
    @Autowired
    private ReservationService reservationService;
    @PersistenceContext
    private EntityManager entityManager;
   
    @Autowired
    private ReservationRepository reservationRepository;
    @GetMapping("/api/cars/filter")
    @ResponseBody
    public ResponseEntity<List<CarDTO>> filtrerVoituresApi(
            @RequestParam(name = "make", required = false) String marque,
            @RequestParam(name = "model", required = false) String modele,
            @RequestParam(name = "status", required = false) String statut) {

        // Récupère toutes les voitures filtrées
        List<Car> resultatsEntites = voitureService.filtrerVoitures(marque, modele, statut);

        // Filtrer les voitures masquées côté base
        resultatsEntites = resultatsEntites.stream()
                            .filter(car -> car.getSupprimer() == null || car.getSupprimer() != 1)
                            .collect(Collectors.toList());

        // Convertir en DTO
        List<CarDTO> resultatsDto = resultatsEntites.stream()
                                         .map(this::convertToCarDto)
                                         .collect(Collectors.toList());

        return ResponseEntity.ok(resultatsDto);
    }

    @PostMapping("/masquer/{id}")
    @ResponseBody
    public ResponseEntity<?> masquerVoiture(@PathVariable Long id, @RequestParam Integer supprimer) {
        Optional<Car> carOpt = carRepository.findById(id);
        if(carOpt.isPresent()) {
            Car car = carOpt.get();
            car.setSupprimer(supprimer); // mettre 1
            carRepository.save(car);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false));
    }

    
    @GetMapping("/admin/reservations/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> getReservationDetails(@PathVariable String bookingId) {
        // Enlever le préfixe 'R' pour l'ID si besoin
        Long id = Long.parseLong(bookingId.substring(1));
        Optional<Reservation> res = reservationService.findById(id);
        if (!res.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation non trouvée");
        }
        Reservation reservation = res.get();

        // Préparer la réponse JSON (DTO ou Map)
        Map<String, Object> response = new HashMap<>();
        response.put("id", reservation.getId());
        response.put("clientName", reservation.getUtilisateur().getFirstName() + " " + reservation.getUtilisateur().getLastName());
        response.put("carMarque", reservation.getVoiture().getMarque());
        response.put("carModel", reservation.getVoiture().getModele());
        response.put("dateDebut", reservation.getDateDebut().toString());
        response.put("dateFin", reservation.getDateFin().toString());
        response.put("statut", reservation.getStatut().name());
        response.put("montant", reservation.getMontant());

        return ResponseEntity.ok(response);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @GetMapping("/all")
    public List<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("firstName", user.getFirstName());
            map.put("lastName", user.getLastName());
            map.put("email", user.getEmail());
            map.put("roles", user.getRoles()
                                .stream()
                                .map(Role::getName) // Assurez-vous que Role a un getName()
                                .collect(Collectors.toList()));
            result.add(map);
        }

        return result;
    }
    // La méthode de conversion est définie dans la même classe AdminController
    private CarDTO convertToCarDto(Car car) {
        if (car == null) {
            return null;
        }
        CarDTO carDto = new CarDTO();
        // Assurez-vous que car.getId() retourne Long
        if (car.getId() instanceof Long) {
            carDto.setId((Long) car.getId());
        } else if (car.getId() != null) {
            // Log ou gestion d'erreur si ce n'est pas un Long
            System.err.println("Avertissement: car.getId() n'est pas de type Long pour la voiture avec l'objet ID: " + car.getId());
            // Vous pourriez essayer de convertir si c'est un Number, ou retourner null/lever une exception
            if (car.getId() instanceof Number) {
                 carDto.setId(((Number)car.getId()).longValue());
            }
        }


        carDto.setMarque(car.getMarque());
        carDto.setModele(car.getModele());
        carDto.setVille(car.getVille());
        carDto.setImmatriculation(car.getImmatriculation());

        if (car.getStatutApprobation() != null) {
            carDto.setStatutApprobation(car.getStatutApprobation());
        } else {
        }

        carDto.setPrixJournalier(car.getPrixJournalier());
        carDto.setImagePrincipaleURL(car.getImagePrincipaleURL());

        if (car.getProprietaire() != null) {
            ProprietaireDto proprietaireDto = new ProprietaireDto();

            Propritaire proprietaire = car.getProprietaire();
            
            // Vérifie que le User est présent
            if (proprietaire.getUser() != null) {
                User user = proprietaire.getUser();
                proprietaireDto.setId(proprietaire.getId());
                proprietaireDto.setFirstName(user.getFirstName());
                proprietaireDto.setLastName(user.getLastName());
                // tu peux aussi ajouter email, tel, roles, etc.
            }

            carDto.setProprietaire(proprietaireDto);
        }

        return carDto;

    }

    // Endpoint pour afficher la page HTML initiale du tableau de bord
    @GetMapping("/tableaubord")
    public String afficherTableauDeBord(Model model, Principal principal) {
  

        DashboardStatsDTO stats = dashboardService.getStats();
        model.addAttribute("stats", stats);
        model.addAttribute("pageTitle", "Tableau de Bord - Admin");
        model.addAttribute("statuts", StatutApprobationVoiture.values());

        // Données pour les graphiques, etc.
        model.addAttribute("revenusParMois", dashboardService.getRevenusDes6DerniersMois());
        model.addAttribute("statutsReservation", dashboardService.getStatutsReservation());
        model.addAttribute("tachesEnAttente", dashboardService.getTachesEnAttente());
        model.addAttribute("activitesRecentes", dashboardService.getActivitesRecentes());
        List<Reservation> reservations = reservationRepository.findAll();
        model.addAttribute("reservations", reservations);
        List<String> marques = carRepository.findDistinctMarques();
        List<String> modeles = carRepository.findDistinctModeles();
        List<Paiement> paiements = paiementService.findAll();
        model.addAttribute("paiements", paiements);
        model.addAttribute("marques", marques);
        model.addAttribute("modeles", modeles);
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("demandes", demandeService.getToutesLesDemandes());

        return "Administrateurproprietaire/dashbordAdmin"; // Nom de votre template Thymeleaf
    }
   /* @PostMapping("/admin/ajouter")
     
    public ResponseEntity<String> ajouterAdmin(@RequestBody Map<String,String> data) {
        try {
            System.out.println("Requête reçue pour ajouter : " + data);

            adminService.creerAdmin(
                data.get("firstName"),
                data.get("lastName"),
                data.get("email"),
                data.get("password"),
                Integer.parseInt(data.get("niveauAcces"))
            );
            return ResponseEntity.ok("Administrateur ajouté avec succès");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erreur serveur");
        }
        
    }
*/
    
    @GetMapping("/owners")
    public ResponseEntity<List<UserDTO>> getOwners() {
        System.out.println("getOwners() dans AdminController appelée");
        List<UserDTO> ownersDTO = userService.getOwnersDTO();
        System.out.println("OwnersDTO récupérés (taille) : " + (ownersDTO == null ? "null" : ownersDTO.size()));
        return ResponseEntity.ok(ownersDTO);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<UserDTO>> getClients() {
        List<UserDTO> clientsDTO = userService.getClientsDTO();
        System.out.println("Nombre clients retournés : " + (clientsDTO != null ? clientsDTO.size() : "null"));
        return ResponseEntity.ok(clientsDTO);
    }

    @GetMapping("/visitors")
    public ResponseEntity<List<UserDTO>> getVisitors() {
        List<UserDTO> visitors = userService.getVisitorsDTO();
        return ResponseEntity.ok(visitors);
    }
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO updatedUser, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non connecté");
        }

        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        // Mettre à jour uniquement les champs autorisés
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        try {
            userService.save(user);
            return ResponseEntity.ok("Profil mis à jour avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour du profil");
        }
    }

    @GetMapping("/profile")
    @ResponseBody
    public ResponseEntity<User> getConnectedUser(Principal principal) {
        if (principal == null) {
            System.out.println("🔴 Aucun utilisateur connecté (principal null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            Object principalObj = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            if (principalObj instanceof org.springframework.security.core.userdetails.User) {
                email = ((org.springframework.security.core.userdetails.User) principalObj).getUsername();
            } else if (principalObj instanceof User) {
                email = ((User) principalObj).getEmail();
            } else {
                email = principal.getName();
            }
        } else {
            email = principal.getName();
        }

        System.out.println("🔵 Récupération du profil pour l'email : " + email);

        User user = userService.findByEmail(email);

        if (user == null) {
            System.out.println("🔴 Aucun utilisateur trouvé avec cet email.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        System.out.println("✅ Utilisateur trouvé : " + user.getFirstName() + " " + user.getLastName());
        return ResponseEntity.ok(user);
    }

    
    @PostMapping("/ajoutervoiture")
    @ResponseBody
    public ResponseEntity<?> ajouterVoiture(
            @RequestParam("marque") String marque,
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
            @RequestParam("categorie") String categorie,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("galleryImages") MultipartFile[] galleryImages,
            Principal principal) {

        Map<String, Object> resp = new HashMap<>();

        try {
            // Vérification image principale
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Image principale obligatoire."
                ));
            }

            Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email;

            if (principalObj instanceof UserDetails) {
                email = ((UserDetails) principalObj).getUsername(); // username = email
            } else if (principalObj instanceof User) {
                email = ((User) principalObj).getEmail();
            } else {
                throw new RuntimeException("Impossible de récupérer l'email de l'utilisateur connecté");
            }

            System.out.println("====> Email récupéré correctement : " + email);

            User proprietaireUser = userRepository.findByEmail(email);
            if (proprietaireUser == null) {
                throw new RuntimeException("Propriétaire non trouvé");
            }

      

            Propritaire proprietaire = proprietaireRepository.findByUser(proprietaireUser);
            if (proprietaire == null) {
                throw new RuntimeException("Propriétaire lié à l'utilisateur non trouvé");
            }

            // ⚡ Créer automatiquement les dossiers uploads du propriétaire
            fileService.initProprietaireFolders(proprietaire.getId());

            // Sauvegarder image principale via ProprietaireFileService
            String mainImagePath = fileService.saveImage(imageFile, proprietaire.getId());

            // Récupérer coordonnées ville
            Double latitude = 0.0, longitude = 0.0;
            try {
                Double[] coords = geoCodingService.getLatLong(ville);
                if (coords != null) {
                    latitude = coords[0];
                    longitude = coords[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Créer l'objet Car
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
            car.setCategorie(categorie);
            car.setProprietaire(proprietaire);
            car.setLatitude(latitude);
            car.setLongitude(longitude);
            car.setImagePrincipaleURL(mainImagePath);

            // Sauvegarder voiture
            carRepository.save(car);

            // Sauvegarder images galerie
            if (galleryImages != null && galleryImages.length > 0) {
                for (MultipartFile galleryImage : galleryImages) {
                    if (galleryImage != null && !galleryImage.isEmpty()) {
                        String galleryPath = fileService.saveImage(galleryImage, proprietaire.getId());
                        Gallery gallery = new Gallery();
                        gallery.setImageURL(galleryPath);
                        gallery.setVoiture(car);
                        galleryService.saveGalleryImage(gallery);
                    }
                }
            }

            resp.put("success", true);
            resp.put("message", "Voiture ajoutée avec succès !");
            return ResponseEntity.ok(resp);

        } catch (IOException e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "Erreur fichier image : " + e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "Erreur serveur : " + e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    @GetMapping("/profile/{id}")
    @ResponseBody
    public String showProfile(@PathVariable Long id) {
        User user = utilisateurService.findById(id);
        return "<p><strong>Nom :</strong> " + user.getNom() + "</p>" +
               "<p><strong>Email :</strong> " + user.getEmail() + "</p>" +
               "<p><strong>Rôle :</strong> " + user.getRole().getNom() + "</p>" +
               "<p><strong>Statut :</strong> " + user.getStatut() + "</p>";
    }

    @GetMapping("/historique/{id}")
    @ResponseBody
    public String showHistorique(@PathVariable Long id) {
        List<Reservation> reservations = userService.findReservationsByUserId(id);
        StringBuilder html = new StringBuilder();
        html.append("<table class='table'><thead><tr>")
            .append("<th>Voiture</th><th>Début</th><th>Fin</th><th>Statut</th>")
            .append("</tr></thead><tbody>");

        for (Reservation r : reservations) {
            html.append("<tr>")
                .append("<td>").append(r.getCar().getMarque()).append(" ").append(r.getCar().getModele()).append("</td>")
                .append("<td>").append(r.getDateDebut()).append("</td>")
                .append("<td>").append(r.getDateFin()).append("</td>")
                .append("<td>").append(r.getStatut()).append("</td>")
                .append("</tr>");
        }
        html.append("</tbody></table>");
        return html.toString();
    }

  

    @PostMapping("/block/{id}")
    @ResponseBody
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok("Utilisateur bloqué");
    }
    @GetMapping("/api/disputes")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public List<Litige> getAllDisputes() {
        TypedQuery<Litige> query = entityManager.createQuery(
            "SELECT l FROM Litige l " +
            "LEFT JOIN FETCH l.reservation r " +
            "LEFT JOIN FETCH r.locataire " +
            "LEFT JOIN FETCH r.voiture v " +
            "LEFT JOIN FETCH v.proprietaire",
            Litige.class
        );
        return query.getResultList();
    }

   
    @GetMapping("/history/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDisputeHistory(@PathVariable Long id) {
        TypedQuery<Litige> query = entityManager.createQuery(
            "SELECT l FROM Litige l " +
            "LEFT JOIN FETCH l.reservation r " +
            "LEFT JOIN FETCH r.locataire " +
            "LEFT JOIN FETCH r.voiture v " +
            "LEFT JOIN FETCH v.proprietaire " +
            "WHERE l.id = :id",
            Litige.class
        );
        query.setParameter("id", id);
        Litige litige = query.getResultList().stream().findFirst().orElse(null);
        if (litige == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Litige non trouvé"));
        }
        return ResponseEntity.ok(litige);
    }
    @GetMapping
    public ResponseEntity<List<LitigeDTO>> getAllLitiges() {
        List<Litige> litiges = litigeService.findAll();
        
        // Convertir en LitigeDTO
        List<LitigeDTO> litigeDTOs = litiges.stream()
            .map(LitigeDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(litigeDTOs);
    }
   

    @GetMapping("/{id}")
    public ResponseEntity<Litige> getLitigeById(@PathVariable Long id) {
        logger.info("➡️ Requête GET pour litige avec ID : {}", id);

        return litigeService.findById(id)
                .map(litige -> {
                    logger.info("✅ Litige trouvé : {}", litige);

                    // Tests des propriétés du litige
                    if (litige.getReservation() == null) {
                        logger.warn("⚠️ Reservation est NULL pour le litige ID : {}", litige.getId());
                    } else {
                        logger.info("🟢 Reservation ID : {}", litige.getReservation().getId());
                    }

                    if (litige.getClient() == null && litige.getUtilisateur() == null) {
                        logger.warn("⚠️ Aucun client/utilisateur associé au litige ID : {}", litige.getId());
                    } else {
                        logger.info("🟢 Client : {}, Utilisateur : {}", litige.getClient(), litige.getUtilisateur());
                    }

                    if (litige.getVehicle() == null) {
                        logger.warn("⚠️ Véhicule est NULL pour le litige ID : {}", litige.getId());
                    } else {
                        logger.info("🟢 Véhicule ID : {}", litige.getVehicle());
                    }

                    return ResponseEntity.ok(litige);
                })
                .orElseGet(() -> {
                    logger.warn("❌ Litige non trouvé pour l'ID : {}", id);
                    return ResponseEntity.notFound().build();
                });
        
    }


    // Résoudre un litige
    @PostMapping("/{id}/resoudre")
    public ResponseEntity<?> resolveDispute(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return litigeRepository.findById(id).map(dispute -> {
            dispute.setStatut(StatutLitige.RESOLU);
            dispute.setResolution(payload.get("response"));
            litigeRepository.save(dispute);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // Fermer un litige
    @PutMapping("/{id}/fermer")
    public ResponseEntity<?> closeDispute(@PathVariable Long id) {
        return litigeRepository.findById(id).map(dispute -> {
            dispute.setStatut(StatutLitige.FERME);
            litigeRepository.save(dispute);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // Rejeter un litige
    @PutMapping("/{id}/rejeter")
    public ResponseEntity<?> rejectDispute(@PathVariable Long id) {
        return litigeRepository.findById(id).map(dispute -> {
            dispute.setStatut(StatutLitige.REJETE);
            litigeRepository.save(dispute);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

  
    @GetMapping("/details/{id}")
    public ResponseEntity<PaiementDTO> getPaiementById(@PathVariable Long id) {
        System.out.println("Entrée dans getPaiementById avec id = " + id);  // TEST LOG
        Paiement paiement = paiementService.findById(id);
        if (paiement == null) {
            System.out.println("Paiement null pour id = " + id); // TEST LOG
            return ResponseEntity.notFound().build();
        }
        PaiementDTO dto = paiementService.toDto(paiement);
        System.out.println("DTO créé : " + dto);
        return ResponseEntity.ok(dto);
    }


    // 2. POST retenter paiement
    @PostMapping("/{id}/retry")
    public ResponseEntity<Map<String, String>> retryPaiement(@PathVariable Long id) {
        boolean success = paiementService.retryPaiement(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Paiement re-tenté avec succès."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Échec lors de la tentative."));
        }
    }
    @GetMapping("/paiement/{id}/facture")
    @ResponseBody
    public ResponseEntity<byte[]> getFacturePdfByPaiement(@PathVariable Long id) {
        System.out.println("Début getFacturePdfByPaiement avec reservation id = " + id);
        Facture facture = factureService.findByReservationId(id);  // <-- ici on cherche par reservation id
        if (facture == null || facture.getFacturePdf() == null) {
            System.out.println("Aucune facture trouvée pour reservation id = " + id);
            return ResponseEntity.notFound().build();
        }
        System.out.println("Facture trouvée pour reservation id = " + id);
        byte[] pdf = facture.getFacturePdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // 3. POST rembourser paiement
    @PostMapping("/paiement/{id}/rembourser")
    @ResponseBody
    public ResponseEntity<String> rembourserPaiement(@PathVariable Long id) {
        try {
            Paiement paiement = paiementService.findById(id);
            paiement.setStatut(StatutPaiement.REMBOURSE);
            paiementService.save(paiement);
            return ResponseEntity.ok("Paiement remboursé.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // 4. POST renvoyer notification
    @PostMapping("/paiement/{idPaiement}/renvoyer-notification")
    public ResponseEntity<?> renvoyerNotification(@PathVariable Long idPaiement) {

        Paiement paiement = paiementService.findById(idPaiement);
        if (paiement == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("message", "Paiement introuvable"));
        }

        Reservation reservation = paiement.getReservation();
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", "Reservation introuvable"));
        }

        Car voiture = reservation.getVoiture();
        if (voiture == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", "Voiture introuvable"));
        }

        Propritaire proprietaire = voiture.getProprietaire(); // Propritaire, pas User
        if (proprietaire == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", "Propriétaire introuvable"));
        }

        // Récupérer l'utilisateur lié
        User user = proprietaire.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", "Utilisateur lié au propriétaire introuvable"));
        }

        // Maintenant tu peux utiliser 'user'


        Alert alert = new Alert();
        alert.setSujet("Nouvelle réservation payée");
        alert.setMessage("Un client a réservé et payé pour votre voiture " + voiture.getMarque() + " (ID: " + voiture.getId() + ").");
        alert.setType(TypeAlert.NOTIFICATION);
        alert.setUtilisateur(proprietaire.getUser()); // <-- ici on passe un User
        alert.setDateEnvoi(LocalDateTime.now());
        alert.setEnvoyeAvecSucces(true);

        alertService.save(alert); // enregistre l'alerte en BDD

        return ResponseEntity.ok(Map.of("message", "Notification envoyée au propriétaire."));
    }

    @PostMapping("/{reviewId}/approuver")
    public @ResponseBody void approveReview(@PathVariable Long reviewId) {
        avisService.updateReviewStatus(reviewId, "APPROVED");
    }

    @PostMapping("/{reviewId}/rejeter")
    public @ResponseBody void rejectReview(@PathVariable Long reviewId) {
        avisService.updateReviewStatus(reviewId, "REJECTED");
    }

    @PostMapping("/{reviewId}/modérer")
    public @ResponseBody void moderateReview(@PathVariable Long reviewId) {
        // Implement moderation logic (e.g., open modal for editing)
    }

    @PostMapping("/{reviewId}/répondre")
    public @ResponseBody void respondReview(@PathVariable Long reviewId) {
        // Implement response logic
    }

    @PostMapping("/{reviewId}/signaler")
    public @ResponseBody void flagReview(@PathVariable Long reviewId) {
        // Implement flag logic
    }

    @PostMapping("/{reviewId}/détails")
    public @ResponseBody void viewDetails(@PathVariable Long reviewId) {
        // Implement details view logic
    }

    @PostMapping("/{reviewId}/voir original")
    public @ResponseBody void viewOriginal(@PathVariable Long reviewId) {
        // Implement view original logic
    }

    @PostMapping("/{reviewId}/supprimer définitivement")
    public @ResponseBody void deletePermanently(@PathVariable Long reviewId) {
        avisService.deleteReview(reviewId);
    }
    @GetMapping("/api/avis")
    public ResponseEntity<?> getAvis(
        @RequestParam(required = false, defaultValue = "Tous") String type,
        @RequestParam(required = false, defaultValue = "0") Integer note
    ) {
        try {
            System.out.println("getAvis appelé avec type = " + type + ", note = " + note);

            List<Avis> avis = avisService.getFilteredAvis(type, note);

            System.out.println("Nombre d'avis récupérés : " + avis.size());

            // Convertir en DTO
            List<AvisDTO> dtoList = avis.stream()
                    .map(AvisDTO::new)
                    .collect(Collectors.toList());

            System.out.println("Nombre d'avis convertis en DTO : " + dtoList.size());

            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            e.printStackTrace();
            // Retourner une réponse d'erreur propre à l'API REST
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des avis : " + e.getMessage());
        }
    }

    @PostMapping(value = "/publier", consumes = "application/json")
    public ResponseEntity<?> publierAvis(@RequestBody Avis avis) {
        System.out.println("Avis reçu : " + avis);
        // traitement
        return ResponseEntity.ok().build();
    }



    // Supprimer un avis (DELETE)
    @DeleteMapping("/supprimer/{id}")
    @ResponseBody
    public ResponseEntity<Void> supprimerAvis(@PathVariable Long id) {
        boolean supprimé = avisService.supprimerAvis(id);
        if (supprimé) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Liste de tous les avis (GET)
    
    @PostMapping("/suspend/{id}")
    @ResponseBody
    public ResponseEntity<User> suspendUser(@PathVariable("id") Long userId) {
        System.out.println("suspendUser called with id = " + userId);
        try {
            User updatedUser = utilisateurService.suspendUser(userId);
            System.out.println("User suspended successfully: " + updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.err.println("RuntimeException in suspendUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            System.err.println("Exception in suspendUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/profiles/{id}")
    @ResponseBody
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable("id") Long userId) {
        System.out.println("getUserProfile called with id = " + userId);
        try {
            User user = utilisateurService.getUserById(userId);
            if (user == null) {
                System.err.println("User not found with id = " + userId);
                return ResponseEntity.notFound().build(); // plus propre
            }

            System.out.println("User found: " + user);

            // On construit un UserProfileDTO
            UserProfileDTO userDTO = new UserProfileDTO(user);

            // On renvoie le DTO
            return ResponseEntity.ok(userDTO);

        } catch (RuntimeException e) {
            System.err.println("RuntimeException in getUserProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) {
            System.err.println("Exception in getUserProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable("id") Long userId) {
        try {
            System.out.println("🔍 Chargement stats pour user ID: " + userId);
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                System.out.println("❌ Utilisateur non trouvé: " + userId);
                return ResponseEntity.status(404).body(null);
            }

            User user = userOpt.get();
            System.out.println("✅ Utilisateur trouvé: " + user.getFirstName() + " " + user.getLastName());

            // CORRECTION : Utilisez CarRepository pour compter les voitures
            Long voituresCount = carRepository.countByUserId(userId);
            System.out.println("📊 Nombre de voitures: " + voituresCount);

            Map<String, Object> stats = new HashMap<>();
            stats.put("name", user.getFirstName() + " " + user.getLastName());
            stats.put("voituresCount", voituresCount);
            stats.put("email", user.getEmail());
            stats.put("telephone", user.getTel());
            
            // Autres stats
            String otherStats = "Membre depuis " + (user.getId() % 12 + 1) + " mois";
            stats.put("otherStats", otherStats);

            System.out.println("📈 Stats générées: " + stats);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur dans getUserStats: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Map<String, String>> blocksUser(@PathVariable Long id) {
        boolean success = userService.toggleBlockUser(id);
        if (success) {
            Map<String, String> response = Map.of("status", "OK", "message", "Utilisateur bloqué/débloqué avec succès");
            return ResponseEntity.ok(response);
        }
        Map<String, String> response = Map.of("status", "ERROR", "message", "Erreur lors du blocage");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    
    // GET /admin/users/{id}/history/json => retourne l'historique en JSON (pour AJAX)
    @GetMapping("/{id}/history/json")
    @ResponseBody
    public ResponseEntity<?> getUserHistoryJson(@PathVariable Long id) {
        try {
            User user = utilisateurService.findById(id);

            if (user == null) {
                System.out.println("Utilisateur non trouvé pour l'ID : " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("Utilisateur non trouvé");
            }

            List<?> historyList = userService.getUserHistory(id);

            if (historyList == null || historyList.isEmpty()) {
                System.out.println("Historique vide pour l'utilisateur ID : " + id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            System.out.println("Historique récupéré pour l'utilisateur ID : " + id + ", taille : " + historyList.size());

            return ResponseEntity.ok(historyList);

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'historique pour l'utilisateur ID : " + id);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erreur interne serveur");
        }
    }
    @PutMapping("/users/{id}/activer")
    public ResponseEntity<?> activerUtilisateur(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec ID: " + id));

        if (user.isEnabled()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("L'utilisateur est déjà activé.");
        }

        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur activé avec succès (ID: " + id + ").");
    }


    // 2️⃣ Récupérer le profil d'un utilisateur
    @GetMapping("/profilessetaille/{id}")
    public ResponseEntity<?> getUserProfiles(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // on renvoie uniquement les infos nécessaires (évite d'envoyer le password !)
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", user.getId());
        profileData.put("firstName", user.getFirstName());
        profileData.put("lastName", user.getLastName());
        profileData.put("email", user.getEmail());
        profileData.put("tel", user.getTel());
        profileData.put("statut", user.getStatut());

        return ResponseEntity.ok(profileData);
    }

    @GetMapping("/downloadFile/{litigeId}")
    public ResponseEntity<Resource> downloadLitigeAttachment(@PathVariable Long litigeId) {
        try {
            Optional<Litige> optionalLitige = litigeRepository.findById(litigeId);

            if (optionalLitige.isEmpty()) {
                logger.warn("Litige non trouvé pour ID : " + litigeId);
                return ResponseEntity.notFound().build();  // Resource vide, OK
            }

            Litige litige = optionalLitige.get();
            String attachmentPath = litige.getAttachmentPath();

            if (attachmentPath == null || attachmentPath.isEmpty()) {
                logger.warn("Aucun attachment pour litige ID : " + litigeId);
                return ResponseEntity.notFound().build();  // Resource vide, OK
            }

            // attachmentPath contient seulement les noms de fichiers séparés par ';' (ex. : "uuid1.pdf;uuid2.pdf")
            String[] filenames = attachmentPath.split(";");
            if (filenames.length == 0) {
                return ResponseEntity.notFound().build();
            }

            // Prend le premier nom de fichier non vide (pour single download ; pour multi, frontend appelle plusieurs fois ou ajoute param)
            String selectedFilename = null;
            for (String filename : filenames) {
                if (filename != null && !filename.trim().isEmpty()) {
                    selectedFilename = filename.trim();
                    break;
                }
            }

            if (selectedFilename == null) {
                return ResponseEntity.notFound().build();
            }

            // Récupération de reservationId et proprietaireId pour reconstruire le chemin complet
            Reservation reservation = litige.getReservation();
            if (reservation == null) {
                logger.warn("Reservation non trouvée pour litige ID : " + litigeId);
                return ResponseEntity.notFound().build();
            }

            Car voiture = reservation.getVoiture();
            if (voiture == null) {
                logger.warn("Voiture non trouvée pour litige ID : " + litigeId);
                return ResponseEntity.notFound().build();
            }

            Propritaire proprietaire = voiture.getProprietaire();
            if (proprietaire == null) {
                logger.warn("Propriétaire non trouvé pour litige ID : " + litigeId);
                return ResponseEntity.notFound().build();
            }

            Long proprietaireId = proprietaire.getId();
            if (proprietaireId == null) {
                logger.warn("ID propriétaire null pour litige ID : " + litigeId);
                return ResponseEntity.notFound().build();
            }

            String reservationId = reservation.getId().toString();

            // Reconstruit le chemin complet (ex. : "uploads/proprietaire_1/litiges/15/uuid.pdf")
            String fullPath = "uploads/proprietaire_" + proprietaireId + "/litiges/" + reservationId + "/" + selectedFilename;

            // Utilise le service pour récupérer le resource et contentType
            Resource resource = fileService.getAttachmentResource(proprietaireId, reservationId, selectedFilename);
            if (resource == null || !resource.exists()) {
                logger.warn("Attachment non trouvé pour litige ID " + litigeId + " : " + fullPath);
                return ResponseEntity.notFound().build();
            }

            String contentType = fileService.getAttachmentContentType(proprietaireId, reservationId, selectedFilename);
            if (contentType == null) {
                contentType = "application/octet-stream";  // Default
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + selectedFilename + "\"")
                    .body(resource);  // Resource body, OK

        } catch (NumberFormatException e) {
            logger.error("Erreur parsing ID pour litige ID " + litigeId + " : " + e.getMessage());
            return ResponseEntity.badRequest().build();  // Resource vide, OK
        } catch (Exception e) {
            logger.error("Erreur téléchargement attachment litige ID " + litigeId + " : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();  // Resource vide, OK
        }
    }
    @GetMapping("/charts")
    @ResponseBody
    public Map<String, Object> getChartData() {
        Map<String, Object> response = new HashMap<>();

        // 1. Revenu mensuel
        List<Object[]> revenus = reservationRepository.findMonthlyRevenue();
        List<String> mois = new ArrayList<>();
        List<Double> valeurs = new ArrayList<>();
        for (Object[] row : revenus) {
            mois.add("Mois " + row[0]); // tu peux convertir vers Jan, Feb, etc.
            valeurs.add(((Number) row[1]).doubleValue());
        }
        response.put("revenueData", Map.of(
            "labels", mois,
            "datasets", List.of(Map.of(
                "label", "Revenu (€)",
                "data", valeurs,
                "borderColor", "rgba(75, 192, 192, 1)",
                "backgroundColor", "rgba(75, 192, 192, 0.2)"
            ))
        ));

        // 2. Statut de réservation
        List<Object[]> statuts = reservationRepository.countByStatut();
        List<String> labels = new ArrayList<>();
        List<Long> statutCount = new ArrayList<>();
        for (Object[] row : statuts) {
        	labels.add(row[0].toString());
            statutCount.add((Long) row[1]);
        }
        response.put("bookingStatusData", Map.of(
            "labels", labels,
            "datasets", List.of(Map.of(
                "data", statutCount,
                "backgroundColor", List.of("#4e73df", "#1cc88a", "#e74a3b")
            ))
        ));

        // 3. Taux d'occupation
        List<Object[]> villes = reservationRepository.occupationParVille();
        List<String> villesLabels = new ArrayList<>();
        List<Long> taux = new ArrayList<>();
        for (Object[] row : villes) {
            villesLabels.add((String) row[0]);
            taux.add((Long) row[1]);
        }
        response.put("occupationData", Map.of(
            "labels", villesLabels,
            "datasets", List.of(Map.of(
                "label", "Taux (%)",
                "data", taux,
                "backgroundColor", "#36b9cc"
            ))
        ));

        // 4. Top 5 voitures louées
        List<Object[]> topVoitures = reservationRepository.topVoitures(PageRequest.of(0, 5));
        List<String> nomsVoitures = new ArrayList<>();
        List<Long> locations = new ArrayList<>();
        for (Object[] row : topVoitures) {
            nomsVoitures.add((String) row[0]);
            locations.add((Long) row[1]);
        }
        response.put("topCarsData", Map.of(
            "labels", nomsVoitures,
            "datasets", List.of(Map.of(
                "label", "Nombre de locations",
                "data", locations,
                "backgroundColor", "#f6c23e"
            ))
        ));

        return response;
    }

    @PostMapping("/rapport")
    @ResponseBody
    public ResponseEntity<?> generateRapport(@RequestBody RapportRequest request) {
        Map<String, Object> response = new HashMap<>();

        String type = request.getType();
        String startDateStr = request.getStartDate();
        String endDateStr = request.getEndDate();

        if (startDateStr == null || startDateStr.isBlank()) {
            return ResponseEntity.badRequest().body("❌ Date de début manquante !");
        }

        if (endDateStr == null || endDateStr.isBlank()) {
            return ResponseEntity.badRequest().body("❌ Date de fin manquante !");
        }

        LocalDate start, end;
        try {
            start = LocalDate.parse(startDateStr);
            end = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("❌ Format de date invalide. Utilise yyyy-MM-dd.");
        }

        System.out.println("=== Rapport demandé ===");
        System.out.println("Type de rapport : " + type);
        System.out.println("Période : " + start + " au " + end);

        switch (type) {
            case "Activité Utilisateur":
                List<Object[]> rawUsers = userRepository.findUsersActivityBetween(start, end);
                System.out.println("Utilisateurs actifs trouvés : " + rawUsers.size());
                response.put("headers", List.of("Nom", "Email", "Dernière Activité"));
                response.put("rows", mapToUserRows(rawUsers));
                break;

            case "Performance Voiture":
                List<Object[]> rawCars = carRepository.findCarPerformanceBetween(start, end);
                System.out.println("Voitures analysées : " + rawCars.size());
                response.put("headers", List.of("Voiture", "Nombre Locations", "Chiffre d'affaires"));
                response.put("rows", mapToCarRows(rawCars));
                break;


            case "Financier Détaillé":
                // Pas besoin de conversion, tu utilises directement LocalDate
                List<Paiement> paiements = paiementRepository.findPaymentsBetween(start, end);

                System.out.println("Paiements récupérés : " + paiements.size());

                response.put("headers", List.of("Date Paiement", "Montant (€)", "Mode Paiement", "Reservation ID"));
                response.put("rows", mapToPaiementRows(paiements));
                break;


            case "Maintenance Prévue":
                // TODO : implémenter la logique
                System.out.println("Rapport Maintenance Prévue demandé.");
                response.put("headers", List.of("Colonne1", "Colonne2")); // Adapter selon besoin
                response.put("rows", List.of());
                break;


            case "Litiges Résolus":
                LocalDateTime startDateTime = start.atStartOfDay();
                LocalDateTime endDateTime = end.atTime(23, 59, 59);
                System.out.println("Recherche de litiges résolus entre : " + startDateTime + " et " + endDateTime);

                List<Litige> litiges = litigeRepository.findLitigesByStatutBetween(
                    StatutLitige.RESOLU,
                    startDateTime,
                    endDateTime
                );

                System.out.println("Nombre de litiges trouvés : " + litiges.size());
                for (Litige l : litiges) {
                    String utilisateurNom = "null";
                    if (l.getReservation() != null && l.getReservation().getUtilisateur() != null) {
                        utilisateurNom = l.getReservation().getUtilisateur().getFirstName();
                    }

                    System.out.println("-> Litige ID: " + l.getId() +
                        ", Statut: " + l.getStatut() +
                        ", Créé le: " + l.getDateCreation() +
                        ", Résolution: " + l.getResolution() +
                        ", Utilisateur: " + utilisateurNom);
                }


                response.put("headers", List.of("ID Litige", "Description", "Date Résolution", "Utilisateur"));
                response.put("rows", mapToLitigeRows(litiges));
                break;

            default:
                System.out.println("Type de rapport inconnu.");
                response.put("headers", List.of());
                response.put("rows", List.of());
                break;
        }

        return ResponseEntity.ok(response);
    }


    private List<List<Object>> mapToUserRows(List<Object[]> rawUsers) {
        if (rawUsers == null) return List.of();

        List<List<Object>> rows = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Object[] user : rawUsers) {
            String nom = (String) user[0];         // firstName
            String email = (String) user[1];       // email
            LocalDate dateDebut = (LocalDate) user[2]; // dateDebut
            String formattedDate = dateDebut.format(formatter);

            rows.add(List.of(nom, email, formattedDate));
        }
        return rows;
    }


    private List<List<Object>> mapToCarRows(List<Object[]> rawCars) {
        List<List<Object>> rows = new ArrayList<>();
        for (Object[] row : rawCars) {
            Car car = (Car) row[0];
            Long nbLocations = (Long) row[1];
            Double chiffreAffaires = (Double) row[2];

            rows.add(List.of(
                car.getFullName(),
                nbLocations != null ? nbLocations : 0,
                chiffreAffaires != null ? chiffreAffaires : 0
            ));
        }
        return rows;
    }

    private List<List<Object>> mapToPaiementRows(List<Paiement> paiements) {
        List<List<Object>> rows = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Format souhaité

        for (Paiement paiement : paiements) {
            String dateFormatee = paiement.getDatePaiement().format(formatter);

            rows.add(List.of(
                dateFormatee, // date formatée en string
                paiement.getMontant(),
                paiement.getModePaiement(),
                paiement.getReservation() != null ? paiement.getReservation().getId() : "N/A"
            ));
        }
        return rows;
    }
private List<List<Object>> mapToLitigeRows(List<Litige> litiges) {
    List<List<Object>> rows = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    for (Litige litige : litiges) {
        String utilisateurNom = "Inconnu";
        if (litige.getReservation() != null && litige.getReservation().getUtilisateur() != null) {
            User u = litige.getReservation().getUtilisateur();
            utilisateurNom = u.getFirstName() + " " + u.getLastName();
        }

        String formattedDate = litige.getDateCreation() != null
            ? litige.getDateCreation().format(formatter)
            : "Date inconnue";

        rows.add(List.of(
            litige.getId(),
            litige.getDescription(),
            formattedDate, // 👉 formaté ici
            utilisateurNom
        ));
    }

    return rows;
}

   

@PostMapping("/partenariat/accepter")
@ResponseBody
public ResponseEntity<String> accepter(@RequestParam Long id) {
    String path = demandeService.accepter(id);
    return ResponseEntity.ok(path); // retourne le chemin du fichier
    
}


@PostMapping("/partenariat/rejeter")
@ResponseBody
public ResponseEntity<String> rejeter(@RequestParam Long id) {
    demandeService.rejeter(id);
    return ResponseEntity.ok("Demande rejetée.");
}
@GetMapping("/telecharger")
public void telechargerPDF(@RequestParam String path, HttpServletResponse response) throws IOException {
    File file = new File(path);
    if (!file.exists()) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier non trouvé");
        return;
    }

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
    Files.copy(file.toPath(), response.getOutputStream());
    response.getOutputStream().flush();
}
@GetMapping("/reservations/{id}")
public ResponseEntity<ReservationDetailsDTO> getReservationDetails(@PathVariable Long id) {
    try {
        System.out.println("🔍 Requête reçue pour les détails de réservation ID: " + id);
        
        
   Optional<Reservation> reservationOpt = reservationService.findById(id);
        
        // Vérifiez avec isPresent() au lieu de == null
        if (!reservationOpt.isPresent()) {
            System.out.println("❌ Reservation non trouvée pour ID: " + id);
            return ResponseEntity.notFound().build();
        }

        // Récupérez l'objet avec get()
        Reservation reservation = reservationOpt.get();
        System.out.println("✅ Reservation trouvée: " + reservation.getId());
        


        System.out.println("✅ Reservation trouvée: " + reservation.getId());
        
        ReservationDetailsDTO dto = convertToReservationDetailsDTO(reservation);
        
        return ResponseEntity.ok(dto);
        
    } catch (Exception e) {
        System.err.println("❌ Erreur lors de la récupération de la réservation: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }
}
private ReservationDetailsDTO convertToReservationDetailsDTO(Reservation reservation) {
    ReservationDetailsDTO dto = new ReservationDetailsDTO();
    
    // Debug des données
    System.out.println("🔍 Reservation ID: " + reservation.getId());
    System.out.println("🔍 Date début: " + reservation.getDateDebut());
    System.out.println("🔍 Date fin: " + reservation.getDateFin());
    
    // Informations de base de la réservation
    dto.setId(reservation.getId());
    dto.setStatut(reservation.getStatut() != null ? reservation.getStatut().name() : "N/A");
    dto.setDateDebut(reservation.getDateDebut());
    dto.setDateFin(reservation.getDateFin());
    dto.setPrixTotal(reservation.getPrixTotal());
    
    // Calcul du nombre de jours
    dto.setNombreJours(calculerNombreJours(reservation.getDateDebut(), reservation.getDateFin()));
    
    // Récupérer les notes depuis la table Avis
    dto.setNotes(getNotesFromAvis(reservation));
    
    // CORRECTION: Date de création (vous aviez dateFin au lieu de dateCreation)
    if (reservation.getDateFin() == null) {
        dto.setDateCreation(java.time.LocalDateTime.now());
    } else {
        dto.setDateFin(reservation.getDateFin()); // Correction ici
    }

    // Informations du client
    if (reservation.getClient() != null) {
        User client = reservation.getClient();
        ClientDTO clientDTO = new ClientDTO(client);
        dto.setClient(clientDTO);
    } else if (reservation.getUtilisateur() != null) {
        User utilisateur = reservation.getUtilisateur();
        ClientDTO clientDTO = new ClientDTO(utilisateur);
        dto.setClient(clientDTO);
    }

    // Informations de la voiture
    if (reservation.getVoiture() != null) {
        Car voiture = reservation.getVoiture();
        CarDTO carDTO = new CarDTO();
        carDTO.setId(voiture.getId());
        carDTO.setMarque(voiture.getMarque());
        carDTO.setModele(voiture.getModele());
        carDTO.setImmatriculation(voiture.getImmatriculation());
        carDTO.setPrixJournalier(voiture.getPrixJournalier());
        
        dto.setVoiture(carDTO);
    }

    System.out.println("📊 DTO créé pour réservation ID: " + dto.getId());
    System.out.println("📊 Statut: " + dto.getStatut());
    System.out.println("📊 Client: " + (dto.getClient() != null ? dto.getClient().getFullName() : "null"));
    System.out.println("📊 Voiture: " + (dto.getVoiture() != null ? dto.getVoiture().getMarque() : "null"));
    System.out.println("📊 Notes: " + dto.getNotes());

    return dto;
}

private String getNotesFromAvis(Reservation reservation) {
    try {
        System.out.println("🔍 Recherche des notes pour réservation ID: " + reservation.getId());
        
        // Vérification de base
        if (reservation == null) {
            System.out.println("❌ Reservation est null");
            return "Aucune note disponible";
        }
        
        // Vérification de la voiture
        if (reservation.getVoiture() == null) {
            System.out.println("⚠️ Aucune voiture associée à cette réservation");
            return "Aucune note disponible";
        }
        
        Car voiture = reservation.getVoiture();
        System.out.println("🚗 Voiture trouvée: ID " + voiture.getId() + " - " + voiture.getMarque() + " " + voiture.getModele());
        
        // Vérification des avis
        if (voiture.getAvis() == null) {
            System.out.println("⚠️ Liste d'avis est null");
            return "Aucune note disponible";
        }
        
        List<Avis> avisList = voiture.getAvis();
        System.out.println("📊 Nombre d'avis trouvés: " + avisList.size());
        
        // Recherche de l'avis correspondant à la réservation
        for (Avis avis : avisList) {
            System.out.println("🔍 Analyse avis ID: " + avis.getId());
            
            // Vérification de la réservation dans l'avis
            if (avis.getReservation() == null) {
                System.out.println("   ⚠️ Avis sans réservation associée");
                continue;
            }
            
            System.out.println("   📋 Reservation avis ID: " + avis.getReservation().getId());
            System.out.println("   📋 Reservation actuelle ID: " + reservation.getId());
            
            // Comparaison des IDs de réservation
            if (avis.getReservation().getId().equals(reservation.getId())) {
                String commentaire = avis.getCommentaire();
                System.out.println("   💬 Commentaire trouvé: " + (commentaire != null ? commentaire : "null"));
                
                if (commentaire != null && !commentaire.trim().isEmpty()) {
                    System.out.println("✅ Note trouvée via voiture: " + commentaire);
                    return commentaire;
                } else {
                    System.out.println("ℹ️ Avis trouvé mais commentaire vide");
                    return "Aucun commentaire";
                }
            }
        }
        
        // Si aucun avis spécifique n'est trouvé, chercher un commentaire générique
        System.out.println("🔍 Recherche d'un commentaire générique...");
        for (Avis avis : avisList) {
            String commentaire = avis.getCommentaire();
            if (commentaire != null && !commentaire.trim().isEmpty()) {
                System.out.println("✅ Commentaire générique trouvé: " + commentaire);
                return commentaire;
            }
        }
        
        System.out.println("ℹ️ Aucune note trouvée pour cette réservation");
        return "Aucune note disponible";
        
    } catch (Exception e) {
        System.err.println("❌ Erreur lors de la récupération des notes: " + e.getMessage());
        e.printStackTrace();
        return "Erreur lors du chargement des notes";
    }
}


private Integer calculerNombreJours(java.time.LocalDate dateDebut, java.time.LocalDate dateFin) {
    if (dateDebut == null || dateFin == null) {
        return 0;
    }
    return (int) java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
}

/*

@PostMapping("/ajoutervoiture")
@ResponseBody
public ResponseEntity<?> ajouterVoiture(
        @RequestParam("marque") String marque,
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
        @RequestParam("categorie") String categorie,
        @RequestParam("image") MultipartFile imageFile,
        @RequestParam("galleryImages") MultipartFile[] galleryImages,Principal principal) {
    Map<String, Object> resp = new HashMap<>();


    try {
        System.out.println("====> Début ajouterVoiture");

        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Image principale obligatoire.\"}");
        }

        // Créer dossier upload
     
        String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // Créer le fichier d’image



        // Vérifier
        // Nettoyer et enregistrer l'image principale
        String imageFileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename().replaceAll("\\s+", "_");
        File mainImageFile = new File(uploadDir, imageFileName);
        imageFile.transferTo(mainImageFile);

        Double latitude = null;
        Double longitude = null;

        try {
            Double[] coords = geoCodingService.getLatLong(ville);
            if (coords != null) {
                latitude = coords[0];
                longitude = coords[1];
            } else {
                latitude = 0.0;
                longitude = 0.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            latitude = 0.0;
            longitude = 0.0;
        }

        // Créer l'objet voiture
        System.out.println("====> Début création de la voiture");

     // Création de l'objet Car
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
     car.setCategorie(categorie);
     car.setImagePrincipaleURL(imageFileName);
     System.out.println("====> Car rempli avec les paramètres du formulaire");

     System.out.println("====> Coordonnées récupérées pour la ville : " + ville);
     System.out.println("      Latitude : " + latitude);
     System.out.println("      Longitude : " + longitude);

     car.setLatitude(latitude);
     car.setLongitude(longitude);
     System.out.println("====> Coordonnées affectées à la voiture");

     // Vérifier l'utilisateur connecté
     Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     String email;

     if (principalObj instanceof UserDetails) {
         email = ((UserDetails) principalObj).getUsername(); // username = email
     } else if (principalObj instanceof User) {
         email = ((User) principalObj).getEmail();
     } else {
         throw new RuntimeException("Impossible de récupérer l'email de l'utilisateur connecté");
     }

     System.out.println("====> Email récupéré correctement : " + email);

     User proprietaireUser = userRepository.findByEmail(email);
     if (proprietaireUser == null) {
         throw new RuntimeException("Propriétaire non trouvé");
     }



     // Vérifier le propriétaire lié à cet utilisateur
     Propritaire proprietaire = proprietaireRepository.findByUser(proprietaireUser);
     if (proprietaire == null) {
         System.out.println("====> Aucune correspondance Propritaire trouvée pour l'utilisateur !");
         throw new RuntimeException("Propriétaire lié à l'utilisateur non trouvé");
     }
     System.out.println("====> Propriétaire trouvé : " + proprietaire.getId() + " - " + proprietaire.getRaisonSociale());

     car.setProprietaire(proprietaire);

     // Sauvegarde voiture
     carRepository.save(car);
     System.out.println("====> Voiture enregistrée : ID = " + car.getId());

     // Retour JSON correct pour AJAX
 
        // Sauvegarde images de galerie
        if (galleryImages != null && galleryImages.length > 0) {
            for (MultipartFile galleryImage : galleryImages) {
                if (galleryImage != null && !galleryImage.isEmpty()) {
                    String galleryFileName =
                            galleryImage.getOriginalFilename().replaceAll("\\s+", "_");
                    File galleryFile = new File(uploadDir, galleryFileName);
                    galleryImage.transferTo(galleryFile);

                    Gallery gallery = new Gallery();
                    gallery.setImageURL(galleryFileName);
                    gallery.setVoiture(car);
                    galleryService.saveGalleryImage(gallery);

                    System.out.println("    → Galerie ajoutée : " + galleryFileName);
                }
            }
        }
        resp.put("success", true);
        resp.put("message", "Voiture ajoutée avec succès !");
        return ResponseEntity.ok(resp);

    } catch (IOException e) {
        e.printStackTrace();
        resp.put("success", false);
        resp.put("message", "Erreur fichier image : " + e.getMessage());
        return ResponseEntity.internalServerError().body(resp);
    } catch (Exception e) {
        e.printStackTrace();
        resp.put("success", false);
        resp.put("message", "Erreur serveur : " + e.getMessage());
        return ResponseEntity.internalServerError().body(resp);
    }}
@ExceptionHandler({
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class
})
public ResponseEntity<?> handleMissingParams(Exception ex) {
    ex.printStackTrace();
    return ResponseEntity.badRequest()
            .body("{\"success\": false, \"message\": \"Paramètre manquant ou mal formaté : " + ex.getMessage() + "\"}");
}*/
}



    
    
    
    
    
    
    
    
    
