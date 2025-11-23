package ma.abisoft.spring;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua_parser.Parser;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

@Configuration
public class LoginNotificationConfig {

    @Bean
    public Parser uaParser() throws IOException {
        return new Parser();
    }

    @Bean
    public DatabaseReader databaseReader() throws IOException {
        // Charger depuis le classpath
        ClassPathResource resource = new ClassPathResource("maxmind/GeoLite2-City.mmdb");

        // Copier dans un fichier temporaire
        File temp = File.createTempFile("GeoLite2-City", ".mmdb");
        temp.deleteOnExit();

        try (InputStream in = resource.getInputStream();
             OutputStream out = new FileOutputStream(temp)) {

            byte[] buffer = new byte[4096];
            int bytes;
            while ((bytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes);
            }
        }

        // DatabaseReader nécessite un File, pas InputStream
        return new DatabaseReader.Builder(temp).build();
    }
}
