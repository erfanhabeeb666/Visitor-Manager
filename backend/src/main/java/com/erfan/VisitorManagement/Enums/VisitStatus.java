package com.erfan.VisitorManagement.Enums;

public enum VisitStatus {
    PENDING,
    APPROVED,
    REJECTED,
    IN,        // security checked-in
    OUT,       // checked-out
    EXPIRED    // QR time expired
}
