package location_voiture.persistence.model;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import location_voiture.service.EmailService;

@Component
public class TestEmail {

    @Autowired
    private EmailService emailService;

   
}