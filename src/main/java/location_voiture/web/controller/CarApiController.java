package location_voiture.web.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.dto.ProprietaireDto;
import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Propritaire;
import location_voiture.persistence.model.StatutApprobationVoiture;
import location_voiture.repository.CarRepository;
import location_voiture.repository.ProprietaireRepository;
import location_voiture.service.CarService;
import location_voiture.service.ProprietaireFileService;
import location_voiture.service.ProprietaireService;
import ma.abisoft.persistence.model.User;


@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "") // Ou "*" pour tout autoriser pendant le développement
public class CarApiController {
    private static final Logger logger = LoggerFactory.getLogger(CarApiController.class);

    private final CarService carService;
    private final ProprietaireService proprietaireService;
    private final CarRepository carRepository; // Injecté pour handleVoitureRequest
    private final ProprietaireRepository proprietaireRepository; // Injecté pour handleVoitureRequest
    @Autowired
    private ProprietaireFileService fileService;
    @Autowired
    public CarApiController(CarService carService, CarRepository carRepository, ProprietaireRepository proprietaireRepository,ProprietaireService proprietaireservice) {
        this.carService = carService;
        this.carRepository = carRepository; // Initialisation
        this.proprietaireRepository = proprietaireRepository; 
        this.proprietaireService= proprietaireservice;// Initialisation
    }

    // Méthodes de mapping (privées à ce contrôleur ou déplaçables dans une classe utilitaire/service de mapping)
    private CarDTO mapCarToVoitureDTO(Car car) {
        if (car == null) {
            return null;
        }
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setMarque(car.getMarque());
        dto.setModele(car.getModele());
        dto.setAnnee(car.getAnnee());
        dto.setType(car.getType());
        dto.setPlaces(car.getPlaces());
        dto.setCategorie(car.getCategorie());
        dto.setKilometrage(car.getKilometrage());
        dto.setImmatriculation(car.getImmatriculation());
        dto.setCarburant(car.getCarburant());
        dto.setImagePrincipaleURL(car.getImagePrincipaleURL());
        dto.setBoite(car.getBoite());
        dto.setPrixJournalier(car.getPrixJournalier());
        dto.setVille(car.getVille());
        dto.setDescription(car.getDescription());
        dto.setStatutApprobation(car.getStatutApprobation());

        if (car.getProprietaire() != null) {
            dto.setProprietaire(mapPropritaireToProprietaireDTO(car.getProprietaire()));
        }
        return dto;
    }

    private ProprietaireDto mapPropritaireToProprietaireDTO(Propritaire proprietaire) {
        if (proprietaire == null || proprietaire.getUser() == null) {
            return null;
        }

        User user = proprietaire.getUser();
        ProprietaireDto dto = new ProprietaireDto();
        dto.setId(proprietaire.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setTel(user.getTel());
        // NE PAS mapper les collections qui causeraient des boucles (comme roles ou voitures depuis ici)
        return dto;
    }



    // Endpoints pour les changements de statut d'approbation
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveVoiture(@PathVariable Long id) {
        try {
            Car voitureEntite = carService.updateStatutApprobation(id, StatutApprobationVoiture.APPROUVEE);
            CarDTO voitureDTO = mapCarToVoitureDTO(voitureEntite);
            return ResponseEntity.ok().body(Map.of("message", "Voiture approuvée avec succès.", "voiture", voitureDTO));
        } catch (EntityNotFoundException e) {
            logger.warn("Approbation échouée pour voiture ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Erreur d'argument lors de l'approbation pour voiture ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur interne lors de l'approbation de la voiture ID {}: ", id, e);
            // e.printStackTrace(); // Décommentez pour voir la trace complète dans la console serveur pendant le développement
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne lors de l'approbation.");
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectVoiture(@PathVariable Long id) {
        try {
            Car voitureEntite = carService.updateStatutApprobation(id, StatutApprobationVoiture.REJETEE);
            CarDTO voitureDTO = mapCarToVoitureDTO(voitureEntite);
            return ResponseEntity.ok().body(Map.of("message", "Voiture rejetée avec succès.", "voiture", voitureDTO));
        } catch (EntityNotFoundException e) {
            logger.warn("Rejet échoué pour voiture ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Erreur d'argument lors du rejet pour voiture ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur interne lors du rejet de la voiture ID {}: ", id, e);
            // e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne lors du rejet.");
        }
    }

    @PutMapping("/pending/{id}")
    public ResponseEntity<?> setVoitureAsPending(@PathVariable Long id) {
        try {
            Car voitureEntite = carService.updateStatutApprobation(id, StatutApprobationVoiture.EN_ATTENTE);
            CarDTO voitureDTO = mapCarToVoitureDTO(voitureEntite);
            return ResponseEntity.ok().body(Map.of("message", "Voiture mise en attente avec succès.", "voiture", voitureDTO));
        } catch (EntityNotFoundException e) {
            logger.warn("Mise en attente échouée pour voiture ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Erreur d'argument lors de la mise en attente pour voiture ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur interne lors de la mise en attente de la voiture ID {}: ", id, e);
            // e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne lors de la mise en attente.");
        }
    }


    // Endpoint POST plus générique (à utiliser avec prudence pour les mises à jour de statut)
    @PostMapping
    public ResponseEntity<?> handleVoitureRequest(@RequestBody CarRequestBody requestBody) {
        String action = requestBody.getAction();
        Long id = requestBody.getId();

        logger.debug("Requête POST reçue: action='{}', id={}", action, id);

        if (action == null) { // L'ID peut être null pour une création, mais l'action est nécessaire
            return ResponseEntity.badRequest().body("Action manquante");
        }

        if ("get".equals(action)) {
            if (id == null) return ResponseEntity.badRequest().body("ID manquant pour l'action 'get'");
            // Il serait mieux d'utiliser carService.getCarById(id) et de mapper en DTO
            return carRepository.findById(id)
                    .map(car -> ResponseEntity.ok(mapCarToVoitureDTO(car))) // Mapper en DTO
                    .orElseGet(() -> {
                        logger.warn("Voiture non trouvée pour l'action 'get' avec ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } else if ("update".equals(action)) {
            if (id == null) return ResponseEntity.badRequest().body("ID manquant pour l'action 'update'");
            // Il serait mieux d'utiliser carService.updateCar(carModifiee) et de mapper en DTO
            // La logique ici modifie directement l'entité récupérée du repository
            return carRepository.findById(id)
                    .map(car -> {
                        // Mettre à jour les champs de l'entité 'car' depuis 'requestBody'
                        if (requestBody.getMarque() != null) car.setMarque(requestBody.getMarque());
                        if (requestBody.getModele() != null) car.setModele(requestBody.getModele());
                        if (requestBody.getImmatriculation() != null) car.setImmatriculation(requestBody.getImmatriculation());
                        if (requestBody.getAnnee() != null) car.setAnnee(requestBody.getAnnee());
                        if (requestBody.getPrixJournalier() != null) car.setPrixJournalier(requestBody.getPrixJournalier());
                        if (requestBody.getDescription() != null) car.setDescription(requestBody.getDescription());
                        // ... Mettre à jour les autres champs simples ...

                        // Mise à jour du statut d'approbation (avec conversion String -> Enum)
                        if (requestBody.getStatutApprobation() != null) {
                            try {
                                StatutApprobationVoiture statutEnum = StatutApprobationVoiture.fromString(requestBody.getStatutApprobation());
                                car.setStatutApprobation(statutEnum); // Peut être null si fromString retourne null
                            } catch (IllegalArgumentException e) {
                                logger.warn("Statut d'approbation invalide dans la requête d'update: {}. Ignoré.", requestBody.getStatutApprobation());
                            }
                        }

                        // Charger et affecter le propriétaire
                        if (requestBody.getProprietaire() != null && requestBody.getProprietaire().getId() != null) {
                            Propritaire proprietaire = proprietaireRepository.findById(requestBody.getProprietaire().getId())
                                    .orElseThrow(() -> {
                                        logger.warn("Propriétaire non trouvé avec ID: {}", requestBody.getProprietaire().getId());
                                        return new IllegalArgumentException("Propriétaire non trouvé avec ID: " + requestBody.getProprietaire().getId());
                                    });
                            car.setProprietaire(proprietaire);
                        }

                        Car updatedCarEntity = carRepository.save(car);
                        return ResponseEntity.ok(mapCarToVoitureDTO(updatedCarEntity)); // Mapper en DTO
                    })
                    .orElseGet(() -> {
                        logger.warn("Voiture non trouvée pour l'action 'update' avec ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        }
        // Ajouter une action "create" ?
        // else if ("create".equals(action)) { ... }


        logger.warn("Action non reconnue reçue: {}", action);
        return ResponseEntity.badRequest().body("Action non reconnue: " + action);
    }

    // Classe interne pour mapper le corps de la requête JSON pour l'endpoint POST
    // Devrait idéalement être un DTO séparé et non une classe interne si elle est complexe
    public static class CarRequestBody {
        private Long id;
        private String action;
        private String marque;
        private String modele;
        private String immatriculation;
        private Integer annee;
        private Double prixJournalier;
        private String description;
        private String statutApprobation; // Reçu en String
        private String disponible;
        private ProprietaireDto proprietaire; // Utiliser ProprietaireDTO pour la requête aussi est une bonne idée

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getMarque() { return marque; }
        public void setMarque(String marque) { this.marque = marque; }
        public String getModele() { return modele; }
        public void setModele(String modele) { this.modele = modele; }
        public String getImmatriculation() { return immatriculation; }
        public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
        public Integer getAnnee() { return annee; }
        public void setAnnee(Integer annee) { this.annee = annee; }
        public Double getPrixJournalier() { return prixJournalier; }
        public void setPrixJournalier(Double prixJournalier) { this.prixJournalier = prixJournalier; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatutApprobation() { return statutApprobation; } // Retourne String
        public void setStatutApprobation(String statutApprobation) { this.statutApprobation = statutApprobation; }
        public String getDisponible() { return disponible; }
        public void setDisponible(String disponible) { this.disponible = disponible; }
        public ProprietaireDto getProprietaire() { return proprietaire; } // Devrait être ProprietaireDTO
        public void setProprietaire(ProprietaireDto proprietaire) { this.proprietaire = proprietaire; }
    }
    
    
    
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getVoitureById(@PathVariable Long id) {
        try {
            Car carEntity = carService.getCarById(id);
            if (carEntity == null) {
                logger.warn("GET /get/{}: Voiture non trouvée avec ID: {}", id, id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("message", "Voiture non trouvée avec ID: " + id));
            }
            CarDTO voitureDTO = mapCarToVoitureDTO(carEntity); // mapCarToVoitureDTO doit être définie
            return ResponseEntity.ok(voitureDTO); // Retourner directement le DTO
        } catch (Exception e) {
            logger.error("Erreur GET /get/{} : ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "Erreur lors de la récupération de la voiture."));
        }
    }
    
 // Dans CarApiController.java
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateVoitureData(@PathVariable Long id, @RequestBody CarDTO voitureDTOFromRequest) {
        try {
            // Idéalement, cette logique de mise à jour complète devrait être dans CarService
            Car carToUpdate = carService.getCarById(id);
            if (carToUpdate == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("message", "Voiture non trouvée pour mise à jour avec ID: " + id));
            }

            // Mettre à jour les champs de l'entité carToUpdate depuis voitureDTOFromRequest
            // Assurez-vous que les noms de champs dans VoitureDTO correspondent à ceux de Car
            carToUpdate.setMarque(voitureDTOFromRequest.getMarque());
            carToUpdate.setModele(voitureDTOFromRequest.getModele());
            carToUpdate.setImmatriculation(voitureDTOFromRequest.getImmatriculation());
            carToUpdate.setAnnee(voitureDTOFromRequest.getAnnee());         
            carToUpdate.setPrixJournalier(voitureDTOFromRequest.getPrixJournalier());
            carToUpdate.setDescription(voitureDTOFromRequest.getDescription());
            if (voitureDTOFromRequest.getProprietaireId() != null) {
                Propritaire proprietaire = proprietaireService.getById(voitureDTOFromRequest.getProprietaireId());
                if (proprietaire == null) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("message", "Propriétaire introuvable"));
                }
                carToUpdate.setProprietaire(proprietaire);
            }

         

            Car updatedCarEntity = carService.updateCar(carToUpdate); // carService.updateCar doit juste faire carRepository.save(car)
           CarDTO responseDTO = mapCarToVoitureDTO(updatedCarEntity);

            return ResponseEntity.ok().body(Map.of("message", "Voiture modifiée avec succès (données texte).", "voiture", responseDTO));

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            logger.warn("Erreur de validation PUT /update/{} : {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne PUT /update/{} : ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "Erreur interne lors de la mise à jour."));
        }
    }
    
    @PutMapping("/updateWithImages/{id}")
    public ResponseEntity<?> updateVoitureWithImages(@PathVariable Long id,
                                                     @RequestPart("voitureData") String voitureDataJson,
                                                     @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CarDTO voitureDTOFromRequest = objectMapper.readValue(voitureDataJson, CarDTO.class);

            Car carToUpdate = carService.getCarById(id);
            if (carToUpdate == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("message", "Voiture non trouvée pour mise à jour avec ID: " + id));
            }

            // Mise à jour des champs
            carToUpdate.setMarque(voitureDTOFromRequest.getMarque());
            carToUpdate.setModele(voitureDTOFromRequest.getModele());
            carToUpdate.setStatutApprobation(voitureDTOFromRequest.getStatutApprobation());

            if (voitureDTOFromRequest.getProprietaire() != null && voitureDTOFromRequest.getProprietaire().getId() != null) {
                Propritaire proprietaire = proprietaireRepository.findById(voitureDTOFromRequest.getProprietaire().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec ID: " + voitureDTOFromRequest.getProprietaire().getId()));
                carToUpdate.setProprietaire(proprietaire);
            }
            
            // NE RIEN FAIRE DANS LE ELSE
            // carToUpdate.setProprietaire(null);  <-- supprimer cette ligne


            // Gérer l’image principale localement ici si nécessaire
            if (imageFiles != null && !imageFiles.isEmpty()) {
                if (carToUpdate.getProprietaire() == null) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Propriétaire manquant pour cette voiture. Associez-en un d'abord."
                    ));
                }

                Long proprietaireId = carToUpdate.getProprietaire().getId();
                fileService.initProprietaireFolders(proprietaireId);

                boolean isFirstImage = true;
                for (MultipartFile imageFile : imageFiles) {
                    if (!imageFile.isEmpty()) {
                        String filename = fileService.saveImage(imageFile, proprietaireId);

                        // Première image = image principale
                        if (isFirstImage) {
                            carToUpdate.setImagePrincipaleURL(filename);
                            isFirstImage = false;
                        }

                        // Si tu as une relation OneToMany pour les autres images
                        // CarImage carImage = new CarImage(filename, carToUpdate);
                        // carToUpdate.getImages().add(carImage);
                    }
                }
            }

            // Persister l'entité après avoir mis à jour l'image principale
            Car updatedCarEntity = carService.updateCar(carToUpdate);

            CarDTO responseDTO = mapCarToVoitureDTO(updatedCarEntity);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Voiture modifiée avec succès (avec images).",
                    "voiture", responseDTO
            ));


           

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Erreur de désérialisation JSON pour voitureData dans /updateWithImages/{}: ", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", "Données de la voiture mal formatées."));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            logger.warn("Erreur de validation PUT /updateWithImages/{} : {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne PUT /updateWithImages/{} : ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "Erreur interne lors de la mise à jour avec images."));
        }
    
    }}
    
    
    
