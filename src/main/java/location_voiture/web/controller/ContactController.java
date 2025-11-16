package location_voiture.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import location_voiture.repository.MessageRepository;
import ma.abisoft.persistence.dao.UserRepository;

@Controller
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageRepository messageRepository;

  
}
