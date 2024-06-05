package pt.migfonseca.vibecheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class VibeCheck {

    public static void main(String[] args) {
        SpringApplication.run(VibeCheck.class, args);
    }
}
