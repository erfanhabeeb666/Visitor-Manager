package com.erfan.VisitorManagement.Repos;

import com.erfan.VisitorManagement.Models.WhatsAppMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhatsAppMessageRepository extends JpaRepository<WhatsAppMessage, Long> {
}
