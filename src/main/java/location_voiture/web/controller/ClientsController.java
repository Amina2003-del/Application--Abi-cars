package location_voiture.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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
import org.springframework.web.server.ResponseStatusException;

import com.itextpdf.text.DocumentException;

import location_voiture.persistence.dto.AvisRequestDTO;
import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.dto.ClientDTO;
import location_voiture.persistence.dto.LitigeDTO;
import location_voiture.persistence.dto.MessageDTO;
import location_voiture.persistence.dto.PasswordChangeDTO;
import location_voiture.persistence.dto.ReservationCreateDTO;
import location_voiture.persistence.dto.ReservationRequestDTO;
import location_voiture.persistence.dto.UserDTO;
import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.CarNotAvailableException;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Litige;
import location_voiture.persistence.model.Message;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.RoleUtilisateur;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutLitige;
import location_voiture.persistence.model.StatutReservation;
import location_voiture.persistence.model.TypeLitige;
import location_voiture.persistence.model.TypeMessage;
import location_voiture.repository.AvisRepository;
import location_voiture.repository.CarRepository;
import location_voiture.repository.FactureRepository;
import location_voiture.repository.LitigeRepository;
import location_voiture.repository.MessageRepository;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.ReservationRepository;
import location_voiture.service.AvisService;
import location_voiture.service.CarService;
import location_voiture.service.FactureService;
import location_voiture.service.LitigeService;
import location_voiture.service.MessageService;
import location_voiture.service.PaiementService;
import location_voiture.service.PdfFactureGenerator;
import location_voiture.service.ReservationService;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.CustomUserDetails;
import ma.abisoft.service.MailClient;
import ma.abisoft.service.UserService;

@Controller
@RequestMapping("/Clientes")
public class ClientsController {

    private final Logger logger = LoggerFactory.getLogger(ClientsController.class);
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PdfFactureGenerator pdfFactureGenerator;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FactureRepository factureRepository; // Renamed to FacturesRepository

    @Autowired
    private AvisRepository avisRepository;

    @Autowired
    private LitigeRepository litigeRepository;

    @Autowired
    private UserService utilisateurService;

    @Autowired
    private LitigeService litigeService;
    @Autowired
    private FactureService factureService;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PaiementRepository paiementRepository;

    
    @Autowired
    private PaiementService paiementService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private CarService carService;
    @Autowired
    private CarService voitureService;
    @Autowired
    private AvisService avisService;
    @Autowired
    private MailClient mailClient;

    @Autowired
    private ReservationRepository reservationRepository;


    // Tableau de bord
    @GetMapping("/tableaubordclient")
    public String afficherTableauBordClient(Model model, Principal principal) {
    	
        if (principal == null) {
            return "redirect:/login";
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "error";
        }

        List<Reservation> reservations = reservationRepository.findByUtilisateur_Id(user.getId());

        List<Litige> disputes = litigeRepository.findByReservationIn(reservations);
        List<Facture> invoices = factureRepository.findByClient(user);
        List<Message> messages = messageRepository.findByDestinataireId(user.getId());
        List<Avis> reviews = avisRepository.findByAuteur(user);
    	

        long activeReservations = reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .count();
        double totalSpent = reservations.stream()
                .mapToDouble(Reservation::getPrixTotal)
                .sum();
        long totalReviews = reviews.size();
        long unreadMessages = messages.stream().filter(m -> !m.isLu()).count();

        // Données pour le graphique des réservations par mois
        Map<String, Long> reservationsByMonth = reservations.stream()
                .filter(r -> r.getDateDebut() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getDateDebut().getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH),
                        Collectors.counting()
                ));

        // Ensure all months are included
        List<String> monthLabels = Arrays.asList(
                "janv.", "févr.", "mars", "avr.", "mai", "juin",
                "juil.", "août", "sept.", "oct.", "nov.", "déc."
        );
        Map<String, Long> completeReservationsByMonth = new LinkedHashMap<>();
        monthLabels.forEach(month -> completeReservationsByMonth.put(month, reservationsByMonth.getOrDefault(month, 0L)));

        DecimalFormat df = new DecimalFormat("#.00");
        model.addAttribute("user", user);
        model.addAttribute("reservations", reservations);
        model.addAttribute("reviews", reviews);
        model.addAttribute("disputes", disputes);
        model.addAttribute("invoices", invoices);
        model.addAttribute("messages", messages);
        model.addAttribute("activeReservations", activeReservations);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("notificationsCount", unreadMessages);
        model.addAttribute("lastReservation", reservations.isEmpty() ? null : reservations.get(0));
        model.addAttribute("dashboardMessage", "Profitez de -15% sur votre prochaine location avec le code WINTER2025 !");
        model.addAttribute("currentLanguage", "fr");
        model.addAttribute("reservationsByMonth", completeReservationsByMonth);
        model.addAttribute("formattedTotalSpent", df.format(totalSpent));

        return "client_dashboard";
    }
    
    private void sendEmailWithLogo(String to, String subject, String[] lines, String logoPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("contact@aladintours.com");  // <== ajoute bien l'expéditeur ici
        helper.setTo(to);
        helper.setSubject(subject);

        // Construire le corps HTML
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        for (String line : lines) {
            sb.append("<p>").append(line).append("</p>");
        }
        sb.append("<img src='cid:logoImage' alt='Logo'/>");
        sb.append("</body></html>");

        helper.setText(sb.toString(), true);

        // Ajouter l'image inline
        FileSystemResource res = new FileSystemResource(new File(logoPath));
        helper.addInline("logoImage", res);

        mailSender.send(message);
    }
    

    // Rechercher et réserver
    @GetMapping("/rechercher-reserver")
    public String afficherRechercherReserver(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            System.err.println("Utilisateur non authentifié !");
            return "redirect:/login";
        }

        User user = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            user = (User) principal;
            System.out.println("Utilisateur connecté : " + user.getEmail());
        } else {
            System.err.println("Principal n'est pas une instance de User");
            return "redirect:/login";
        }

        // Chargement des données générales
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        model.addAttribute("request", new ReservationRequestDTO());
        model.addAttribute("activeReservations", reservationService.getActiveReservations());
        model.addAttribute("totalSpent", paiementService.getTotalSpent());
        model.addAttribute("totalReviews", avisService.getTotalReviews());
        model.addAttribute("reservationsByMonth", reservationService.getReservationsGroupedByMonth());
        model.addAttribute("dashboardMessage", "Bienvenue sur votre tableau de bord !");
        model.addAttribute("section", "rechercher");

        Optional<Reservation> lastReservation = reservationService.getLastReservation();
        model.addAttribute("lastReservation", lastReservation.orElse(null));

        // Si l'utilisateur est bien authentifié
        if (user != null) {
            List<Facture> invoicees = factureRepository.findByClient(user);
            List<Reservation> userReservations = reservationService.findByUtilisateur(user);
            System.out.println("Reservations trouvées : " + userReservations.size());
            model.addAttribute("reservations", userReservations);
            model.addAttribute("userEmail", user.getEmail());

            List<Message> userMessages = messageService.getMessagesForUser(user);
            System.out.println("Messages trouvés : " + userMessages.size());
            userMessages.forEach(m -> System.out.println("Sujet : " + m.getSujet()));

            // Litiges et avis liés à l'utilisateur
            List<Litige> litiges = litigeService.getLitigesByUtilisateur(user);
            List<Avis> reviews = avisRepository.findByAuteur(user);
            model.addAttribute("reviews", reviews);
            model.addAttribute("disputes", litiges);
            model.addAttribute("utilisateur", user);
            Map<Long, Double> montantMap = new HashMap<>();
            for (Facture invoice : invoicees) {
                Double montant = null;
                if (invoice.getReservation() != null) {
                    Paiement paiement = paiementRepository.findOptionalByReservationId(invoice.getReservation().getId()).orElse(null);
                    if (paiement != null) {
                        montant = paiement.getMontant();
                    }
                }
                montantMap.put(invoice.getId(), montant);
            }
            model.addAttribute("montantMap", montantMap);

            // 🔽 Factures liées à l'utilisateur
            List<Facture> invoices = factureRepository.findByClient(user);
            model.addAttribute("invoices", invoices);
            model.addAttribute("section", "invoices");
        }
        model.addAttribute("litigesParMois", litigeService.getLitigesParMois());
        List<Message> messages = messageRepository.findByExpediteur(user);
        
        System.out.println("Nombre de messages envoyés: " + messages.size());
        
        // Debug détaillé
        for (Message msg : messages) {
            System.out.println("📤 Message ID: " + msg.getId());
            System.out.println("   Sujet: " + msg.getSujet());
            System.out.println("   Destinataire: " + (msg.getDestinataire() != null ? 
                msg.getDestinataire().getDisplayName() + " (ID: " + msg.getDestinataire().getId() + ")" : "NULL"));
            System.out.println("   Date: " + msg.getDateEnvoi());
            System.out.println("   Contenu: " + (msg.getContent() != null ? 
                msg.getContent().substring(0, Math.min(msg.getContent().length(), 30)) + "..." : "NULL"));
        }

        model.addAttribute("messages", messages);
        model.addAttribute("section", "messages");
        return "Clientes/rechercher-reserver";
    }

    @GetMapping("/messages/details/{id}")
    @ResponseBody
    public ResponseEntity<?> getMessageDetails(
        @PathVariable Long id,
        @AuthenticationPrincipal User user) {

        System.out.println("Début getMessageDetails pour id = " + id);

        if (user == null) {
            System.out.println("Utilisateur non authentifié (user=null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non autorisé : utilisateur non connecté");
        }
        System.out.println("Utilisateur connecté : " + user.getEmail() + " (id=" + user.getId() + ")");

        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isEmpty()) {
            System.out.println("Message introuvable pour id = " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message non trouvé avec l'id : " + id);
        }

        Message message = messageOpt.get();

        if (!message.getDestinataire().getId().equals(user.getId())) {
            System.out.println("Accès refusé : utilisateur " + user.getId() + " ne possède pas le message " + id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé : ce message ne vous appartient pas");
        }

        System.out.println("Message trouvé et appartenant à l'utilisateur, préparation de la réponse...");

        Map<String, Object> result = new HashMap<>();
        result.put("sujet", message.getSujet());
        result.put("expediteur", message.getExpediteur() != null ? message.getExpediteur().getDisplayName() : "Inconnu");

        Object dateObj = message.getDateEnvoi();
        if (dateObj == null) {
            System.out.println("Date d'envoi du message est NULL");
            result.put("dateEnvoiFormatted", "Date non disponible");
        } else if (dateObj instanceof java.time.LocalDateTime) {
            java.time.LocalDateTime ldt = (java.time.LocalDateTime) dateObj;
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            result.put("dateEnvoiFormatted", ldt.format(formatter));
            System.out.println("Date formatée (LocalDateTime) : " + ldt.format(formatter));
        } else if (dateObj instanceof java.util.Date) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            result.put("dateEnvoiFormatted", sdf.format((java.util.Date) dateObj));
            System.out.println("Date formatée (Date) : " + sdf.format((java.util.Date) dateObj));
        } else {
            System.out.println("Type de date inattendu : " + dateObj.getClass());
            result.put("dateEnvoiFormatted", dateObj.toString());
        }

        result.put("content", message.getContent());

        System.out.println("Réponse prête : " + result);

        return ResponseEntity.ok(result);
    }
   
    @GetMapping("/api/client/profile")
    @ResponseBody
    public ResponseEntity<UserDTO> getClientProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;

            // Convertir User en UserDTO
            UserDTO userDto = new UserDTO();
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setTelephone(user.getTel());

            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/api/messages/{id}")
    public ResponseEntity<MessageDTO> getMessagesById(@PathVariable Long id) {
        Message message = messageService.findById(id);
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with id: " + id);
        }
        return ResponseEntity.ok(new MessageDTO(message));
    }


    @PostMapping("/api/client/profile")
    @ResponseBody
    public ResponseEntity<?> updateClientProfile(@RequestBody UserDTO userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) authentication.getPrincipal();

        // Mettre à jour les champs modifiables
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setTel(userDto.getTelephone());

        // Sauvegarder user en base (assure-toi que userService.save(user) existe)
        utilisateurService.save(user);

        return ResponseEntity.ok(Map.of("message", "Profil mis à jour avec succès"));
    }
    @GetMapping("/test-session")
    @ResponseBody
    public String testSession(Principal principal) {
        return (principal == null) ? "Non connecté" : "Connecté : " + principal.getName();
    }


    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<String> sendMessage(
            @RequestParam("sujet") String sujet,
            @RequestParam("contenu") String contenu,
            @RequestParam(value = "reservationId", required = false) Long reservationId,
            HttpServletRequest request) {

        System.out.println(">>> [DEBUG] Entrée dans /Clientes/send");
        System.out.println(">>> [DEBUG] Cookie JSESSIONID reçu : " + request.getHeader("Cookie"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        User client = (User) authentication.getPrincipal();
        System.out.println(">>> [DEBUG] Utilisateur authentifié : " + client.getEmail());

        if (reservationId == null) {
            return ResponseEntity.badRequest().body("Veuillez sélectionner une réservation valide");
        }

        Optional<Reservation> optReservation = reservationService.findById(reservationId);
        if (optReservation.isEmpty()) {
            return ResponseEntity.badRequest().body("Reservation non trouvée");
        }

        Reservation reservation = optReservation.get();
        Car car = reservation.getVoiture();
        if (car == null) {
            return ResponseEntity.badRequest().body("Voiture non trouvée dans la réservation");
        }

        try {
            Propritaire proprietaire = car.getProprietaire();
            if (proprietaire == null || proprietaire.getUser() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Propriétaire introuvable");
            }

            User userProprietaire = proprietaire.getUser();

            Message msg = new Message();
            msg.setSujet(sujet);
            msg.setContent(contenu);
            msg.setExpediteur(client);
            msg.setDestinataire(userProprietaire);  // Utiliser le User associé
            msg.setDateEnvoi(LocalDateTime.now());
            msg.setTypeMessage(TypeMessage.INTERNE);
            msg.setReservation(reservation);

            messageService.save(msg);

            System.out.println(">>> [DEBUG] Message enregistré : " + msg.getId());

            // Email au client
          /*  SimpleMailMessage mailToClient = new SimpleMailMessage();
            mailToClient.setTo(client.getEmail());
            mailToClient.setSubject("📨 Votre message a été bien envoyé !");
            mailToClient.setText("Bonjour " + client.getEmail() + ",\n\n"
                    + "Nous avons bien reçu votre message :\nSujet : " + sujet + "\n\n"
                    + "Merci de votre confiance.\n\nRentCar");
            mailClient.prepareAndSend(mailToClient);

            // Email au propriétaire
            if (proprietaire.getUser().getEmail() != null) {
                SimpleMailMessage mailToOwner = new SimpleMailMessage();
                mailToOwner.setTo(proprietaire.getUser().getEmail());
                mailToOwner.setSubject("🚗 Nouveau message pour votre voiture !");
                mailToOwner.setText("Bonjour " + proprietaire.getUser().getEmail()
 + ",\n\n"
                        + "Vous avez reçu un message concernant votre voiture : " + car.getMarque() + "\n"
                        + "Sujet : " + sujet + "\n\n"
                        + "Connectez-vous à votre espace pour y répondre.\n\nRentCar");
                mailClient.prepareAndSend(mailToOwner);
            }*/

            return ResponseEntity.ok("Votre message a été envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erreur lors de l'envoi du message");
        }
    }
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseEntity<Message> getMessageDetail(@PathVariable Long id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<?> searchCars(@Valid @RequestBody ReservationRequestDTO request, BindingResult bindingResult) {
        System.out.println("[POST /search] Requête reçue : " + request);

        if (bindingResult.hasErrors()) {
            String errorMsg = "Erreurs de validation : " + bindingResult.getAllErrors();
            System.out.println(errorMsg);
            return ResponseEntity.badRequest().body(new SearchResponse(errorMsg, null));
        }

        if (request.getDateDebut() == null || request.getDateFin() == null || request.getTypeVoiture() == null) {
            String errorMsg = "Veuillez fournir les dates et le type de voiture.";
            System.out.println(errorMsg);
            return ResponseEntity.badRequest().body(new SearchResponse(errorMsg, null));
        }

        if (request.getDateDebut().isAfter(request.getDateFin())) {
            String errorMsg = "La date de début doit être antérieure à la date de fin.";
            System.out.println(errorMsg);
            return ResponseEntity.badRequest().body(new SearchResponse(errorMsg, null));
        }

        try {
            List<Car> cars = reservationService.searchAvailableCars(request);
            System.out.println("[POST /search] Nombre de voitures trouvées : " + cars.size());
            return ResponseEntity.ok().body(new SearchResponse(cars.isEmpty() ? "Aucune voiture disponible pour ces critères." : null, cars));
        } catch (Exception e) {
            String errorMsg = "Erreur lors de la recherche : " + e.getMessage();
            System.out.println(errorMsg);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SearchResponse(errorMsg, null));
        }
    }

    // Classe pour structurer la réponse JSON
    public static class SearchResponse {
        private String message;
        private List<Car> cars;

        public SearchResponse(String message, List<Car> cars) {
            this.message = message;
            this.cars = cars;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<Car> getCars() { return cars; }
        public void setCars(List<Car> cars) { this.cars = cars; }
    }
    // Reservations
    @GetMapping("/reservations")
    public String afficherReservations(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "error";
        }

        List<Reservation> reservations = reservationRepository.findByUtilisateur_Id(user.getId());
        model.addAttribute("reservations", reservations);
        model.addAttribute("section", "reservations");
        return "client_dashboard";
    }

    // Statistiques du tableau de bord
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Long> getDashboardStats(Principal principal) {
        logger.info("Requête reçue : /Clientes/stats");

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return new HashMap<>();
        }

        long activeReservations = reservationRepository.countByStatutAndUtilisateur_Id(StatutReservation.CONFIRMEE, user.getId());
        long unreadMessages = messageRepository.countByDestinataireIdAndLuFalse(user.getId());
        long pendingInvoices = factureRepository.countByClientAndStatut(user, "EN_ATTENTE");
        long paidInvoices = factureRepository.countByClientAndStatut(user, "PAYEE");

        Map<String, Long> stats = new HashMap<>();
        stats.put("activeReservations", activeReservations);
        stats.put("unreadMessages", unreadMessages);
        stats.put("pendingInvoices", pendingInvoices);
        stats.put("paidInvoices", paidInvoices);
        return stats;
    }

    // Activités récentes
    @GetMapping("/recent-activities")
    @ResponseBody
    public List<Map<String, String>> getRecentActivities(Principal principal) {
        logger.info("Chargement des activités récentes");

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return new ArrayList<>();
        }

        List<Map<String, String>> activities = new ArrayList<>();

        List<Reservation> reservations = reservationRepository.findByUtilisateur_Id(user.getId());
        for (Reservation r : reservations) {
            Map<String, String> activity = new HashMap<>();
            activity.put("type", "Reservation");
            String detail = r.getVoiture() != null
                    ? r.getVoiture().getModele() + " - #" + r.getId()
                    : "Voiture non spécifiée";
            String statut = (r.getStatut() != null) ? r.getStatut().name() : "Statut inconnu";
            activity.put("detail", detail);
            activity.put("date", r.getDateDebut() != null ? r.getDateDebut().toString() : "Date inconnue");
            activity.put("status", statut);
            activities.add(activity);
        }

        List<Message> messages = messageRepository.findByDestinataireIdWithExpediteur(user.getId());
        for (Message m : messages) {
            Map<String, String> activity = new HashMap<>();
            activity.put("type", "Message");
            activity.put("detail", "Réponse du support - Ticket #" + m.getId());
            activity.put("date", m.getDateEnvoi() != null ? m.getDateEnvoi().toString() : "Date inconnue");
            activity.put("status", m.isLu() ? "Lu" : "Non lu");
            activities.add(activity);
        }

        logger.info("Activités chargées : {}", activities.size());
        return activities.subList(0, Math.min(activities.size(), 5));
    }

    // Historique des réservations
    @GetMapping("/{id}/reservations")
    @ResponseBody
    public ResponseEntity<List<Reservation>> getHistoriqueReservations(@PathVariable Long id) {
        List<Reservation> reservations = reservationRepository.findByUtilisateur_Id(id);
        if (reservations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservations);
    }

    // Reservations du client (API)
    @GetMapping("/client/reservations")
    @ResponseBody
    public ResponseEntity<?> getClientReservations(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        List<Reservation> reservations = reservationRepository.findByUtilisateur_Id(user.getId());
        List<Map<String, Object>> result = reservations.stream().map(res -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", res.getId());
            map.put("voiture", res.getVoiture() != null
                    ? res.getVoiture().getMarque() + " " + res.getVoiture().getModele()
                    : "Voiture inconnue");
            map.put("dateDebut", res.getDateDebut());
            map.put("dateFin", res.getDateFin());
            map.put("prixTotal", res.getPrixTotal());
            map.put("statut", res.getStatut() != null ? res.getStatut().name() : "INCONNU");
            map.put("adressePriseEnCharge", res.getAdressePriseEnCharge());
            map.put("adresseRestitution", res.getAdresseRestitution());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Détails d’une réservation
    @GetMapping("/client/reservations/{id}")
    @ResponseBody
    public ResponseEntity<?> getReservationDetails(@PathVariable Long id, Principal principal) {
        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null || !reservation.getUtilisateur().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation non trouvée");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", reservation.getId());
        result.put("voiture", reservation.getVoiture() != null
                ? reservation.getVoiture().getMarque() + " " + reservation.getVoiture().getModele()
                : "Voiture inconnue");
        result.put("dateDebut", reservation.getDateDebut());
        result.put("dateFin", reservation.getDateFin());
        result.put("prixTotal", reservation.getPrixTotal());
        result.put("statut", reservation.getStatut() != null ? reservation.getStatut().name() : "INCONNU");
        result.put("adressePriseEnCharge", reservation.getAdressePriseEnCharge());
        result.put("adresseRestitution", reservation.getAdresseRestitution());

        System.out.println("Détails réservation envoyés : " + result);

        return ResponseEntity.ok(result);
    }


    // Nouvelle réservation
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

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(dateDebut, formatter);
            LocalDate end = LocalDate.parse(dateFin, formatter);

            if (end.isBefore(start)) {
                return ResponseEntity.badRequest().body("La date de fin doit être après la date de début.");
            }
            List<Car> availableCars = null;

         // List<Car> availableCars = carRepository.findByDisponible(Car.ETAT_DISPONIBLE);
            // Filtrer par type de voiture (simplifié, à affiner selon votre logique)
            availableCars = availableCars.stream()
                    .filter(car -> car.getType().equalsIgnoreCase(typeVoiture))
                    .collect(Collectors.toList());

            if (availableCars.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune voiture disponible pour ces critères.");
            }

           Map<String, Object> response = new HashMap<>();
            response.put("cars", availableCars.stream().map(car -> {
                Map<String, Object> carMap = new HashMap<>();
                carMap.put("id", car.getId());
                carMap.put("modele", car.getModele());
                carMap.put("prixJournalier", car.getPrixJournalier());
                return carMap;
            }).collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Format de date invalide.");
        }
    }

 
    // Rechercher des voitures
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
          //  voitures = carRepository.findByDisponible(Car.ETAT_DISPONIBLE);
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

    // Messages
   /* @GetMapping("/messages")
    public String afficherMessages(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "error";
        }

        List<Message> messages = messageRepository.findByDestinataireId(user.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("section", "messages");
        return "client_dashboard";
    }

  */
   
    // Avis
    @GetMapping("/client/reviews")
    public String afficherAvis(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "error";
        }

        List<Avis> reviews = avisRepository.findByAuteur(user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("section", "reviews");
        return "client_dashboard";
    }

    @PostMapping("/client/reviews/submit")
    @ResponseBody
    public ResponseEntity<?> submitReview(
            @RequestParam Long reservationId,
            @RequestParam Integer note,
            @RequestParam(required = false) String commentaire,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null || !reservation.getUtilisateur().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation non trouvée ou non associée à l'utilisateur");
        }

        // Vérification basique de la note (1 à 5)
        if (note == null || note < 1 || note > 5) {
            return ResponseEntity.badRequest().body("Note invalide. Doit être entre 1 et 5.");
        }

        Avis avis = new Avis();
        avis.setReservation(reservation);          // Je recommande de stocker l'objet Reservation, pas juste son ID
        avis.setVoiture(reservation.getVoiture());
        avis.setAuteur(user);
        avis.setNote(note);
        avis.setCommentaire(commentaire != null ? commentaire.trim() : null);
        avis.setDate(LocalDate.now());

        avisRepository.save(avis);

        return ResponseEntity.ok("Avis soumis avec succès");
    }

    // Litiges
    @GetMapping("/client/disputes")
    public String afficherLitiges(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "error";
        }

        List<Reservation> reservations = reservationRepository.findByUtilisateur_Id(user.getId());
        List<Litige> disputes = litigeRepository.findByReservationIn(reservations);
        model.addAttribute("disputes", disputes);
        model.addAttribute("section", "disputes");
        return "client_dashboard";
    }

    @GetMapping("/client/disputes/{id}")
    @ResponseBody
    public ResponseEntity<?> getDisputeDetails(@PathVariable Long id, Principal principal) {
        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        Litige litige = litigeRepository.findById(id).orElse(null);
        if (litige == null || !litige.getReservation().getUtilisateur().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Litige non trouvé");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", litige.getId());
        result.put("reservation", litige.getReservation() != null ? litige.getReservation().getId() : null);
        result.put("type", litige.getType());
        result.put("statut", litige.getStatut());
        result.put("description", litige.getDescription());
        result.put("resolution", litige.getResolution());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submitDispute(
            @RequestParam Long reservationId,
            @RequestParam TypeLitige type,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile[] attachments,
            Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Non authentifié"));
        }

        User userFromToken = (User) authentication.getPrincipal();
        User user = utilisateurService.findByEmail(userFromToken.getEmail().toLowerCase());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Utilisateur introuvable"));
        }

        try {
            Litige litige = litigeService.creerLitige(reservationId, type, description, attachments, user.getId());
            String emailProprietaire = null;

            if (litige != null && litige.getReservation() != null) {
                Car voiture = litige.getReservation().getVoiture();
                if (voiture != null && voiture.getProprietaire() != null) {
                    Propritaire proprietaire = voiture.getProprietaire();
                    User userProprietaire = proprietaire.getUser(); // ← important !
                    
                  //  Message message = new Message();
                   // message.setSujet("Litige sur la réservation #" + litige.getReservation().getId()); // Sujet obligatoire
                   // message.setContent("Litige ouvert pour la réservation #" + litige.getReservation().getId()
                    //    + " concernant votre voiture " + voiture.getModele()
                      //  + ". Description : " + description);
                   // message.setDestinataire(userProprietaire);
                   // message.setType(TypeMessage.ALERTE);
                   // message.setLu(false);
                   // message.setDateEnvoi(LocalDateTime.now());

                    //messageRepository.save(message);

                   // emailProprietaire = userProprietaire.getEmail(); 
                }
                
            }

         /*   System.out.println("Envoi de l'email à : " + emailProprietaire);

            String emailClient = user.getEmail();

            sendEmailWithLogo(emailClient,
                    "Confirmation de réception de votre litige",
                    new String[]{
                            "Bonjour,",
                            "Votre litige a bien été reçu et sera traité rapidement.",
                            "Cordialement."
                    },
                    "src/main/resources/static/img/logo.png");

            if (emailProprietaire != null && !emailProprietaire.isEmpty()) {
                sendEmailWithLogo(emailProprietaire,
                        "Nouveau litige concernant votre voiture",
                        new String[]{
                                "Bonjour,",
                                "Un nouveau litige a été signalé sur votre voiture.",
                                "Merci de vérifier votre espace propriétaire."
                        },
                        "src/main/resources/static/img/logo.png");
            }*/

            return ResponseEntity.ok(Map.of("success", true, "message", "Litige soumis avec succès"));

        } catch (MessagingException me) {
            me.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur en envoyant l'email : " + me.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur : " + e.getMessage()));
        }
    }

    // Factures
   

    @GetMapping("/client/invoices/{id}")
    @ResponseBody
    public ResponseEntity<?> getInvoiceDetails(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            System.out.println("[DEBUG] Principal est NULL => Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        String email = null;

        if (principal instanceof Authentication) {
            Object principalObj = ((Authentication) principal).getPrincipal();
            System.out.println("[DEBUG] Authentication.principal.class = " + principalObj.getClass().getName());

            if (principalObj instanceof UserDetails) {
                email = ((UserDetails) principalObj).getUsername();
                System.out.println("[DEBUG] Email extrait via UserDetails : " + email);
            } 
            else if (principalObj instanceof String) {
                email = (String) principalObj;
                System.out.println("[DEBUG] Email extrait via String principal : " + email);
            } 
            else if (principalObj instanceof User) {
                // Ici, tu cast directement ta classe User perso
                email = ((User) principalObj).getEmail();
                System.out.println("[DEBUG] Email extrait via User (classe perso) : " + email);
            }
            else {
                System.out.println("[DEBUG] Type de principal non géré : " + principalObj.getClass().getName());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
            }
        } else {
            email = principal.getName();
            System.out.println("[DEBUG] Email extrait via principal.getName() : " + email);
        }
    
        // Reste du code inchangé...
        User user = utilisateurService.findByEmail(email);
        if (user == null) {
            System.out.println("[DEBUG] Aucun utilisateur trouvé avec l'email : " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        Facture invoice = factureRepository.findById(id).orElse(null);
        if (invoice == null) {
            System.out.println("[DEBUG] Facture non trouvée pour id : " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Facture non trouvée");
        }
        if (!invoice.getClient().getId().equals(user.getId())) {
            System.out.println("[DEBUG] Facture n'appartient pas à l'utilisateur. Facture client id : "
                + invoice.getClient().getId() + " Utilisateur id : " + user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Facture non trouvée");
        }
        Double montant = null;
        if (invoice.getReservation() != null) {
            Paiement paiement = paiementRepository.findOptionalByReservationId(invoice.getReservation().getId()).orElse(null);
            if (paiement != null) {
                montant = paiement.getMontant();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", invoice.getId());
        result.put("reservation", invoice.getReservation() != null ? invoice.getReservation().getId() : null);
        result.put("montant", montant);
        result.put("dateEmission", invoice.getDateEmission());
        result.put("statut", invoice.getStatut());
        result.put("modePaiement", invoice.getModePaiement());

        System.out.println("[DEBUG] Envoi des détails de la facture : " + result);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFacturePdf(@PathVariable Long id) {
        logger.info("Début téléchargement facture, id = {}", id);

        Optional<Facture> factureOpt = factureService.findById(id);
        if (factureOpt.isEmpty()) {
            logger.warn("Aucune facture trouvée avec l'id {}", id);
            return ResponseEntity.notFound().build();
        }

        Facture facture = factureOpt.get();

        User currentUser = utilisateurService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("Aucun utilisateur connecté (currentUser == null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info("Utilisateur connecté : id = {}", currentUser.getId());

        User client = facture.getClient();
        if (client == null) {
            logger.error("Facture id={} : client est null", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        logger.info("Client facture id={} : clientId = {}", id, client.getId());

        if (!client.getId().equals(currentUser.getId())) {
            logger.warn("L'utilisateur id={} tente d'accéder à une facture id={} qui ne lui appartient pas", currentUser.getId(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] pdfBytes;
        try {
            pdfBytes = pdfFactureGenerator.genererFacturePDF(facture);
            facture.setFacturePdf(pdfBytes);
            factureService.save(facture);
            logger.info("Facture id={} régénérée et mise à jour avec succès", id);
        } catch (DocumentException e) {
            logger.error("Erreur lors de la régénération du PDF pour la facture id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "attachment; filename=\"facture-" + id + "_" + System.currentTimeMillis() + ".pdf\"");
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        logger.info("Téléchargement facture id={} prêt, taille PDF = {} octets", id, pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // Profil
    @GetMapping("/client/profile")
    public String afficherProfil(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "error";
        }

        model.addAttribute("user", user);
        model.addAttribute("section", "profile");
        return "client_dashboard";
    }
   
    @PostMapping("/client/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String tel,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setTel(tel);
        userRepository.save(user);

        return ResponseEntity.ok("Profil mis à jour avec succès");
    }

    @PostMapping("/client/profile/password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        User user = utilisateurService.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        // Vérifier le mot de passe actuel (simplifié, utiliser BCrypt dans une implémentation réelle)
        if (!user.getPassword().equals(currentPassword)) {
            return ResponseEntity.badRequest().body("Mot de passe actuel incorrect");
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Les nouveaux mots de passe ne correspondent pas");
        }

        user.setPassword(newPassword); // À encoder avec BCrypt en production
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe changé avec succès");
    }

    // Changement de langue
    @GetMapping("/set-language")
    @ResponseBody
    public ResponseEntity<?> setLanguage(@RequestParam String lang) {
        logger.info("Changement de langue : {}", lang);
        return ResponseEntity.ok("Langue changée : " + lang);
    }
    
   
    
    
    @GetMapping(value = "/Client/litige/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLitigeById(@PathVariable Long id) {
        Optional<Litige> optional = litigeService.getLitigeById(id);
        if (optional.isPresent()) {
            LitigeDTO dto = new LitigeDTO(optional.get());
            return ResponseEntity.ok(dto);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Litige introuvable");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    @GetMapping(value = "/Client/message/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMessageById(@PathVariable Long id) {
        Optional<Message> optional = messageService.getMessageById(id);
        if (optional.isPresent()) {
            Message message = optional.get();
            // Convertir en DTO pour éviter les problèmes de sérialisation
            MessageDTO dto = new MessageDTO(message);
            return ResponseEntity.ok(dto);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Message introuvable");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getReservationDetails(@PathVariable Long id) {
        System.out.println("🔍 [GET] /Clientes/" + id);

        Optional<Reservation> opt = reservationService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("❌ Reservation introuvable pour l'id : " + id);
            return ResponseEntity.notFound().build();
        }

        Reservation res = opt.get();

        try {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", res.getId());
            dto.put("voitureModele", res.getVoiture().getModele());
            dto.put("dateDebut", res.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            dto.put("dateFin", res.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            dto.put("prixTotal", res.getPrixTotal());
            dto.put("statut", res.getStatut());
            dto.put("adressePriseEnCharge", res.getAdressePriseEnCharge());
            dto.put("adresseRestitution", res.getAdresseRestitution());

            System.out.println("✅ Reservation chargée : " + dto);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println("🚨 Erreur lors de la création du DTO : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne");
        }
    }

    
    @PostMapping("/api/client/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        User user = (User) auth.getPrincipal();

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Mot de passe actuel incorrect");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Les nouveaux mots de passe ne correspondent pas");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
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
        // Méthode alternative : header "Content-Disposition" sous forme string
        headers.add("Content-Disposition", "attachment; filename=\"facture_" + id + ".pdf\"");

        return new ResponseEntity<>(facture.getFacturePdf(), headers, HttpStatus.OK);
    }



    @PostMapping("/avis/submit")
    public ResponseEntity<?> enregistrerAvis(@RequestBody AvisRequestDTO avisDTO, Authentication authentication) {
        System.out.println("==> Méthode enregistrerAvis appelée !");
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        String email = null;
        Object principalObj = authentication.getPrincipal();

        if (principalObj instanceof String) {
            email = (String) principalObj;  // souvent "anonymousUser" si pas connecté
        } else if (principalObj instanceof User) {
            email = ((User) principalObj).getEmail();  // Si ton User custom est utilisé
        }

        System.out.println("Utilisateur connecté (email) : " + email);

        if (email == null || email.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non connecté");
        }

        User auteur = userRepository.findByEmail(email);
        if (auteur == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non connecté");
        }

        // Reste de ton code...
        Optional<Reservation> optionalRes = reservationRepository.findById(avisDTO.getReservationId());
        if (optionalRes.isEmpty()) {
            return ResponseEntity.badRequest().body("Reservation non trouvée");
        }

        Reservation reservation = optionalRes.get();
        Car voiture = reservation.getVoiture();

        Avis avis = new Avis();
        avis.setCommentaire(avisDTO.getCommentaire());
        avis.setNote(avisDTO.getNote());
        avis.setDate(LocalDate.now());
        avis.setReservation(reservation);
        avis.setVoiture(voiture);
        avis.setAuteur(auteur);
        avis.setUtilisateur(auteur);


        System.out.println("==> Tentative d'enregistrement de l'avis : " + avis);
        Avis savedAvis = avisRepository.save(avis);
        System.out.println("==> Avis enregistré avec ID : " + savedAvis.getId());

        return ResponseEntity.ok("Avis inséré avec succès !");
    }


    @GetMapping("/Clients/initiate-reservation")
    public String initiateReservation(@RequestParam("id") Long carId, HttpSession session, Principal principal) {
        // Si l'utilisateur est déjà connecté
        if (principal != null) {
            // On stocke l'ID de la voiture en session pour la prochaine page
            session.setAttribute("selectedCarId", carId);

            // Redirige vers l'espace client, section réservation
            return "redirect:/Clientes/rechercher-reserver";
        } else {
            // Si pas connecté, on stocke aussi l'ID temporairement
            session.setAttribute("selectedCarId", carId);

            // Redirige vers la page de login
            return "redirect:/login";
        
        }}

}