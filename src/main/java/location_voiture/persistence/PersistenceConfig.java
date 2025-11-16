package location_voiture.persistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PersistenceConfig {

	@Bean(name = "webRestTemplate") // Nom unique
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }}