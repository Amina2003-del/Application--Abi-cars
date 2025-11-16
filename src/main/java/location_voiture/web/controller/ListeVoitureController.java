package location_voiture.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/proprietaire") // Groupe logique pour les routes propriétaires
public class ListeVoitureController {
    @GetMapping("/Liste")
    public String afficherListeVoitures(Model model) {
        return "redirect:/proprietaire/Listevoiture"; // Correspond à src/main/resources/templates/proprietaire/Listevoiture.html
    }
}

