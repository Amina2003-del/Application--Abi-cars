package location_voiture.persistence.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;


@Configuration
public class paypalconfig {
	
	    private static final Logger logger = LoggerFactory.getLogger(paypalconfig.class);

	    @Value("${paypal.client.id}")
	    private String clientId;

	    @Value("${paypal.client.secret}")
	    private String clientSecret;

	    @Value("${paypal.mode}")
	    private String mode;

	    @Bean
	    public PayPalEnvironment payPalEnvironment() {
	        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
	            logger.error("Les identifiants PayPal (client.id ou client.secret) ne sont pas définis.");
	            throw new IllegalStateException("Les identifiants PayPal doivent être configurés dans application.properties.");
	        }

	        if ("sandbox".equalsIgnoreCase(mode)) {
	            logger.info("Configuration PayPal en mode Sandbox.");
	            return new PayPalEnvironment.Sandbox(clientId, clientSecret);
	        } else {
	            logger.info("Configuration PayPal en mode Live.");
	            return new PayPalEnvironment.Live(clientId, clientSecret);
	        }
	    }

	    @Bean
	    public PayPalHttpClient payPalHttpClient(PayPalEnvironment environment) {
	        return new PayPalHttpClient(environment);
	    }
	}
	
	




