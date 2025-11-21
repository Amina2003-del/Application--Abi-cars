package ma.abisoft.web.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import location_voiture.service.EmailService;
import ma.abisoft.persistence.dao.PasswordResetTokenRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.PasswordResetToken;
import ma.abisoft.persistence.model.User;
import ma.abisoft.service.UserService;


@Controller
public class AdministrateurController {
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;
	 @Autowired
	    private UserService userService;
	 

    // Méthode pour afficher la page "Mot de passe oublié"
    @GetMapping("/Admin/forgot_password")
    public String forgotPasswordPage() {
        return "Admin/forgot_password"; // Cela correspond au fichier Admin/forgot_password.html dans templates
    }
    @GetMapping("/reset-password")
    public String displayResetPasswordPage(Model model,CsrfToken csrfToken) {
        model.addAttribute("_csrf", csrfToken);

        return "reset-password";
    }

    // 2. Traitement du formulaire forgot password
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("email") String email) {
    	
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // Générer un token
            String token = UUID.randomUUID().toString();
            PasswordResetToken myToken = new PasswordResetToken(token, user);
            tokenRepository.save(myToken);

            // Construire le lien de réinitialisation
            String resetLink = "/change-password?token=" + token;

            // Envoyer l'email
            emailService.sendSimpleMessage(email, "Reset Password", 
                    "Cliquez sur le lien pour réinitialiser votre mot de passe: " + resetLink);
        }
        

        // Toujours rediriger vers la même page pour éviter de dire "email inconnu"
        return "redirect:/reset-password?success";
    }

    // 3. Affichage du formulaire "Nouveau mot de passe"
    @GetMapping("/change-password")
    public String displayChangePasswordPage(@RequestParam("token") String token, Model model) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);

        if (resetToken == null || resetToken.getExpiryDate().before(new java.util.Date())) {
            return "redirect:/reset-password?error";
        }

        model.addAttribute("token", token);
        return "Change Password";
    }

    // 4. Traitement du formulaire "Nouveau mot de passe"
    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam("token") String token,
                                        @RequestParam("password") String password) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);

        if (resetToken == null || resetToken.getExpiryDate().before(new java.util.Date())) {
            return "redirect:/reset-password?error";
        }

        // Mettre à jour le mot de passe
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // Supprimer le token
        tokenRepository.delete(resetToken);

        return "redirect:/login?resetSuccess";
    }

}
