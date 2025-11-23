package com.erfan.VisitorManagement.Models;

import com.erfan.VisitorManagement.Enums.VisitStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String visitorName;
    private String visitorPhone;

    private LocalDateTime visitDateTime;
    private LocalDateTime expiryTime;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_security_id")
    private Security checkedInSecurity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_out_security_id")
    private Security checkedOutSecurity;

    @Enumerated(EnumType.STRING)
    private VisitStatus status;

    private String qrCodePath;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
