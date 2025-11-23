package com.erfan.VisitorManagement.Dtos;

import com.erfan.VisitorManagement.Models.Room;

public class RoomDto {
    public Long id;
    public String roomNumber;
    public Long floorId;

    public static RoomDto fromEntity(Room r) {
        RoomDto dto = new RoomDto();
        dto.id = r.getId();
        dto.roomNumber = r.getRoomNumber();
        dto.floorId = r.getFloor() != null ? r.getFloor().getId() : null;
        return dto;
    }
}
