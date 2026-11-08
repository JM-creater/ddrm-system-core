package ph.gov.tabunok.drrm.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

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
    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {
        private boolean enabled = true;
        private String endpoint;
        private String bucket;
        private String accessKey;
        private String secretKey;
        private String region = "auto";
        private boolean pathStyleAccess = false;
        private String publicUrl;
        private long presignMinutes = 1440;

        public boolean isConfigured() {
            return enabled
                    && StringUtils.hasText(endpoint)
                    && StringUtils.hasText(bucket)
                    && StringUtils.hasText(accessKey)
                    && StringUtils.hasText(secretKey);
        }
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMinutes = 480;
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = List.of("*");
    }

    @Getter
    @Setter
    public static class Seed {
        private String adminEmail;
        private String adminPassword;
    }
}
