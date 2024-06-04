package pt.migfonseca.vibecheck.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DotenvConfig {

    private final Environment environment;

    public DotenvConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Dotenv dotenv() {
        String activeProfile = environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0] : "default";
        String dotenvFile = String.format(".env.%s", activeProfile);

        return Dotenv.configure()
                     .filename(dotenvFile)
                     .load();
    }
}
