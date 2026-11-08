package ph.gov.tabunok.drrm.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class
AppProperties {

    private String uploadDir = "uploads";
    /** Puroks of Barangay Tabunok offered in forms. Adjust in application.properties. */
    private List<String> puroks = List.of();
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Seed seed = new Seed();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMinutes = 480;
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:5173");
    }

    @Getter
    @Setter
    public static class Seed {
        private String adminEmail;
        private String adminPassword;
    }
}
