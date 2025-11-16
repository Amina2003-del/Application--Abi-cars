package location_voiture.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AjouteVoitureController {

	@GetMapping("/ajouter")
	  public String getAjoutervoiture() {
        return "proprietaire/ajoutervoiture";
    }
 
}
