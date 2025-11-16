package location_voiture.web.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Ajouté pour une meilleure gestion des réponses
import org.springframework.stereotype.Controller; // Changé de @RestController à @Controller si tu mélanges API REST et vues Thymeleaf
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Ajouté pour les paramètres de requête
import org.springframework.web.bind.annotation.ResponseBody; // Ajouté si @Controller est utilisé et que cette méthode doit retourner JSON

import location_voiture.persistence.dto.CarDTO;
import location_voiture.persistence.model.Car;
import location_voiture.repository.CarRepository;
// import location_voiture.repository.CarRepository; // Plus besoin si CarService gère tout
import location_voiture.service.CarService;

@Controller // Utilise @Controller si tu sers aussi des vues Thymeleaf depuis ce contrôleur
@RequestMapping("/api/cars") // Le préfixe global reste
@CrossOrigin(origins = "http://localhost:8082") // Garde ta configuration CORS
public class CarController {
	
	


	@Autowired
    private CarRepository carRepository;
    // Plus besoin d'injecter CarRepository directement si CarService le fait déjà.
    // @Autowired
    // private CarRepository carRepository;

	@Autowired
	private final CarService carService;


    // Méthode pour mettre à jour le statut d'approbation d'une voiture
  
    
    
    
    
	
    
    
    
    
    
    
    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Endpoint pour récupérer toutes les voitures (API REST).
     * Retourne une liste de voitures en JSON.
     */
    @GetMapping // Mappé à GET /api/cars
    @ResponseBody // Nécessaire si la classe est annotée avec @Controller et que cette méthode doit retourner JSON
    public List<Car> getAllCarsApi() { // Renommé pour éviter la confusion avec la méthode pour Thymeleaf
        return carService.getAllCars(); // Utilise le service
    }

    /**
     * Endpoint pour afficher la page Thymeleaf listant les voitures.
     * @param model
     * @return le nom de la vue Thymeleaf
     */
    @GetMapping("/voitures") // Mappé à GET /api/cars/voitures
    public String listVoituresPage(Model model) { // Renommé pour la clarté
        model.addAttribute("voitures", carService.getAllCars());
        return "voitures"; // Nom du fichier Thymeleaf (ex: src/main/resources/templates/voitures.html)
    }
    
    

    /**
     * NOUVEL ENDPOINT pour le filtrage des voitures (API REST).
     * Cet endpoint sera appelé par ton script jQuery.
     * Retourne une liste de voitures filtrées en JSON.
     */
    @GetMapping("/search") // Mappé à GET /api/cars/search
    @ResponseBody // Nécessaire pour retourner du JSON depuis un @Controller
    public ResponseEntity<List<Car>> searchVoitures(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(name = "status", required = false) String approvalStatus) { // "status" est le nom du paramètre dans l'URL

        // Appelle la méthode de recherche du service
        List<Car> voituresFiltrees = carService.searchVoitures(make, model, approvalStatus);
        
        // Retourne la liste des voitures filtrées avec un statut HTTP 200 OK
        return ResponseEntity.ok(voituresFiltrees);
    }
    
    
    
    @GetMapping("/searchs")
    public ResponseEntity<List<Car>> searchCars(
            @RequestParam String ville,
            @RequestParam String pickupDate,
            @RequestParam String returnDate) {
        
        // Convertir les dates en objets LocalDate (ou autre format selon ton besoin)
        LocalDate startDate = LocalDate.parse(pickupDate);
        LocalDate endDate = LocalDate.parse(returnDate);

        // Appeler le service pour rechercher les voitures disponibles
        List<Car> availableCars = carService.searchAvailableCars(ville, startDate, endDate);

        // Retourner la liste des voitures disponibles
        return new ResponseEntity<>(availableCars, HttpStatus.OK);
    }
    @GetMapping("/voitures/disponibles")
    @ResponseBody
    public List<CarDTO> rechercherVoituresDisponibles(
        @RequestParam String adressePriseEnCharge,
        @RequestParam String adresseRestitution,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
        @RequestParam String typeVoiture) {

        return carService.rechercherDisponibles(adressePriseEnCharge, adresseRestitution, dateDebut, dateFin, typeVoiture);
    }
   
   

}