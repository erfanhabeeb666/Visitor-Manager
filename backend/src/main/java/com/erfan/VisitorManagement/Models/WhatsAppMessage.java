package com.erfan.VisitorManagement.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromPhone;
    private String messageType; // text, interactive, image, etc.

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    private String providerMessageId;
    private String status; // received, processed, failed

    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        if (receivedAt == null) receivedAt = LocalDateTime.now();
    }
}
