package location_voiture.service;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import location_voiture.persistence.model.Entretien;
import location_voiture.persistence.model.Propritaire;

@Service
public class EmailService {


    @Autowired
    private JavaMailSender mailSender;

    public void envoyerFactureAvecPDF(String to, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Votre facture de location");

        helper.setText("Bonjour, \n\nVeuillez trouver ci-joint votre facture de location.\n\nCordialement,\nL'équipe");

        ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
        helper.addAttachment("facture-location.pdf", dataSource);

        mailSender.send(message);
    }

    public void envoyerEmail(String to, String sujet, String contenu) {
        MimeMessage message = mailSender.createMimeMessage();  // <-- ici mailSender doit être non null

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false); // false = pas de pièce jointe
            helper.setTo(to);
            helper.setFrom("contact@aladintours.com");
            helper.setSubject(sujet);
            helper.setText(contenu, false); // false = contenu en texte brut, true = HTML
            
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Tu peux logger ou gérer l'exception comme tu veux ici
        }
    }
    public void envoyerMessageAuAdmin(String nom, String emailVisiteur, String messageVisiteur) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("contact@aladintours.com"); // doit être un vrai compte configuré
        helper.setTo("contact@aladintours.com");   // l’admin reçoit
        helper.setSubject("📩 Nouveau message de contact de " + nom);

        String contenuHtml = "<h2>Nouveau message de contact</h2>"
                           + "<p><strong>Nom :</strong> " + nom + "</p>"
                           + "<p><strong>Email :</strong> " + emailVisiteur + "</p>"
                           + "<p><strong>Message :</strong><br/>" + messageVisiteur + "</p>";

        helper.setText(contenuHtml, true); // true = HTML
        mailSender.send(message);
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact@aladintours.com");  // bien définir l’expéditeur !
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void envoyerContrat(String email, String nom, File fichierContrat) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("contact@aladintours.com"); // doit être un vrai compte SMTP
            helper.setTo(email);
            helper.setSubject("🤝 Votre partenariat avec ALAEDIN TOURS est accepté");

            String contenuHtml = "<h2 style='color:#2E86C1;'>Contrat de Partenariat Accepté</h2>"
                    + "<p>Bonjour <strong>" + nom + "</strong>,</p>"
                    + "<p>Nous avons le plaisir de vous informer que votre demande de partenariat a été <strong style='color:green;'>acceptée</strong> par notre équipe.</p>"
                    + "<p>Veuillez trouver ci-joint votre contrat de partenariat officiel, à conserver pour vos dossiers.</p>"
                    + "<p style='margin-top:20px;'>Nous vous remercions de la confiance que vous accordez à <strong>ALAEDIN TOURS</strong>.</p>"
                    + "<p>Cordialement,<br/>L’équipe ALAEDIN TOURS</p>"
                    + "<hr style='margin-top:30px;'/>"
                    + "<p style='font-size:12px; color:gray;'>125 Avenue des Bureaux de Printemp, 3000 FES, Maroc<br/>"
                    + "Téléphone : 07 13 61 53 81 | Email : contact@aladintours.com</p>";

            helper.setText(contenuHtml, true); // HTML enabled
            helper.addAttachment(fichierContrat.getName(), fichierContrat);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            // Tu peux logger l'erreur ou la relancer en tant qu'exception personnalisée
        }
    }

    public void envoyerRappelEntretien(Entretien entretien) {
        Propritaire proprietaire = entretien.getCar().getProprietaire();
        if (proprietaire != null && proprietaire.getUser() != null) {
            String emailProprietaire = proprietaire.getUser().getEmail(); // email réel

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailProprietaire);
            message.setSubject("🔧 Rappel d'entretien pour votre véhicule");

            String content = "Bonjour " + proprietaire.getUser().getFirstName() + ",\n\n"
                + "Un entretien est prévu pour votre véhicule "
                + entretien.getCar().getImmatriculation() + " le "
                + entretien.getProchaineDateEstimee() + ".\n"
                + "Merci de prendre les dispositions nécessaires.\n\n"
                + "Garage AutoService";

            message.setText(content);
            mailSender.send(message);
        } else {
            System.out.println("Propriétaire ou utilisateur associé introuvable pour l'entretien ID " + entretien.getId());
        }
    }


    public void sendPasswordEmail(String email, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("contact@aladintours.com");
            helper.setTo(email);
            helper.setSubject("📧 Votre mot de passe temporaire");

            String contenuHtml = "<h2>Bienvenue chez ALAEDIN TOURS</h2>"
                    + "<p>Bonjour,</p>"
                    + "<p>Un nouveau compte a été créé pour vous. Votre mot de passe temporaire est : <strong>" + password + "</strong>.</p>"
                    + "<p>Nous vous recommandons de vous connecter et de modifier ce mot de passe dès que possible pour des raisons de sécurité.</p>"
                    + "<p>Cordialement,<br/>L’équipe ALAEDIN TOURS</p>"
                    + "<hr style='margin-top:30px;'/>"
                    + "<p style='font-size:12px; color:gray;'>125 Avenue des Bureaux de Printemp, 3000 FES, Maroc<br/>"
                    + "Téléphone : 07 13 61 53 81 | Email : contact@aladintours.com</p>";

            helper.setText(contenuHtml, true); // HTML enabled
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Vous pouvez ajouter une gestion d'erreur plus robuste ici (logging, réessai, etc.)
        }
    }



}
