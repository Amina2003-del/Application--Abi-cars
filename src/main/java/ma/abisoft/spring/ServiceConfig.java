package ma.abisoft.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "ma.abisoft.service" })
public class ServiceConfig {
}
