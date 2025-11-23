package com.erfan.VisitorManagement.Dtos;
import com.erfan.VisitorManagement.Models.Visit;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VisitResponseDto {

    private Long id;
    private String visitorName;
    private String visitorPhone;

    private LocalDateTime visitDateTime;
    private LocalDateTime expiryTime;

    private String status;

    private String qrCodePath;

    private Long tenantId;
    private String tenantName;

    private Long buildingId;
    private String buildingName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String checkedInBy;
    private String checkedOutBy;

    private Long floorId;
    private Integer floorNumber;

    private Long roomId;
    private String roomNumber;
    public static VisitResponseDto fromEntity(Visit v) {
        String phone = v.getVisitorPhone();
        if (v.getExpiryTime() != null && v.getExpiryTime().isBefore(LocalDateTime.now())) {
            phone = maskPhone(phone);
        }
        return VisitResponseDto.builder()
                .id(v.getId())
                .visitorName(v.getVisitorName())
                .visitorPhone(phone)
                .visitDateTime(v.getVisitDateTime())
                .expiryTime(v.getExpiryTime())
                .status(v.getStatus().name())
                .qrCodePath(v.getQrCodePath())  // localfs path
                .tenantName(v.getTenant().getTenantName())
                .buildingName(v.getBuilding().getName())
                .floorNumber(v.getFloor().getFloorNumber())
                .checkInTime(v.getCheckInTime())
                .checkOutTime(v.getCheckOutTime())
                .checkedInBy(v.getCheckedInSecurity() != null
                        ? v.getCheckedInSecurity().getName()
                        : null)
                .checkedOutBy(v.getCheckedOutSecurity() != null
                        ? v.getCheckedOutSecurity().getName()
                        : null)

                .roomNumber(v.getRoom().getRoomNumber())
                .build();
    }

    private static String maskPhone(String phone) {
        if (phone == null) return null;
        int len = phone.length();
        if (len <= 2) return "**";
        String last2 = phone.substring(len - 2);
        return "*".repeat(len - 2) + last2;
    }
}
