package ma.abisoft.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailClient {

    private JavaMailSender mailSender;
    private MailContentBuilder mailContentBuilder;

    @Value("${support.email}")
    private String from;

    @Autowired
    public MailClient(JavaMailSender mailSender, MailContentBuilder mailContentBuilder) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    /**
     * Prépare et envoie un mail HTML avec image inline et pièce jointe
     * @param simpleMailMessage message simple avec sujet et destinataire
     * @param imageResourceName id de l'image inline (cid)
     * @param imageFileName chemin relatif dans classpath vers image (ex: "static/img/logo.png")
     * @param messages tableau de messages à injecter dans template
     */
    public void prepareAndSend(SimpleMailMessage simpleMailMessage, String imageResourceName, String imageFileName, String[] messages) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(from);
            messageHelper.setTo(simpleMailMessage.getTo());
            messageHelper.setSubject(simpleMailMessage.getSubject());

            // Image inline (logo)
            messageHelper.addInline(imageResourceName, new ClassPathResource(imageFileName));

            // Générer contenu HTML via Thymeleaf
            String content = mailContentBuilder.build(messages, simpleMailMessage.getText(), imageResourceName);

            messageHelper.setText(content, true);

            // Pièce jointe (optionnelle)
            messageHelper.addAttachment("logo.svg", new ClassPathResource("static/img/logo.svg"));
        };

        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

	

	public void prepareAndSend(SimpleMailMessage mailToOwner) {
		// TODO Auto-generated method stub
		
	}
}
