package location_voiture.persistence.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import location_voiture.repository.EntretienRepository;
import location_voiture.service.EmailService;

@Component
@EnableScheduling // à mettre dans ta classe de configuration si pas déjà activé
public class EntretienRappelScheduler {

    @Autowired
    private EntretienRepository entretienRepo;

    @Autowired
    private EmailService mailService;

    @Scheduled(cron = "0 0 8 * * *") // Tous les jours à 08:00
    public void envoyerRappelEntretiens() {
        List<Entretien> entretiens = entretienRepo.findAll();
        for (Entretien e : entretiens) {
            if (!e.isRappelEnvoye()
                && e.getProchaineDateEstimee() != null
                && e.getProchaineDateEstimee().isBefore(LocalDate.now().plusDays(3))) {

                mailService.envoyerRappelEntretien(e);
                e.setRappelEnvoye(true);
                entretienRepo.save(e);
            }
        }
    }
}
