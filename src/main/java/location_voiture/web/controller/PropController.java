package location_voiture.web.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import location_voiture.persistence.dto.UserDTO;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.UserService;

@RestController
@RequestMapping("/api/user")
public class PropController {

	

	    @Autowired
	    private UserService userService;
	    @GetMapping("/profille")
	    public UserDTO getUserProfile(Authentication authentication) {
	        Object principal = authentication.getPrincipal();
	        String email;

	        if (principal instanceof User) {
	            email = ((User) principal).getEmail();
	        } else if (principal instanceof UserDetails) {
	            email = ((UserDetails) principal).getUsername();
	        } else {
	            email = authentication.getName();
	        }

	        User user = userService.getUserByEmail(email);
	        return new UserDTO(user); // <-- utilise le DTO
	    }


	    @PutMapping("/proprofile")
	    public ResponseEntity<String> updateUserProfile(@RequestBody UserDTO userDto, Authentication authentication) {
	        Object principal = authentication.getPrincipal();
	        String email;

	        if (principal instanceof UserDetails) {
	            email = ((UserDetails) principal).getUsername(); // généralement l'email
	        } else if (principal instanceof User) {
	            email = ((User) principal).getEmail();
	        } else {
	            email = authentication.getName();
	        }

	        System.out.println(">>> [PUT] Mise à jour du profil");
	        System.out.println(">>> Email connecté : " + email);
	        System.out.println(">>> Données reçues : ");
	        System.out.println("    Prénom : " + userDto.getFirstName());
	        System.out.println("    Nom : " + userDto.getLastName());
	        System.out.println("    Téléphone : " + userDto.getTelephone());
	        System.out.println("    Email : " + userDto.getEmail());
	        System.out.println("    Mot de passe : " + (userDto.getPassword() != null ? "[FOURNI]" : "[NON FOURNI]"));

	        userService.updateUserProfile(email, userDto);

	        return ResponseEntity.ok("Profil mis à jour avec succès");
	    }

}
