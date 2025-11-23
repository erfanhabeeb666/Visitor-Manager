package com.erfan.VisitorManagement.Dtos;

import com.erfan.VisitorManagement.Enums.Status;
import com.erfan.VisitorManagement.Models.Tenant;

public class TenantDto {
    public Long id;
    public String tenantName;
    public String phoneNumber;
    public Status status;
    public Long buildingId;
    public String buildingName;
    public Long floorId;
    public Integer floorNumber;
    public Long roomId;
    public String roomNumber;

    public static TenantDto fromEntity(Tenant t) {
        TenantDto dto = new TenantDto();
        dto.id = t.getId();
        dto.tenantName = t.getTenantName();
        dto.phoneNumber = t.getPhoneNumber();
        dto.status = t.getStatus();
        if (t.getBuilding() != null) {
            dto.buildingId = t.getBuilding().getId();
            dto.buildingName = t.getBuilding().getName();
        }
        if (t.getFloor() != null) {
            dto.floorId = t.getFloor().getId();
            dto.floorNumber = t.getFloor().getFloorNumber();
        }
        if (t.getRoom() != null) {
            dto.roomId = t.getRoom().getId();
            dto.roomNumber = t.getRoom().getRoomNumber();
        }
        return dto;
    }
}
