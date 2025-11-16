package location_voiture.web.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import location_voiture.repository.CarRepository;
import location_voiture.repository.ReservationRepository;

@RestController
@RequestMapping("/api/activity")
public class RecentActivityController {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CarRepository carRepository;

    @GetMapping("/recent")
    public List<Object[]> getRecentActivities() {
		return null;
        // Simule l'activité récente à partir des mises à jour
      //List<Object[]> activities = carRepository.findTop5ByOrderByDisponibleDesc()
                //.stream().map(v -> new Object[]{v.getMarque() + " (" + v.getImmatriculation() + ")", "marquée comme " /*v.getDisponible()*/, LocalDateTime.now().minusHours(1)})
                //.collect(Collectors.toList());
        //return activities;
    }
}
