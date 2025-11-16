package ma.abisoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Génère le contenu HTML du mail via Thymeleaf
     * @param messages tableau de messages à afficher dans le mail
     * @param url un texte ou lien à afficher (ex: texte du mail)
     * @param imageResourceName identifiant de l'image inline (ex: "logo")
     * @return contenu HTML final
     */
    public String build(String[] messages, String url, String imageResourceName) {
        Context context = new Context();

        // Id de l'image inline (cid)
        context.setVariable("imageResourceName", imageResourceName);

        // Injecter messages en variables message0, message1 ...
        for (int i = 0; i < messages.length; i++) {
            context.setVariable("message" + i, messages[i]);
        }

        context.setVariable("url", url);

        return templateEngine.process("Admin/email_templates/action", context);
    }
}
