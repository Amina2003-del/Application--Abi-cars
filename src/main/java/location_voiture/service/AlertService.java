package location_voiture.service;

import location_voiture.persistence.model.Alert;
import location_voiture.persistence.model.TypeAlert;
import location_voiture.repository.AlertRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
	@Autowired
	private AlertRepository alertRepository;

	@Autowired
	private UserRepository utilisateurRepository;

	public Page<Alert> findByUser(User user, org.springframework.data.domain.Pageable pageable) {
	    return alertRepository.findByUtilisateur(user, pageable);
	}

	public Page<Alert> findByUserAndType(User user, TypeAlert type, org.springframework.data.domain.Pageable pageable) {
	    return alertRepository.findByUtilisateurAndType(user, type, pageable);
	}

	public List<Alert> findRecentAlerts(User user, int limit) {
	    Page<Alert> page = alertRepository.findByUtilisateurOrderByDateEnvoiDesc(user, PageRequest.of(0, limit));
	    return page.getContent();
	}

	public int countUnreadByUser(User utilisateur) {
	    return alertRepository.countByUtilisateurAndEnvoyeAvecSuccesFalse
(utilisateur);
	}
	public List<Alert> findAlertsByUserAndFilter(User user, String filter) {
	    switch(filter.toLowerCase()) {
	        case "received":
	            return alertRepository.findByUtilisateurAndEnvoyeAvecSuccesTrueOrderByDateEnvoiDesc(user);
	        case "pending":
	            return alertRepository.findByUtilisateurAndEnvoyeAvecSuccesFalseOrderByDateEnvoiDesc(user);
	        case "failed":
	            // TODO: gérer le cas "failed" si tu as un champ d'échec, sinon renvoyer vide ou tout
	            return Collections.emptyList();
	        case "all":
	        default:
	            return alertRepository.findByUtilisateurOrderByDateEnvoiDesc(user);
	    }
	}


    @Transactional
    public void markAsRead(Long id, User user) {
        Alert alert = alertRepository.findByIdAndUtilisateur(id, user)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        alert.setEnvoyeAvecSucces(true);
        alertRepository.save(alert);
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Alert> unreadAlerts = alertRepository.findByUtilisateurAndEnvoyeAvecSuccesFalse(user);
        unreadAlerts.forEach(alert -> alert.setEnvoyeAvecSucces(true));
        alertRepository.saveAll(unreadAlerts);
    }

    @Transactional
    public void createNotification(String sujet, String message, TypeAlert type, User user, boolean envoyeAvecSucces) {
        Alert alert = new Alert(sujet, message, type, user);
        alert.setEnvoyeAvecSucces(false);
        alert.setDateEnvoi(LocalDateTime.now()); // Si dateEnvoi est obligatoire
        System.out.println("Création de l'alerte: " + alert);
        alertRepository.save(alert);
        System.out.println("Alerte sauvegardée avec ID: " + alert.getId());
    }

	public boolean renvoyerAlertPaiement(Long id) {
		// TODO Auto-generated method stub
		return false;
	}


    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }


}