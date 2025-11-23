package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Config.WhatsAppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppServiceImpl implements WhatsAppService {
    private final WhatsAppProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    private void postWithRetry(String path, String bodyJson) {
        if (!props.isEnabled()) {
            log.info("[MOCK] WhatsApp POST {} => {}", path, bodyJson);
            return;
        }
        String url = props.getBaseUrl() + props.getPhoneNumberId() + path;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.getToken());
        HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);
        int attempts = 0; long backoff = 500;
        while (attempts < 3) {
            attempts++;
            try {
                ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                if (res.getStatusCode().is2xxSuccessful()) {
                    log.info("WhatsApp success: {}", res.getBody());
                    return;
                }
                if (res.getStatusCode().is5xxServerError()) throw new RuntimeException("5xx from provider");
                log.warn("WhatsApp non-2xx: {} {}", res.getStatusCode(), res.getBody());
                return;
            } catch (Exception ex) {
                log.warn("WhatsApp send failed attempt {}: {}", attempts, ex.getMessage());
                try { Thread.sleep(backoff); } catch (InterruptedException ignored) {}
                backoff *= 2;
            }
        }
        log.error("WhatsApp send failed after retries: {}", bodyJson);
    }

    @Override
    public void sendText(String toPhone, String text) {
        String body = "{\"messaging_product\":\"whatsapp\",\"to\":\""+toPhone+"\",\"type\":\"text\",\"text\":{\"body\":\""+text.replace("\"","\\\"")+"\"}}";
        postWithRetry("/messages", body);
    }

    @Override
    public void sendTemplateWithButtons(String toPhone, String text, String[] buttons) {
        // Using interactive message with 2 buttons
        String body = "{\"messaging_product\":\"whatsapp\",\"to\":\""+toPhone+"\",\"type\":\"interactive\",\"interactive\":{\"type\":\"button\",\"body\":{\"text\":\""+text.replace("\"","\\\"")+"\"},\"action\":{\"buttons\":[{\"type\":\"reply\",\"reply\":{\"id\":\""+buttons[0]+"\",\"title\":\"Approve\"}},{\"type\":\"reply\",\"reply\":{\"id\":\""+buttons[1]+"\",\"title\":\"Reject\"}}]}}}";
        postWithRetry("/messages", body);
    }

    @Override
    public void sendImage(String toPhone, String imageUrl) {
        String body = "{\"messaging_product\":\"whatsapp\",\"to\":\""+toPhone+"\",\"type\":\"image\",\"image\":{\"link\":\""+imageUrl+"\"}}";
        postWithRetry("/messages", body);
    }
}
