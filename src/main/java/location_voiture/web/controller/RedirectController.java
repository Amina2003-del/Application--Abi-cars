package location_voiture.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class RedirectController {
	
	
	  @GetMapping("/redirect-by-role")
	    public String redirectByRole(Authentication auth) {
	        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	            return "redirect:/Administrateur/tableaubord";
	        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"))) {
	            return "redirect:/Clientes/rechercher-reserver";
	        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"))) {
	            return "redirect:/Owner/dashbord";
	        } else {
	            return "redirect:/login?error=unauthorized";
	        }
	    }
}
