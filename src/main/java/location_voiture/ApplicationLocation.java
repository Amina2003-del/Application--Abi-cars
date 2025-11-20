package location_voiture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.web.context.request.RequestContextListener;

@EnableWebSecurity
@SpringBootApplication(scanBasePackages = {"location_voiture", "ma.abisoft"})
@EnableJpaRepositories(basePackages = {
    "location_voiture.repository",
    "ma.abisoft.repository"
})
@EntityScan(basePackages = {
    "location_voiture.persistence.model",
})

public class ApplicationLocation extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationLocation.class, args);
    }

  //  @Bean
    //public RequestContextListener requestContextListener() {
     //   return new RequestContextListener();
    //}
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApplicationLocation.class);
    }
    

}


	
