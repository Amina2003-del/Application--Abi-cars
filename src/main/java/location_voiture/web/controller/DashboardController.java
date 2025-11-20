package location_voiture.web.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import location_voiture.persistence.model.Avis;
import location_voiture.persistence.model.Reservation;
import location_voiture.repository.AvisRepository;
import location_voiture.repository.CarRepository;
import location_voiture.repository.ReservationRepository;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AvisRepository avisRepository;

    @GetMapping
    public String afficherDashboard(Model model) {
        // 1. Statistiques globales
        long totalVoitures = carRepository.count();
       // long voituresDisponibles = carRepository.countByDisponible("true"); // Utiliser countByDisponibleTrue pour plus de clarté
       // long voituresLouees = totalVoitures - voituresDisponibles;

        // 2. Revenu du mois
        int moisActuel = LocalDate.now().getMonthValue();
        Double revenuMensuel = reservationRepository.sommeRevenuDuMois(moisActuel); // Vous devez avoir cette méthode dans votre repo
        if (revenuMensuel == null) revenuMensuel = 0.0;

        // 3. Moyenne des avis
        Double moyenneAvis = avisRepository.moyenneAvis(); // Vous devez avoir cette méthode dans votre repo
        if (moyenneAvis == null) moyenneAvis = 0.0;

        // 4. Derniers avis
        List<Avis> derniersAvis = avisRepository.findTop5ByOrderByDateDesc();

        // 5. Dernières réservations
        List<Reservation> dernieresLocations = reservationRepository.findTop5ByOrderByIdDesc();

        // 6. Données pour le graphique (exemple simplifié)
        // Il serait plus utile de générer ces données dynamiquement depuis votre base de données
        List<String> mois = List.of("Jan", "Fév", "Mars", "Avr", "Mai", "Juin"); // à adapter
        List<Double> revenusParMois = reservationRepository.revenusParMois(); // Vous devrez créer cette méthode pour récupérer les revenus par mois

        // Ajouter les données au modèle
        model.addAttribute("totalVoitures", totalVoitures);
       // model.addAttribute("voituresDisponibles", voituresDisponibles);
       // model.addAttribute("voituresLouees", voituresLouees);
        model.addAttribute("revenuMensuel", revenuMensuel);
        model.addAttribute("moyenneAvis", moyenneAvis);
        model.addAttribute("derniersAvis", derniersAvis);
        model.addAttribute("dernieresLocations", dernieresLocations);
        model.addAttribute("mois", mois);
        model.addAttribute("revenusParMois", revenusParMois);

        return "proprietaire/dashboard"; // Assurez-vous que le nom de la vue est correct
    }
}
