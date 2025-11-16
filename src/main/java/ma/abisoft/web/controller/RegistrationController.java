package ma.abisoft.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import location_voiture.persistence.model.Propritaire;
import location_voiture.repository.ProprietaireRepository;
import location_voiture.service.ProprietaireFileService;
import ma.abisoft.persistence.model.User;
import ma.abisoft.persistence.model.VerificationToken;
import ma.abisoft.registration.OnRegistrationCompleteEvent;
import ma.abisoft.security.ISecurityUserService;
import ma.abisoft.service.IUserService;
import ma.abisoft.service.MailClient;
import ma.abisoft.web.dto.PasswordDto;
import ma.abisoft.web.dto.UserDto;
import ma.abisoft.web.error.InvalidOldPasswordException;
import ma.abisoft.web.util.GenericResponse;

@Controller
public class RegistrationController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MailClient mailClient;
    @Autowired
    private ProprietaireRepository proprietaireRepository; 
    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;
    @Autowired
    private ProprietaireFileService fileService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public RegistrationController() {
        super();
    }

    // ==================== USER REGISTRATION ====================

    @PostMapping("/user/registration")
    @ResponseBody
    public GenericResponse registerUserAccount(
            @Valid @ModelAttribute UserDto accountDto,
            @RequestParam("logovoitureFile") MultipartFile logoFile,
            final HttpServletRequest request) throws IOException {

        // 1) Créer le User
        User registered = userService.registerNewUserAccount(accountDto);

        // 2) Récupérer le propriétaire associé
        Propritaire proprietaire = proprietaireRepository.findByUserId(registered.getId());
        if (proprietaire == null) {
            return new GenericResponse("error: proprietaire not found");
        }

        // 3) Créer les dossiers du propriétaire
        fileService.initProprietaireFolders(proprietaire.getId());

        // 4) Si logo envoyé → enregistrer
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoName = fileService.saveLogo(logoFile, proprietaire.getId());
            proprietaire.setLogovoiturePath(logoName);
            proprietaireRepository.save(proprietaire);
        }

        // 5) Event email
        eventPublisher.publishEvent(
                new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request))
        );

        return new GenericResponse("success");
    }

    // ==================== CONFIRMATION REGISTRATION ====================

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(
            HttpServletRequest request,
            Model model,
            @RequestParam("token") String token,
            RedirectAttributes ra) throws UnsupportedEncodingException {

        Locale locale = request.getLocale();
        String result = userService.validateVerificationToken(token);

        if ("valid".equals(result)) {
            User user = userService.getUser(token);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                session.setAttribute("user1", user);
            }
            authWithoutPassword(user);
            LOGGER.info("User authenticated: {}", user.getEmail());
            return "redirect:/Clientes/rechercher-reserver.html?user=" + user.getEmail();
        }

        model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    // ==================== RESEND TOKEN ====================

    @GetMapping("/user/resendRegistrationToken")
    @ResponseBody
    public GenericResponse resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String existingToken) {
        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, user));
        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    // ==================== RESET PASSWORD ====================

    @PostMapping("/user/resetPassword")
    @ResponseBody
    public GenericResponse resetPassword(
            HttpServletRequest request,
            @RequestParam("email") String userEmail,
            Locale locale) {

        User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            String[] body = new String[5];
            body[0] = messages.getMessage("message.registration.tete", null, locale) + " " + user.getFirstName() + ",";
            body[1] = messages.getMessage("message.resetPassword", null, locale);
            body[2] = messages.getMessage("message.resetPassword.text", null, locale);
            body[3] = messages.getMessage("message.registration.fin", null, locale);
            body[4] = messages.getMessage("message.resetPassword.text1", null, locale);

            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            Path headerPath = Paths.get("header.jpg");
            mailClient.prepareAndSend(constructResetTokenEmail(getAppUrl(request), locale, token, user),
                    headerPath.getFileName().toString(), "header.jpg", body);
        }

        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    // ==================== CHANGE PASSWORD (TOKEN VALIDATION) ====================

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("id") long id,
                                         @RequestParam("token") String token) {
        String result = securityUserService.validatePasswordResetToken(id, token);
        if (result != null) {
            model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
            return "redirect:/login?lang=" + locale.getLanguage();
        }
        return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }

    // ==================== SAVE NEW PASSWORD ====================

    @PostMapping("/user/savePassword")
    @ResponseBody
    public GenericResponse savePassword(Locale locale, @Valid PasswordDto passwordDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    }

    // ==================== UPDATE PASSWORD ====================

    @PostMapping("/user/updatePassword")
    @ResponseBody
    public GenericResponse changeUserPassword(Locale locale, @Valid PasswordDto passwordDto) {
        User user = userService.findUserByEmail(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    // ==================== UPDATE 2FA ====================

    @PostMapping("/user/update/2fa")
    @ResponseBody
    public GenericResponse modifyUser2FA(@RequestParam("use2FA") boolean use2FA) throws UnsupportedEncodingException {
        User user = userService.updateUser2FA(use2FA);
        return use2FA ? new GenericResponse(userService.generateQRUrl(user)) : null;
    }

    // ==================== UTIL ====================

    private void authWithoutPassword(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();
    }

    private SimpleMailMessage constructResendVerificationTokenEmail(
            String contextPath, Locale locale, VerificationToken newToken, User user) {
        String confirmationUrl = contextPath + "/registrationConfirm?token=" + newToken.getToken();
        String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + "\r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, User user) {
        String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + "\r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}
