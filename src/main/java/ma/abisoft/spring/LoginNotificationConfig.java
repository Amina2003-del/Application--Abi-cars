package ma.abisoft.spring;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua_parser.Parser;

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
        InputStream database = getClass().getClassLoader()
                .getResourceAsStream("maxmind/GeoLite2-City.mmdb");
        if (database == null) {
            throw new IOException("GeoLite2-City.mmdb not found in classpath");
        }
        return new DatabaseReader.Builder(database).build();
    }
}
