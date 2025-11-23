package com.erfan.VisitorManagement.Dtos;

import lombok.Data;

@Data
public class WhatsAppWebhookDto {
    private String fromPhone;
    private String messageBody;
    private String interactiveReplyId; // e.g., APP_10 or REJ_10
    private String providerPayload;    // raw JSON string
}
