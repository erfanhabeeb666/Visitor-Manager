package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Config.WhatsAppProperties;
import com.erfan.VisitorManagement.Dtos.WhatsAppWebhookDto;
import com.erfan.VisitorManagement.Models.WhatsAppMessage;
import com.erfan.VisitorManagement.Repos.WhatsAppMessageRepository;
import com.erfan.VisitorManagement.Services.WebhookProcessor;
import com.erfan.VisitorManagement.Utils.HmacSignatureVerifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhook/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {
    private final WhatsAppProperties props;
    private final WhatsAppMessageRepository messageRepository;
    private final WebhookProcessor processor;

    @GetMapping
    public ResponseEntity<String> verify(@RequestParam(name = "hub.mode", required = false) String mode,
                                         @RequestParam(name = "hub.verify_token", required = false) String token,
                                         @RequestParam(name = "hub.challenge", required = false) String challenge) {
        if ("subscribe".equals(mode) && token != null && token.equals(props.getAppSecret())) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Verification failed");
    }

    @PostMapping
    public ResponseEntity<Void> receive(HttpServletRequest request,
                                        @RequestHeader(name = "X-Hub-Signature-256", required = false) String sig) throws IOException {
        String body;
        try (BufferedReader reader = request.getReader()) {
            body = reader.lines().collect(Collectors.joining("\n"));
        }
        if (!HmacSignatureVerifier.verifySha256(props.getAppSecret(), body, sig)) {
            log.warn("Invalid webhook signature");
            return ResponseEntity.status(401).build();
        }
        // persist raw
        WhatsAppMessage msg = WhatsAppMessage.builder()
                .rawPayload(body)
                .status("received")
                .build();
        messageRepository.save(msg);

        // parse simplified format for local testing; real Meta payload parsing can be added
        WhatsAppWebhookDto dto = new WhatsAppWebhookDto();
        if (body.contains("interactive")) {
            // very naive demo parser
            String id = body.replaceAll(".*\\\"id\\\":\\\\\\\"(.*?)\\\\\\\".*", "$1");
            dto.setInteractiveReplyId(id);
        } else {
            String from = body.replaceAll(".*\\\"from\\\":\\\\\\\"(.*?)\\\\\\\".*", "$1");
            String text = body.replaceAll(".*\\\"body\\\":\\\\\\\"(.*?)\\\\\\\".*", "$1");
            dto.setFromPhone(from);
            dto.setMessageBody(text);
        }
        dto.setProviderPayload(body);

        // async could be used; for MVP, process in a new thread
        new Thread(() -> {
            try {
                processor.process(dto);
                msg.setStatus("processed");
            } catch (Exception ex) {
                msg.setStatus("failed");
                log.error("Processing failed", ex);
            } finally {
                messageRepository.save(msg);
            }
        }).start();

        return ResponseEntity.ok().build();
    }
}
