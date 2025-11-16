package location_voiture.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import location_voiture.persistence.model.Localisation;
import location_voiture.repository.VilleRepository;

@RestController
@RequestMapping("/villes")
@CrossOrigin(origins = "*")
public class VilleController {

    @Autowired
    private VilleRepository villeRepository;

    @GetMapping("/search")
    public List<Localisation> searchVilles(@RequestParam("term") String term) {
        System.out.println("Recherche villes avec terme = '" + term + "'");

        List<Localisation> result = villeRepository.findByNomStartingWithIgnoreCase(term);

        System.out.println("Nombre de villes trouvées avant déduplication : " + result.size());

        // Déduplication basée sur le nom de la ville
        Map<String, Localisation> uniqueVilles = new HashMap<>();
        for (Localisation loc : result) {
            uniqueVilles.putIfAbsent(loc.getNom(), loc);
        }

        List<Localisation> deduplicatedResult = new ArrayList<>(uniqueVilles.values());

        System.out.println("Nombre de villes trouvées après déduplication : " + deduplicatedResult.size());

        return deduplicatedResult;
    }
}

