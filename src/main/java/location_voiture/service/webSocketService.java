package location_voiture.service;

import org.springframework.stereotype.Service;

import location_voiture.WebSocketBrokerMessage.WebSocketHandler;

@Service
public class webSocketService {

    public void envoyerNotification(Long destinataireId, String contenu) {
        // Gérer l'envoi de la notification pour un destinataire spécifique (propriétaire ou locataire)
        WebSocketHandler.envoyerNotification("Message reçu : " + contenu);
    }

    public void envoyerNotificationAClients(String contenu) {
        // Envoyer la notification à tous les clients connectés
        WebSocketHandler.envoyerNotification(contenu);
    }
}
