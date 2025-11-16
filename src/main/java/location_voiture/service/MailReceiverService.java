package location_voiture.service;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.Properties;

@Service
public class MailReceiverService {

    @Value("${mail.imap.host}")
    private String imapHost;

    @Value("${mail.imap.port}")
    private String imapPort;

    @Value("${mail.imap.username}")
    private String imapUsername;

    @Value("${mail.imap.password}")
    private String imapPassword;

    public void receiveEmails() {
        try {
            Properties properties = new Properties();
            properties.put("mail.imap.host", imapHost);
            properties.put("mail.imap.port", imapPort);
            properties.put("mail.imap.ssl.enable", "true");

            Session session = Session.getDefaultInstance(properties);

            // Se connecter à la boîte mail via IMAP
            Store store = session.getStore("imap");
            store.connect(imapHost, imapUsername, imapPassword);

            // Accéder à la boîte de réception (Inbox)
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // Récupérer les messages
            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
            }

            // Fermer les ressources
            folder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

