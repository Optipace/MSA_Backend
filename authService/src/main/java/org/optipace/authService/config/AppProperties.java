package org.optipace.authService.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@Slf4j
public class AppProperties {

    private Login login = new Login();
    private Jwt jwt = new Jwt();

    @Data
    public static class Login {
        private String fixedPassword;
    }

    @Data
    public static class Jwt {
        private String secret;
        private long expirationAccess;
        private long expirationRefresh;
    }

    @PostConstruct
    public void logPropertiesOnStartup() {
        log.info("✅ Login fixed password: {}", mask(login.getFixedPassword()));
        log.info("✅ JWT secret: {}", mask(jwt.getSecret()));
        log.info("✅ JWT access expiration (ms): {}", jwt.getExpirationAccess());
    }

    private String mask(String value) {
        if (value == null || value.length() < 4) return "****";
        int visible = 4;
        return "*".repeat(value.length() - visible) + value.substring(value.length() - visible);
    }
}

