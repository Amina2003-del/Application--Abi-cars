package ma.abisoft.spring;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua_parser.Parser;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class LoginNotificationConfig {

    @Bean
    public Parser uaParser() throws IOException {
        return new Parser();
    }

    @Bean
public DatabaseReader databaseReader() throws IOException {
    InputStream database = new ClassPathResource("maxmind/GeoLite2-City.mmdb").getInputStream();
    return new DatabaseReader.Builder(database).build();
}

}
