package com.erfan.VisitorManagement.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "whatsapp")
@Getter
@Setter
public class WhatsAppProperties {
    private String baseUrl; // https://graph.facebook.com/v20.0/
    private String phoneNumberId;
    private String token;
    private String appSecret;
    private boolean enabled; // feature flag to perform real calls
    private String publicBaseUrl; // e.g., https://<ngrok-id>.ngrok.io
}
