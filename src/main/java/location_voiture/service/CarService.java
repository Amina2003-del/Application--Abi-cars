// CarService.java
package location_voiture.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
// import java.util.stream.Collectors; // Non utilisé
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException; // Important pour les méthodes de statut
import javax.persistence.criteria.Predicate;
// import javax.transaction.Transactional; // On va utiliser celui de Spring
import org.springframework.transaction.annotation.Transactional; // Utilisation de Spring

import org.slf4j.Logger; // Ajout pour le logging
import org.slf4j.LoggerFactory; // Ajout pour le logging

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Disponibilite;
import location_voiture.persistence.model.Gallery;
import location_voiture.persistence.model.Panne;
import location_voiture.persistence.model.Reservation;
import location_voiture.persistence.model.StatutApprobationVoiture; // Assurez-vous que cet Enum est correct
import location_voiture.persistence.model.StatutTechnique;
import location_voiture.persistence.model.TypeReservation;
import location_voiture.repository.CarRepository;
import location_voiture.repository.DisponibiliteRepository;
import location_voiture.repository.GalleryRepository;
import location_voiture.repository.ReservationRepository;
import ma.abisoft.persistence.model.User;

@Service
public class CarService {

	@Autowired
    private ReservationRepository reservationRepository;
	 @Autowired
	    private GalleryRepository galleryRepository;
	 @Autowired
	    private DisponibiliteRepository disponibiliteRepository;
    private static final Logger logger = LoggerFactory.getLogger(CarService.class); // Ajout du logger
    
  /*  public List<Car> getVoituresDisponibles() {
        return carRepository.findByDisponible("disponible"); // Vous pouvez ajuster cette valeur si nécessaire
    }*/
    public List<Car> findByOwner(User proprietaire) {
        return carRepository.findByProprietaire(proprietaire);
    }
    
   /* public List<Car> getVoituresDisponiblesParCategorie(String categorie) {
        return carRepository.findByDisponibleAndCategorie("disponible", categorie);
    }*/
   // public List<Car> getCarsDisponibles(LocalDate start, LocalDate end) {
       // return carRepository.findCarsDisponibles(start, end);
   // }
 
    public List<String> getDistinctMarques() {
        return carRepository.fetchDistinctMarques();
    }
    public List<Car> getAvailableCars() {
        return carRepository.findBySupprimer(0); // on ne prend que celles non supprimées
    }


    public List<String> getDistinctModeles() {
        return carRepository.fetchDistinctModeles();
    }

    public List<String> getDistinctAnnees() {
        return carRepository.fetchDistinctAnnees();
    }

    public List<String> getDistinctVilles() {
        return carRepository.fetchDistinctVilles();
    }
 
    @Autowired
    private CarRepository carRepository;
    private final List<Car> voituresList = new ArrayList<>(); // Toujours présent comme dans votre code

    // Méthode de recherche utilisant JPA Specifications pour plus de flexibilité
    public List<Car> searchVoitures(String make, String model, String status) {
        Specification<Car> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (make != null && !make.isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("marque")), make.toLowerCase()));
            }
            if (model != null && !model.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("modele")), "%" + model.toLowerCase() + "%"));
            }
            if (status != null && !status.isEmpty() && !"Tous".equalsIgnoreCase(status)) {
                // Modification pour utiliser l'Enum StatutApprobationVoiture
                try {
                    // Tenter de convertir la chaîne en Enum.
                    // Si votre Enum a une méthode fromString(String text) c'est mieux.
                    // Sinon, valueOf est sensible à la casse et doit correspondre exactement au nom de la constante.
                    StatutApprobationVoiture statutEnum = StatutApprobationVoiture.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("statutApprobation"), statutEnum));
                } catch (IllegalArgumentException e) {
                    logger.warn("Valeur de statut invalide reçue dans searchVoitures : '{}'. Ignoré.", status);
                    // System.err.println (original) peut être remplacé par logger.warn
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        // Si vous voulez que les filtres s'appliquent, il faut passer spec à findAll:
        // return carRepository.findAll(); // Version originale qui ignore les filtres
        return carRepository.findAll(spec); // CORRIGÉ pour appliquer les filtres
    }

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Value("${upload.dir:uploads/}") // Gardé tel quel
    private String uploadDir;

    @Transactional // Utiliser org.springframework.transaction.annotation.Transactional
    public Car saveCar(Car car) {
        // Si c'est une nouvelle voiture et que le statut n'est pas défini, mettez PENDING par défaut.
        if (car.getId() == null && car.getStatutApprobation() == null) {
            car.setStatutApprobation(StatutApprobationVoiture.EN_ATTENTE);
          /*  if (car.getDisponible() == null) { // Gérer la dispo par défaut
                car.setDisponible("Indisponible"); // ou false si Boolean
            }*/
        }
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public void saveCarWithImages(Car car, List<MultipartFile> carImages) {
        // TODO Auto-generated method stub - Laissé comme dans votre code, bien qu'il y ait une autre méthode saveCar avec images
    }

  /*  public long countAvailableCars() {
        // Si 'disponible' est Boolean : return carRepository.countByDisponible(true);
        return carRepository.countByDisponible("Disponible");
    }*/

   // public long countNonValidatedCars() {
        // Cela compte les voitures "Indisponible". Si "non validées" signifie "PENDING",
        // il faudrait une autre méthode : countByStatutApprobation(StatutApprobationVoiture.PENDING)
        // Si 'disponible' est Boolean : return carRepository.countByDisponible(false);
      //  return carRepository.countByDisponible("Indisponible");
   // }

    public List<Car> getAllVoitures() {
        return carRepository.findAll();
    }

    @Transactional // Utiliser org.springframework.transaction.annotation.Transactional
    public Car saveCar(Car car, List<MultipartFile> images) {
        // Si c'est une nouvelle voiture et que le statut n'est pas défini, mettez PENDING par défaut.
         if (car.getId() == null && car.getStatutApprobation() == null) {
            car.setStatutApprobation(StatutApprobationVoiture.EN_ATTENTE);
          /*  if (car.getDisponible() == null) { // Gérer la dispo par défaut
                car.setDisponible("Indisponible"); // ou false si Boolean
            }*/
        }
        // Votre logique originale pour saveCar avec images
        Car savedCar = carRepository.save(car); // Sauvegarde la voiture d'abord

        if (images != null && !images.isEmpty()) {
            try {
                // Appelle votre méthode saveCarImages originale
                saveCarImages(savedCar, images);
            } catch (IOException e) {
                logger.error("Erreur lors de la sauvegarde des images pour la voiture ID {}", savedCar.getId(), e);
                // e.printStackTrace(); // Remplacé par logger.error
                // Gérer l'erreur selon vos besoins, peut-être relancer une RuntimeException pour rollback
                throw new RuntimeException("Erreur IO pendant la sauvegarde des images", e);
            }
        }
        return savedCar;
    }

    // Votre méthode saveCarImages originale, non modifiée structurellement
    private void saveCarImages(Car car, List<MultipartFile> imageFiles) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        boolean isFirst = true;
        // La liste savedImages n'est pas utilisée pour ajouter à car.images dans votre code original ici.
        // Si l'entité Car a une List<ImageVoiture> images, elles devraient être ajoutées à cette collection
        // et persistées par cascade. Pour l'instant, je respecte votre code où saveCarImages
        // appelle carRepository.save(car) à la fin.
        List<Gallery> savedImages = new ArrayList<>(); // Gardé comme dans votre code

        for (MultipartFile file : imageFiles) {
            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed_image";
                String fileName = UUID.randomUUID().toString() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                Path filePath = uploadPath.resolve(fileName);

                Files.write(filePath, file.getBytes());

                Gallery image = new Gallery();
                image.setNomImage(fileName);
                image.setUrlImage("/uploads/cars/" + fileName);
                image.setContentType(file.getContentType());
                image.setPrincipale(isFirst);
                image.setVoiture(car);

                savedImages.add(image); // Ajouté à la liste locale

                isFirst = false;
            }
        }
        // Si Car a une relation @OneToMany(cascade=CascadeType.ALL) avec ImageVoiture,
        // vous devriez faire :
        // if (car.getImages() == null) car.setImages(new ArrayList<>());
        // car.getImages().addAll(savedImages);
        // Et le carRepository.save(car) ci-dessous les persisterait.
        // Cependant, votre code original ne fait pas cela et appelle save une seconde fois.

        // Enregistrer la voiture mise à jour avec ses images
        carRepository.save(car); // Appel original gardé
    }

    @Transactional // Utiliser org.springframework.transaction.annotation.Transactional
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    @Transactional // Utiliser org.springframework.transaction.annotation.Transactional
    public Car updateCar(Car car) {
        return carRepository.save(car);
    }

    public List<Car> filtrerVoitures(String marque, String modele, String statutFiltreString) {
        return carRepository.findAll(creerSpecificationFiltre(marque, modele, statutFiltreString));
    }

    private Specification<Car> creerSpecificationFiltre(String marque, String modele, String statutFiltreString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (marque != null && !marque.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("marque")), "%" + marque.toLowerCase().trim() + "%"));
            }

            if (modele != null && !modele.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("modele")), "%" + modele.toLowerCase().trim() + "%"));
            }

            if (statutFiltreString != null && !statutFiltreString.trim().isEmpty() && !statutFiltreString.equalsIgnoreCase("Tous")) {
                try {
                    // Conversion des valeurs frontend vers backend
                    StatutApprobationVoiture statutEnum = convertToBackendStatut(statutFiltreString);
                    if (statutEnum != null) {
                        predicates.add(cb.equal(root.get("statutApprobation"), statutEnum));
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Statut invalide: '{}'. Filtre ignoré.", statutFiltreString);
                }
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Méthode de conversion des statuts
    private StatutApprobationVoiture convertToBackendStatut(String frontendStatut) {
        if (frontendStatut == null) return null;
        
        switch (frontendStatut.toUpperCase()) {
            case "APPROVED":
                return StatutApprobationVoiture.APPROUVEE; // ou APPROVED selon votre enum
            case "PENDING":
                return StatutApprobationVoiture.EN_ATTENTE; // ou PENDING
            case "REJECTED":
                return StatutApprobationVoiture.REJETEE; // ou REJECTED
            default:
                // Essayer de matcher directement avec l'enum
                try {
                    return StatutApprobationVoiture.valueOf(frontendStatut.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Statut non reconnu: {}", frontendStatut);
                    return null;
                }
        }
    }

    // ================================================================
    // IMPLÉMENTATION DES MÉTHODES DE STATUT DEMANDÉES
    // ================================================================
    @Transactional // Utiliser org.springframework.transaction.annotation.Transactional
    public void setCarPending(Long carId) {
        logger.info("Mise en attente de la voiture ID: {}", carId);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Voiture non trouvée pour mise en attente avec l'ID: " + carId));
        car.setStatutApprobation(StatutApprobationVoiture.EN_ATTENTE);
        // Optionnel: ajuster la disponibilité
        // car.setDisponible("Indisponible"); // ou false si Boolean
        carRepository.save(car);
        logger.info("Voiture ID {} mise en attente.", carId);
    }

    @Transactional // Utiliser org.springframework.transaction.annotation.Transactional
    public void rejectCar(Long carId) {
        logger.info("Rejet de la voiture ID: {}", carId);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Voiture non trouvée pour rejet avec l'ID: " + carId));
        car.setStatutApprobation(StatutApprobationVoiture.REJETEE);
        //car.setDisponible("Indisponible"); // ou false si Boolean
        carRepository.save(car);
        logger.info("Voiture ID {} rejetée.", carId);
    }

    @Transactional // Utiliser org.springframework.transaction.annotation.Transactional
    public void approveCar(Long carId) {
        logger.info("Approbation de la voiture ID: {}", carId);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Voiture non trouvée pour approbation avec l'ID: " + carId));
        car.setStatutApprobation(StatutApprobationVoiture.APPROUVEE);
       // car.setDisponible("Disponible"); // ou true si Boolean
        carRepository.save(car);
        logger.info("Voiture ID {} approuvée.", carId);
    }
    // ================================================================
    
    
    

    @Transactional
    public Car updateStatutApprobation(Long voitureId, StatutApprobationVoiture nouveauStatut) {
        // Ajoutez ce log pour être sûr de ce qui arrive
        logger.info("SERVICE updateStatutApprobation - voitureId: {}, nouveauStatut: {}", voitureId, nouveauStatut);

        if (nouveauStatut == null) { // Vérification cruciale
            logger.error("SERVICE ERREUR: nouveauStatut est null pour voiture ID {}. Levée d'IllegalArgumentException.", voitureId);
            throw new IllegalArgumentException("Le nouveau statut d'approbation ne peut pas être null.");
        }

        Car voiture = carRepository.findById(voitureId)
                .orElseThrow(() -> {
                    logger.warn("SERVICE: Voiture non trouvée avec l'ID : {}", voitureId);
                    return new EntityNotFoundException("Voiture non trouvée avec l'ID : " + voitureId);
                });

        logger.debug("SERVICE: Voiture trouvée: {}", voiture.getId());
         voiture.setStatutApprobation(nouveauStatut);
        logger.debug("SERVICE: Statut d'approbation setté à: {}", nouveauStatut);

        // Logique pour la disponibilité basée sur le statut
       switch (nouveauStatut) { // Maintenant, nouveauStatut ne devrait plus être null ici
            case APPROUVEE:
              //  voiture.setDisponible("Disponible"); // Assurez-vous que cette chaîne correspond à vos attentes
                break;
            case REJETEE:
              //  voiture.setDisponible("Indisponible");
                break;
            case EN_ATTENTE:
               // voiture.setDisponible("Indisponible");
                break;
        }
        //logger.debug("SERVICE: Disponibilité settée à: {}", voiture.getDisponible());

        Car savedVoiture = carRepository.save(voiture);
       // logger.info("SERVICE: Voiture ID {} sauvegardée avec statut {} et dispo {}", savedVoiture.getId(), savedVoiture.getStatutApprobation(), savedVoiture.getDisponible());
        return savedVoiture;
    
    }

    public boolean isDisponiblePourPeriode(Car voiture, LocalDate debut, LocalDate fin) {
        for (Reservation res : voiture.getReservations()) {
            if (!(res.getDateFin().isBefore(debut) || res.getDateDebut().isAfter(fin))) {
                return false; // Chevauchement trouvé
            }
        }
        return true;
    }

   

   

    // Récupérer la galerie d'une voiture
    public List<Gallery> getGalleryByCarId(Long carId) {
        return galleryRepository.findByVoitureId(carId);
    }
	


	public Car findById(Long voitureId) {
	    return carRepository.findById(voitureId).orElse(null);
	}


	public List<Car> getAllVehicules() {
        return carRepository.findAll();
	}


	public List<Car> findAll() {
        return carRepository.findAll();
    }


	public List<Map<String, Object>> getAvailableCars(String ville, String pickupDate, String returnDate) {
		// TODO Auto-generated method stub
		return null;
	}


	public Map<String, Object> getCarById(String carId) {
		// TODO Auto-generated method stub
		return null;
	}


	




	public List<Car> searchAvailableCars(String ville, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub
		return null;
	}


    public List<Disponibilite> findDisponibilitesByCarId(Long carId) {
        return disponibiliteRepository.findByCarId(carId);
    }
		
		

    public Map<String, Integer> getCarPopularity() {
        List<Reservation> reservations = reservationRepository.findAll();
        Map<String, Integer> popularity = new HashMap<>();
        for (Reservation reservation : reservations) {
            Car car = reservation.getVoiture();
            if (car != null) {
                String key = car.getMarque() + " " + car.getModele();
                popularity.put(key, popularity.getOrDefault(key, 0) + 1);
            }
        }
        return popularity;
    }

    public List<Map<String, String>> getRecentActivities() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(r -> {
            Map<String, String> activity = new HashMap<>();
            activity.put("type", "Reservation");
            String detail = r.getVoiture() != null
                    ? r.getVoiture().getModele() + " - #" + r.getId()
                    : "Voiture non spécifiée";
            String statut = (r.getStatut() != null) ? r.getStatut().name() : "Statut inconnu";
            activity.put("detail", detail);
            activity.put("date", r.getDateDebut() != null ? r.getDateDebut().toString() : "Date inconnue");
            activity.put("status", statut);
            return activity;
        }).collect(Collectors.toList());
    }
public List<CarDTO> rechercherDisponibles(String pickup, LocalDate dateDebut, LocalDate dateFin, String type) {
    List<Car> cars = carRepository.findAvailableCars(pickup, dateDebut, dateFin, type);

    return cars.stream()
        .<CarDTO>map(c ->  // <CarDTO> force l'inférence : T = CarDTO, pas Object
            CarDTO.builder()
                .id(c.getId())
                .marque(c.getMarque())
                .modele(c.getModele())
                .type(type)
                .imagePrincipaleURL(c.getImagePrincipaleURL())
                .prixJournalier(c.getPrixJournalier())  // Autobox si double primitif
                .description(c.getDescription())
                .build()
        )
        .collect(Collectors.toList());
}



	public List<CarDTO> rechercherDisponibles(String adressePriseEnCharge, String adresseRestitution,
			LocalDate dateDebut, LocalDate dateFin, String typeVoiture) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<Car> getAllCarsWithDisponibilites() {
	    return carRepository.findAllWithDisponibilites();
	}


	public List<Car> rechercherVoituresDisponibles(String adressePriseEnCharge, String adresseRestitution,
			LocalDate dateDebut, LocalDate dateFin, String typeVoiture) {
		// TODO Auto-generated method stub
		return null;
	}


	

	 public List<Car> findByOwnerId(Long ownerId) {
	        return carRepository.findByProprietaireId(ownerId);
	    }
	

	    public Car save(Car car) {
	        return carRepository.save(car);
	    }
	    public String getEtatActuel(Car voiture, LocalDate date, List<Reservation> reservations) {
	        if (voiture == null) return "Indisponible";

	        // 1️⃣ Vérifier pannes actives aujourd'hui
	        if (voiture.getPannes() != null) {
	            boolean enPanneActuelle = voiture.getPannes().stream()
	                .filter(p -> p.getDateDebut() != null && p.getDateFin() != null)
	                .anyMatch(p -> !date.isBefore(p.getDateDebut()) && !date.isAfter(p.getDateFin()));
	            if (enPanneActuelle) return "En Panne";
	        }

	        // 2️⃣ Vérifier réservation en cours aujourd'hui
	        Optional<Reservation> resEnCours = reservations.stream()
	            .filter(r -> r.getDateDebut() != null && r.getDateFin() != null)
	            .filter(r -> !date.isBefore(r.getDateDebut()) && !date.isAfter(r.getDateFin()))
	            .findFirst();

	        if (resEnCours.isPresent()) {
	            TypeReservation typeRes = resEnCours.get().getTypeReservation();
	            switch (typeRes) {
	            case PRESENTIELLE: return "Réservée (Présentielle)";
                case DISTANCE: return "Réservée (À distance)";
                default: return "Réservée";
	            }
	        }

	        // 3️⃣ Vérifier prochaine réservation future
	        Optional<Reservation> resFuture = reservations.stream()
	            .filter(r -> r.getDateDebut() != null)
	            .filter(r -> date.isBefore(r.getDateDebut()))
	            .sorted(Comparator.comparing(Reservation::getDateDebut))
	            .findFirst();

	        if (resFuture.isPresent()) {
	            TypeReservation typeRes = resFuture.get().getTypeReservation();
	            switch (typeRes) {
	                
	                case PRESENTIELLE: return "Occupée (Présentielle)";
	                case DISTANCE: return "Occupée (À distance)";
	                default: return "Réservée";
	            }
	        }

	        // 4️⃣ Vérifier pannes futures
	        if (voiture.getPannes() != null) {
	            boolean enPanneFuture = voiture.getPannes().stream()
	                .filter(p -> p.getDateDebut() != null && p.getDateDebut().isAfter(date))
	                .findAny()
	                .isPresent();
	            if (enPanneFuture) return "Panne programmée";
	        }

	        // 5️⃣ Sinon voiture opérationnelle
	        return "Opérationnelle";
	    }

	  
	    


}