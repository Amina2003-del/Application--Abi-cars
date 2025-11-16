package location_voiture.web.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Car;
import location_voiture.repository.CarRepository;
import location_voiture.service.AvisService;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;

@Controller
@RequestMapping("/avis")
public class AvisController {

    private final AvisService avisService;
    private final CarRepository carRepository; // Pour peupler les listes déroulantes
    private final UserRepository userRepository; // Pour peupler les listes déroulantes

    @Autowired
    public AvisController(AvisService avisService, CarRepository carRepository, UserRepository userRepository) {
        this.avisService = avisService;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    private void populateDropdowns(Model model) {
        List<Car> voitures = carRepository.findAll();
        List<User> auteurs = userRepository.findAll();
        model.addAttribute("voitures", voitures);
        model.addAttribute("auteurs", auteurs);
    }

    @GetMapping
    public String listAvis(Model model) {
        model.addAttribute("listeAvis", avisService.findAll());
        return "avis/avis-list"; // Chemin vers le template Thymeleaf
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Avis avis = new Avis();
        avis.setDate(LocalDate.now()); // Pré-remplir la date
        model.addAttribute("avis", avis);
        populateDropdowns(model);
        return "avis/avis-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return avisService.findById(id)
                .map(avis -> {
                    model.addAttribute("avis", avis);
                    populateDropdowns(model);
                    return "avis/avis-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Avis non trouvé avec ID: " + id);
                    return "redirect:/avis";
                });
    }

    @PostMapping("/save")
    public String saveAvis(@Valid @ModelAttribute("avis") Avis avis,
                           BindingResult result,
                           @RequestParam("voitureId") Long voitureId, // ID de la voiture sélectionnée
                           @RequestParam("auteurId") Long auteurId,   // ID de l'auteur sélectionné
                           Model model, RedirectAttributes redirectAttributes) {

        // Attribuer manuellement voiture et auteur à partir des IDs
        // Spring ne peut pas binder directement un ID String à un objet Car/User sans Converter
        Car voiture = carRepository.findById(voitureId)
                .orElseThrow(() -> new IllegalArgumentException("Voiture invalide Id:" + voitureId));
        User auteur = userRepository.findById(auteurId)
                .orElseThrow(() -> new IllegalArgumentException("Auteur invalide Id:" + auteurId));
        avis.setVoiture(voiture);
        avis.setAuteur(auteur);

        if (result.hasErrors()) {
            populateDropdowns(model); // Re-peupler les listes si validation échoue
            return "avis/avis-form";
        }

        avisService.save(avis);
        redirectAttributes.addFlashAttribute("successMessage", "Avis sauvegardé avec succès !");
        return "redirect:/avis";
    }

    @GetMapping("/delete/{id}")
    public String deleteAvis(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            avisService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Avis supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de l'avis.");
        }
        return "redirect:/avis";
    }

}
