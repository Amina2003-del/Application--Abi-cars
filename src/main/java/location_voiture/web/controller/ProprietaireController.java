package location_voiture.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import location_voiture.persistence.model.Car;
import location_voiture.persistence.model.Propritaire;
import location_voiture.repository.CarRepository;
import location_voiture.repository.ProprietaireRepository;

@Controller
@RequestMapping("/proprietaire")
public class ProprietaireController {
	
	
	
	
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ProprietaireRepository proprietaireRepository;

    @GetMapping("/ajouter-voiture")
    public String afficherFormulaire(Model model) {
        model.addAttribute("car", new Car());
        return "ajouter_voiture";
    }

    @PostMapping("/ajouter-voiture")
    public String ajouterVoiture(@ModelAttribute("car") Car car, @RequestParam Long proprietaireId) {
        Propritaire proprietaire = proprietaireRepository.findById(proprietaireId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable"));
        car.setProprietaire(proprietaire);
        carRepository.save(car);
        return "test";
    }


}
