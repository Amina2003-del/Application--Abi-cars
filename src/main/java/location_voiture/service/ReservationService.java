package location_voiture.service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import location_voiture.event.ReservationEvent;
import location_voiture.persistence.dto.ReservationCreateDTO;
import location_voiture.persistence.dto.ReservationDTO;
import location_voiture.persistence.dto.ReservationRequest;
import location_voiture.persistence.dto.ReservationRequestDTO;
import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.CarNotAvailableException;
import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Paiement;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.Réservation;
import location_voiture.persistence.model.StatutPaiement;
import location_voiture.persistence.model.StatutReservation;
import location_voiture.persistence.model.TypeAlert;
import location_voiture.repository.AvisRepository;
import location_voiture.repository.CarRepository;
import location_voiture.repository.FactureRepository;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.ReservationRepository;
import location_voiture.web.controller.ClientsController;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;

@Service
public class ReservationService {
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private AlertService alertService; // ou le nom réel de ton service de gestion des alertes

    @Autowired
    private AvisRepository avisRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;
    @Autowired private FactureRepository factureRepo;

    @Autowired
    private CarService carService;
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    @Autowired
    private PaiementRepository paiementRepository; // Ajout de l'injection

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, AvisRepository avisRepository) {
        this.reservationRepository = reservationRepository;
        this.avisRepository = avisRepository;
    }

    public Réservation saveReservation(Réservation reservation) {
        return reservationRepository.save(reservation);
    }
    public Map<Integer, Long> getReservationsGroupedByMonth() {
        List<Object[]> rawData = reservationRepository.countReservationsGroupedByMonth();
        Map<Integer, Long> result = new TreeMap<>();
        for (Object[] row : rawData) {
            Integer month = (Integer) row[0];
            Long count = (Long) row[1];
            result.put(month, count);
        }
        return result;
    }


    public Avis saveAvis(Avis avis) {
        return avisRepository.save(avis);
    }

    public Optional<Réservation> findById(Long id) {
        return reservationRepository.findById(id);
    }
    public long getActiveReservations() {
        List<StatutReservation> activeStatuses = List.of(
            StatutReservation.ACTIVE,
            StatutReservation.CONFIRMEE,
            StatutReservation.EN_COURS
        );
        return reservationRepository.countByStatutIn(activeStatuses);
    }

    /**
     * Enregistre une nouvelle réservation avec prix total et statut initial
     */
    public Réservation enregistrerReservation(Réservation reservation, Long voitureId, Long locataireId) {
        Car voiture = carService.getCarById(voitureId);
        Locataire locataire = new Locataire();
        reservation.setLocataire((Locataire) locataire);
        reservation.setVoiture(voiture);
        reservation.setLocataire(locataire);

        long daysBetween = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        double prixTotal = voiture.getPrixJournalier() * Math.max(daysBetween, 1); // éviter 0

        reservation.setPrixTotal(prixTotal);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        return reservationRepository.save(reservation);
    }

    /**
     * Réserve une voiture pour un utilisateur donné si elle est disponible
     */
    public String reserverVoiture(Long voitureId, LocalDate debut, LocalDate fin, User utilisateur) {
        Car voiture = carRepository.findById(voitureId)
                .orElseThrow(() -> new IllegalArgumentException("Voiture introuvable"));

        if (!isDisponiblePourPeriode(voiture, debut, fin)) {
            return "Voiture déjà réservée sur cette période";
        }

        Réservation reservation = new Réservation();
        reservation.setVoiture(voiture);
        reservation.setDateDebut(debut);
        reservation.setDateFin(fin);
        reservation.setUser(utilisateur);
        reservation.setCar(voiture);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        long jours = ChronoUnit.DAYS.between(debut, fin);
        reservation.setPrixTotal(voiture.getPrixJournalier() * Math.max(jours, 1));

        reservationRepository.save(reservation);
        return "Réservation effectuée avec succès";
    }

    /**
     * Vérifie si une voiture est disponible pour une période donnée
     */
    private boolean isDisponiblePourPeriode(Car voiture, LocalDate debut, LocalDate fin) {
        List<Réservation> reservationsExistantes = reservationRepository.findByVoiture(voiture.getId());

        for (Réservation r : reservationsExistantes) {
            if (r.getDateDebut().isBefore(fin) && r.getDateFin().isAfter(debut)) {
                return false; // Chevauchement de dates
            }
        }
        return true;
    }

    public Réservation reserver(Réservation reservation) {
        // TODO Auto-generated method stub
        return null;
    }

    public Réservation save(Réservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Long countReservationsByMonth(int month) {
        LocalDate start = LocalDate.of(LocalDate.now().getYear(), month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return reservationRepository.countByMonth(start, end);
    }

    public List<ReservationDTO> getAllReservationDTOs() {
        List<Réservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(r -> {
            User u = r.getUtilisateur();
            String nomClient = u.getFirstName() + " " + u.getLastName();
            String voiture = r.getVoiture().getMarque() + " " + r.getVoiture().getModele();
            return new ReservationDTO();
        }).collect(Collectors.toList());
    }

    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                                   .map(ReservationDTO::fromEntity) // transforme chaque Réservation en DTO complet
                                   .collect(Collectors.toList());
    }
   

    public List<Map<String, Object>> getDynamicReservations() {
        List<Map<String, Object>> reservations = new ArrayList<>();

        List<Réservation> reservationList = reservationRepository.findAll();

        for (Réservation reservation : reservationList) {
            Map<String, Object> reservationData = new HashMap<>();

            reservationData.put("id", reservation.getId());

            Car car = reservation.getVoiture();
            Map<String, String> carData = new HashMap<>();
            if (car != null) {
                carData.put("modele", car.getFullName());
                carData.put("immatriculation", car.getImmatriculation());
                } else {
                carData.put("modele", "Inconnu");
                carData.put("immatriculation", "N/A");
            }
            reservationData.put("car", carData);

            User client = reservation.getUtilisateur();
            Map<String, String> clientData = new HashMap<>();
            if (client != null) {
                clientData.put("firstName", client.getFirstName() != null ? client.getFirstName() : "Inconnu");
                clientData.put("lastName", client.getLastName() != null ? client.getLastName() : "");
                clientData.put("email", client.getEmail() != null ? client.getEmail() : "N/A");
                clientData.put("NumeroPermis", "N/A");
            } else {
                clientData.put("firstName", "Inconnu");
                clientData.put("lastName", "");
                clientData.put("email", "N/A");
                clientData.put("NumeroPermis", "N/A");
            }
            reservationData.put("client", clientData);

            reservationData.put("DateDebut", reservation.getDateDebut() != null ? reservation.getDateDebut().toString() : null);
            reservationData.put("DateFin", reservation.getDateFin() != null ? reservation.getDateFin().toString() : null);

            reservationData.put("statut", reservation.getStatut() != null ? reservation.getStatut().toString() : "EN_ATTENTE");

            // Correction : Utilisation de l'instance injectée
            Paiement paiement = paiementRepository.findByReservationId(reservation.getId()); // Correct
            if (paiement != null) {
                reservationData.put("statut", paiement.getStatut() != null ? paiement.getStatut().toString() : "EN_ATTENTE");
                reservationData.put("methode", paiement.getMethode() != null ? paiement.getMethode() : "Inconnu");
                reservationData.put("amount", paiement.getMontant() != null ? paiement.getMontant() : 0.0);
            } else {
                reservationData.put("statut", "EN_ATTENTE");
                reservationData.put("methode", "Inconnu");
                reservationData.put("amount", 0.0);
            }

            reservations.add(reservationData);
        }

        return reservations;
    }

	public boolean modifyReservation(Long id, Réservation reservation) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean refuseReservation(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean confirmReservation(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	public Réservation getReservationById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Réservation> getReservationsWithFilter(String status, String clientName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long countActiveReservations() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Map<String, Object>> getAllBookings() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getUserProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> getDisputes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getBookingById(String bookingId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> getAlerts() {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelBooking(String bookingId) {
		// TODO Auto-generated method stub
		
	}

	public void addComment(String bookingId, String comment, Integer rating) {
		// TODO Auto-generated method stub
		
	}
	public List<Car> searchAvailableCars(ReservationRequestDTO request) {
        System.out.println("[searchAvailableCars] Début avec requête : " + request);
        if (request == null || request.getDateDebut() == null || request.getDateFin() == null || request.getTypeVoiture() == null) {
            System.out.println("[searchAvailableCars] Entrées nulles détectées");
            return new ArrayList<>();
        }

        if (request.getDateDebut().isAfter(request.getDateFin())) {
            System.out.println("[searchAvailableCars] Date de début postérieure à date de fin");
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin.");
        }

        List<Car> availableCars = carRepository.findAvailableCars(
            request.getTypeVoiture(),
            request.getDateDebut(),
            request.getDateFin()
        );
        System.out.println("[searchAvailableCars] Voitures trouvées : " + availableCars.size());

        if (request.getAdressePriseEnCharge() != null) {
            availableCars = availableCars.stream()
                .filter(car -> isCarAvailableAtLocation(car, request.getAdressePriseEnCharge()))
                .collect(Collectors.toList());
            System.out.println("[searchAvailableCars] Voitures après filtrage par adresse : " + availableCars.size());
        }

        return availableCars;
    }

    private boolean isCarAvailableAtLocation(Car car, String adressePriseEnCharge) {
        System.out.println("[isCarAvailableAtLocation] Vérification pour voiture " + car.getId() + " à " + adressePriseEnCharge);
        return true; // Placeholder
    }
	
	
	
	
    @Transactional
    public Réservation createReservation(ReservationCreateDTO request) {
        logger.info("Début de la création de la réservation pour l'email : {}", request.getEmail());

        // 1. Validation des dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            logger.error("Date de début {} postérieure à la date de fin {}", request.getStartDate(), request.getEndDate());
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin.");
        }

        // 2. Vérification des conflits de réservation
        List<Réservation> conflits = reservationRepository.findConflictingReservations(
                request.getCarId(), request.getStartDate(), request.getEndDate());
        if (!conflits.isEmpty()) {
            logger.error("Conflit de réservation détecté pour la voiture ID {} aux dates {}-{}", 
                    request.getCarId(), request.getStartDate(), request.getEndDate());
            throw new IllegalArgumentException("La voiture n'est pas disponible pour ces dates.");
        }

        // 3. Vérification de l'existence de la voiture
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> {
                    logger.error("Voiture ID {} introuvable", request.getCarId());
                    return new IllegalArgumentException("Voiture introuvable.");
                });
        logger.info("Voiture trouvée : {}", car.getModele());

        // 4. Gestion de l'utilisateur
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            logger.info("Utilisateur avec email {} non trouvé, création d'un nouvel utilisateur", request.getEmail());
            user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setTel(request.getPhone());
            user = userRepository.save(user);
            logger.info("Nouvel utilisateur créé avec ID : {}", user.getId());
        } else {
            logger.info("Utilisateur existant trouvé avec ID : {}", user.getId());
        }

        // 5. Création de la réservation
        Réservation reservation = new Réservation();
        reservation.setVoiture(car);
        reservation.setUtilisateur(user);
        reservation.setDateDebut(request.getStartDate());
        reservation.setDateFin(request.getEndDate());
        reservation.setPickupAddress(request.getPickupAddress());
        reservation.setReturnAddress(request.getReturnAddress());
        reservation.setDateReservation(LocalDate.now());
        reservation.setTypeReservation(request.getTypeReservation());
        // 6. Calcul du prix total
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) {
            logger.error("Durée de réservation invalide : {} jours", days);
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }
        double prixTotal = days * car.getPrixJournalier();
        reservation.setPrixTotale(prixTotal);
        logger.info("Prix total calculé : {} pour {} jours", prixTotal, days);

        // 7. Gestion des statuts selon le paiement
        StatutReservation statutReservation;
        StatutPaiement statutPaiement;

        if ("paypal".equalsIgnoreCase(request.getPaymentMethod())) {
            statutReservation = StatutReservation.CONFIRMEE;
            statutPaiement = StatutPaiement.PAYE;
            logger.info("Paiement PayPal détecté, statut : CONFIRMEE/PAYE");
        } else if ("virement".equalsIgnoreCase(request.getPaymentMethod())) {
            statutReservation = StatutReservation.CONFIRMEE;
            statutPaiement = StatutPaiement.PAYE;
            logger.info("Paiement par virement détecté, statut : CONFIRMEE/PAYE");
        } else {
            statutReservation = StatutReservation.EN_ATTENTE;
            statutPaiement = StatutPaiement.EN_ATTENTE;
            logger.info("Paiement en attente, méthode : {}", request.getPaymentMethod());
        }

        reservation.setStatut(statutReservation);

        // 8. Enregistrement de la réservation
        reservation = reservationRepository.save(reservation);
        logger.info("Réservation enregistrée avec ID : {}", reservation.getId());

        // 9. Création du paiement lié
        Paiement paiement = new Paiement();
        paiement.setMontant(prixTotal);
        paiement.setPaymentMethod(request.getPaymentMethod());
        paiement.setReservation(reservation);
        paiement.setStatut(statutPaiement);
        paiementRepository.save(paiement);
        logger.info("Paiement enregistré pour la réservation ID : {}", reservation.getId());

        // 10. Création de l'alerte pour le client
        try {
            String sujet = "Réservation enregistrée";
            String message = String.format("Votre réservation pour la voiture %s du %s au %s a bien été enregistrée.",
                    car.getModel(), request.getStartDate(), request.getEndDate());
            alertService.createNotification(sujet, message, TypeAlert.RESERVATION, user, false);
            logger.info("Notification client créée pour l'utilisateur ID : {}", user.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification client :", e);
        }

        // 11. Création de l'alerte pour le propriétaire
        try {
            Propritaire proprietaire = car.getProprietaire();
            if (proprietaire != null && proprietaire.getUser() != null) {
                User userProprietaire = proprietaire.getUser();

                String sujetProp = "Nouvelle réservation reçue";
                String msgProp = String.format("Votre voiture %s a été réservée du %s au %s par %s %s.",
                        car.getModele(), request.getStartDate(), request.getEndDate(),
                        user.getFirstName(), user.getLastName());

                alertService.createNotification(sujetProp, msgProp, TypeAlert.RESERVATION, userProprietaire, false);
                logger.info("Notification propriétaire créée pour l'utilisateur ID : {}", userProprietaire.getId());
            } else {
                logger.warn("Aucun propriétaire associé à la voiture ID : {}", car.getId());
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification propriétaire :", e);
        }

        // 12. Publication de l'événement
        try {
            eventPublisher.publishEvent(new ReservationEvent(reservation));
            logger.info("Événement ReservationEvent publié pour la réservation ID : {}", reservation.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la publication de l'événement ReservationEvent :", e);
            throw new RuntimeException("Échec de la publication de l'événement de réservation.", e);
        }

        return reservation;
        
    }
   
	public void updateUserProfile(String lastName, String firstName, String tel, String password,
			String confirmPassword) {
		// TODO Auto-generated method stub
		
	}
	@Transactional
	public Réservation reserverEtPayer(ReservationRequest request) {
	    // Création réservation (simplifié)
	    Réservation reservation = new Réservation();
	    // Remplir les infos de réservation...
	    reservationRepository.save(reservation);

	    // Création paiement
	    Paiement paiement = new Paiement(request.getMontant(), request.getMethode(), reservation);
	    paiementRepository.save(paiement);

	    // Confirmer réservation (optionnel)
	    reservation.setStatut(StatutReservation.CONFIRMEE);
	    reservationRepository.save(reservation);

	    return reservation;
	}
	
	
	

	public Map<String, Object> submitDispute(String bookingId, String subject, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	public Optional<Réservation> getLastReservation() {
	    return reservationRepository.findTopByOrderByDateDebutDesc();
	}


    public List<Réservation> findByUtilisateur(User utilisateur) {
        return reservationRepository.findByUtilisateur(utilisateur);
    }

	public List<Réservation> getByUserId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ReservationDTO> findByCurrentUser() {
	    // 1. Récupérer l'utilisateur connecté
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    String username;
	    if (principal instanceof UserDetails) {
	        username = ((UserDetails) principal).getUsername();
	    } else {
	        username = principal.toString();
	    }
	    
	    // 2. Trouver l'utilisateur en base (via UserRepository)
	    User user = userRepository.findByEmail(username);
	    if (user == null) {
	        return Collections.emptyList();
	    }

	    // 3. Récupérer les réservations associées à cet utilisateur
	    List<Réservation> reservations = reservationRepository.findByUtilisateur(user);
;

	    // 4. Convertir les réservations en DTO
	    List<ReservationDTO> result = new ArrayList<>();
	    for (Réservation res : reservations) {
	        String nomClient = user.getFullName(); // ou méthode adaptée
	        String marqueModele = res.getVoiture().getMarque() + " " + res.getVoiture().getModele();
	        LocalDate dateDebut = res.getDateDebut();
	        LocalDate dateFin = res.getDateFin();
	        double prix = res.getPrixTotal() != null ? res.getPrixTotal() : 0;
	        String statut = res.getStatut().name();

	        ReservationDTO dto = new ReservationDTO();
	        dto.setDateFin(dateFin);
	        dto.setPrixtotale(prix);
	        dto.setStatut(statut);

	        result.add(dto);
	    }
	    return result;
	}
	
	public List<ReservationDTO> findByOwner() {
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    String email;

	    if (principal instanceof User) {
	        email = ((User) principal).getEmail();
	    } else if (principal instanceof UserDetails) {
	        email = ((UserDetails) principal).getUsername();
	    } else {
	        email = principal.toString(); // Fallback
	    }

	    System.out.println("🔍 Authenticated user email: " + email);
	  


	    User owner = userRepository.findByEmail(email);
	    System.out.println("🔍 Recherche d'utilisateur avec email: " + email);
	    System.out.println("✅ Résultat findByEmail: " + owner);
	    if (owner == null) {
	        System.out.println("❌ Utilisateur non trouvé dans la base de données !");
	        return Collections.emptyList();
	    }
	    System.out.println("✅ Utilisateur trouvé: " + owner.getFullName());

	    boolean isOwner = owner.getRoles().stream()
	        .anyMatch(role -> role.getName().equals("ROLE_OWNER"));
	    System.out.println("🔒 L'utilisateur est OWNER ? " + isOwner);

	    if (!isOwner) {
	        System.out.println("⚠️ Utilisateur n'est pas propriétaire, retour liste vide.");
	        return Collections.emptyList();
	    }

	    List<Réservation> reservations = reservationRepository.findByVoitureProprietaireEmail(email);
	    System.out.println("📦 Nombre de réservations récupérées: " + (reservations != null ? reservations.size() : "null"));

	    List<ReservationDTO> result = new ArrayList<>();
	    for (Réservation res : reservations) {
	        if (res.getUtilisateur() == null || res.getVoiture() == null) {
	            continue;
	        }

	        String nomClient = res.getUtilisateur().getFullName();
	        String marqueModele = res.getVoiture().getMarque() + " " + res.getVoiture().getModele();
	        LocalDate dateDebut = res.getDateDebut();
	        LocalDate dateFin = res.getDateFin();
	        double prix = res.getPrixTotal() != null ? res.getPrixTotal() : 0;
	        String statut = res.getStatut() != null ? res.getStatut().name() : "INCONNU";

	        ReservationDTO dto = new ReservationDTO();
	        dto.setDateFin(dateFin);
	        dto.setPrixtotale(prix);
	        dto.setStatut(statut);

	        result.add(dto);
	    }

	    return result;
	}
	public Réservation findActiveReservationByClient(Long utilisateurId) {
	    List<Réservation> results = reservationRepository.findByUtilisateurIdAndStatutOrderByDateDebutDesc(utilisateurId, StatutReservation.CONFIRMEE);
	    if (!results.isEmpty()) {
	        return results.get(0);
	    }
	    return null;
	}

	private ReservationDTO convertToDto(Réservation reservation) {
	    ReservationDTO dto = new ReservationDTO();
	    dto.setId(reservation.getId());
	    dto.setDateDebut(reservation.getDateDebut() != null ? reservation.getDateDebut().toString() : "??");
	    dto.setDateFin(reservation.getDateFin() != null ? reservation.getDateFin().toString() : "??");
	    dto.setAmount(reservation.getMontant() != null ? reservation.getMontant() : 0);
	    dto.setStatut(reservation.getStatut() != null ? reservation.getStatut().name() : "??");

	    if (reservation.getVoiture() != null) {
	        dto.setCarModele(reservation.getVoiture().getModele() != null ? reservation.getVoiture().getModele() : "Inconnu");
	        dto.setCarImmatriculation(reservation.getVoiture().getImmatriculation() != null ? reservation.getVoiture().getImmatriculation() : "Inconnue");
	    } else {
	        dto.setCarModele("Inconnu");
	        dto.setCarImmatriculation("Inconnue");
	    }

	    if (reservation.getClient() != null) {
	        dto.setClientFirstName(reservation.getClient().getFirstName() != null ? reservation.getClient().getFirstName() : "");
	        dto.setClientLastName(reservation.getClient().getLastName() != null ? reservation.getClient().getLastName() : "");
	        dto.setClientEmail(reservation.getClient().getEmail() != null ? reservation.getClient().getEmail() : "");
	        dto.setClientNumeroPermis(reservation.getClient().getNumeroPermis() != null ? reservation.getClient().getNumeroPermis() : "");
	    }

	    // Récupérer infos paiement
	 // Info paiement (statut + méthode)
	    if (reservation.getPaiement() != null) {
	        Paiement paiement = reservation.getPaiement(); // bien Paiement, pas Object ni DTO
	        dto.setStatutPaiement(paiement.getStatut() != null ? paiement.getStatut().name() : "??");
	        dto.setMethodePaiement(paiement.getMethodePaiement() != null ? paiement.getMethodePaiement().toString() : "??");
	    } else {
	        dto.setStatutPaiement("??");
	        dto.setMethodePaiement("??");
	    }

	    return dto;
	}

	public List<Réservation> findByProprietaireEmail(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	
	 public List<Réservation> getReservationsByProprietaire(Long proprietaireId) {
	        return reservationRepository.findByVoitureProprietaireId(proprietaireId);
	    }

	    public Réservation updateReservationStatus(Long id, StatutReservation status) {
	        Réservation reservation = reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
	        reservation.setStatut(status);
	        return reservationRepository.save(reservation);
	    }

		public Réservation saveReservation(Réservation reservation, MultipartFile virementFile) {
			// TODO Auto-generated method stub
			return null;
		}

		  

		    @Autowired private UserRepository userRepo;

		    // Création d'une réservation avec paiement et facture
		    @Transactional
		    public Facture saveReservationWithPayment(Réservation reservation, Paiement paiement, User client) throws Exception {
		        // Lier l'utilisateur à la réservation
		        reservation.setUtilisateur(client);
		        reservation.setLocataire(client);

		        // Enregistrer réservation
		        Réservation savedReservation = reservationRepository.save(reservation);

		        // Lier le paiement à la réservation
		        paiement.setReservation(savedReservation);
		        Paiement savedPaiement = paiementRepository.save(paiement);

		        // Générer facture PDF
		        byte[] pdfData = generateInvoicePDF(savedReservation, savedPaiement);

		        Facture facture = new Facture();
		        facture.setReservation(savedReservation);
		        facture.setClient(client);
		        facture.setUtilisateur(client);
		        facture.setPaiement(savedPaiement);
		        facture.setDateEmission(new Date());
		        facture.setDateLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
		        facture.setModePaiement(paiement.getMethode());
		        facture.setStatut("EN_ATTENTE");
		        facture.setFacturePdf(pdfData);

		        // Sauvegarder la facture
		        return factureRepo.save(facture);
		    }

		    // Méthode pour générer un PDF de facture (exemple simple)
		    private byte[] generateInvoicePDF(Réservation reservation, Paiement paiement) throws Exception {
		        // Ici tu peux utiliser iText, PDFBox ou autre bibliothèque pour créer le PDF
		        // Exemple fictif : renvoyer un tableau de bytes vide
		        return ("Facture pour réservation n° " + reservation.getId() + "\nMontant: " + paiement.getMontant()).getBytes();
		    }

			
			public List<Réservation> findReservationsByOwner(Long ownerId) {
			    return reservationRepository.findReservationsByOwner(ownerId);
			}

			public Réservation findLastReservationByClientAndOwner(Long clientId, Long ownerId) {
			    List<Réservation> reservations = reservationRepository.findReservationsByClientAndOwner(clientId, ownerId);
			    return reservations.isEmpty() ? null : reservations.get(0); // renvoie la dernière réservation
			}

		    
			public Réservation findLastReservationByClientAndOwner(User client, Propritaire proprietaire) {
		        List<Réservation> reservations = reservationRepository.findLastReservationByClientAndOwner(client, proprietaire);
		        return reservations.isEmpty() ? null : reservations.get(0);
		    }

		    public List<Réservation> findReservationsByOwner(Propritaire proprietaire) {
		        // Méthode pour récupérer toutes les réservations d'un propriétaire
		        return reservationRepository.findByVoitureProprietaire(proprietaire);
		    }



}