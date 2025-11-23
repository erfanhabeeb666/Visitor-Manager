package com.erfan.VisitorManagement.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateSecurityUserDto {
    @NotBlank
    public String name;

    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String password;

    @NotBlank
    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar must be exactly 12 digits")
    public String adharUid; // matches field name in Security entity
}
