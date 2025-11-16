package location_voiture.web.controller;


import location_voiture.persistence.model.Paiement;
import location_voiture.repository.PaiementRepository;
import location_voiture.repository.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/last6months")
    public Map<String, Double> getLast6MonthsRevenue() {
        Map<String, Double> revenueData = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            String monthLabel = month.getMonth().toString().substring(0, 3);
            double total = paiementRepository.findByReservationDateBetween(
                    month.atDay(1).atStartOfDay(),
                    month.atEndOfMonth().atTime(23, 59, 59))
                    .stream().mapToDouble(Paiement::getMontant).sum();
            revenueData.put(monthLabel, total);
        }
        return revenueData;
    }
}
