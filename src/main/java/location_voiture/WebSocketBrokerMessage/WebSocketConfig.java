package location_voiture.WebSocketBrokerMessage;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;



@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Active un message broker simple pour les destinations commençant par "/topic"
        config.enableSimpleBroker("/topic");
        
        // Préfixe pour les destinations d'application
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Enregistre le point de terminaison WebSocket "/notifications" et active SockJS
        registry.addEndpoint("/notifications").withSockJS();
    }
}

