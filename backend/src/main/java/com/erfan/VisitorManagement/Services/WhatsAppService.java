package com.erfan.VisitorManagement.Services;

public interface WhatsAppService {
    void sendText(String toPhone, String text);
    void sendTemplateWithButtons(String toPhone, String text, String[] buttons);
    void sendImage(String toPhone, String imageUrl);
}
