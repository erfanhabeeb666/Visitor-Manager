package com.erfan.VisitorManagement.Dtos;

import com.erfan.VisitorManagement.Enums.Status;
import com.erfan.VisitorManagement.Models.Security;

public class SecurityUserDto {
    public Long id;
    public String name;
    public String email;
    public String adharUid;
    public Status status;

    public static SecurityUserDto fromEntity(Security s) {
        SecurityUserDto dto = new SecurityUserDto();
        dto.id = s.getId();
        dto.name = s.getName();
        dto.email = s.getEmail();
        dto.adharUid = s.getAdharUid();
        dto.status = s.getStatus();
        return dto;
    }
}
