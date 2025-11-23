package ma.abisoft.service;

import org.springframework.lang.Nullable;
import com.google.common.base.Strings;
import ma.abisoft.persistence.dao.DeviceMetadataRepository;
import ma.abisoft.persistence.model.DeviceMetadata;
import ma.abisoft.persistence.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static java.util.Objects.nonNull;

@Component
public class DeviceService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MailClient mailClient;

    private static final String UNKNOWN = "UNKNOWN";

    @Value("${support.email}")
    private String from;

    private final DeviceMetadataRepository deviceMetadataRepository;
    private final Parser parser;
    private final JavaMailSender mailSender;
    private final MessageSource messages;

    // DatabaseReader supprimé car non utilisé
    public DeviceService(DeviceMetadataRepository deviceMetadataRepository,
                         Parser parser,
                         JavaMailSender mailSender,
                         MessageSource messages) {
        this.deviceMetadataRepository = deviceMetadataRepository;
        this.parser = parser;
        this.mailSender = mailSender;
        this.messages = messages;
    }

    public void verifyDevice(User user, HttpServletRequest request) {
        String ip = extractIp(request);
        logger.info("Adresse IP: {}", ip);

        // Utilisation d'une valeur par défaut pour la localisation
        String location = "UNKNOWN_LOCATION";
        String deviceDetails = getDeviceDetails(request.getHeader("user-agent"));

        DeviceMetadata existingDevice = findExistingDevice(user.getId(), deviceDetails, location);

        if (Objects.isNull(existingDevice)) {
            unknownDeviceNotification(deviceDetails, location, ip, user.getEmail(), request.getLocale());

            DeviceMetadata deviceMetadata = new DeviceMetadata();
            deviceMetadata.setUserId(user.getId());
            deviceMetadata.setLocation(location);
            deviceMetadata.setDeviceDetails(deviceDetails);
            deviceMetadata.setLastLoggedIn(new Date());
            deviceMetadataRepository.save(deviceMetadata);
        } else {
            existingDevice.setLastLoggedIn(new Date());
            deviceMetadataRepository.save(existingDevice);
        }
    }

    private String extractIp(HttpServletRequest request) {
        String clientXForwardedForIp = request.getHeader("x-forwarded-for");
        return nonNull(clientXForwardedForIp) ? parseXForwardedHeader(clientXForwardedForIp)
                                              : request.getRemoteAddr();
    }

    private String parseXForwardedHeader(String header) {
        return header.split(" *, *")[0];
    }

    private String getDeviceDetails(String userAgent) {
        if (userAgent == null) return UNKNOWN;

        Client client = parser.parse(userAgent);
        if (nonNull(client)) {
            return client.userAgent.family + " " + client.userAgent.major + "." + client.userAgent.minor +
                   " - " + client.os.family + " " + client.os.major + "." + client.os.minor;
        }
        return UNKNOWN;
    }

    private DeviceMetadata findExistingDevice(Long userId, String deviceDetails, String location) {
        return deviceMetadataRepository.findByUserId(userId)
                .stream()
                .filter(d -> d.getDeviceDetails().equals(deviceDetails) && d.getLocation().equals(location))
                .findFirst()
                .orElse(null);
    }

    private void unknownDeviceNotification(String deviceDetails, String location, String ip, String email, Locale locale) {
        final String subject = "New Login Notification";
        String text = messages.getMessage("message.login.notification.deviceDetails", null, locale) +
                      " " + deviceDetails + "\n" +
                      messages.getMessage("message.login.notification.location", null, locale) +
                      " " + location + "\n" +
                      messages.getMessage("message.login.notification.ip", null, locale) +
                      " " + ip;

        // Envoi du mail (décommenter si nécessaire)
        // mailClient.prepareAndSend(email, text, subject);
    }
}
