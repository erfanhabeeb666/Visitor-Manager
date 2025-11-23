package com.erfan.VisitorManagement.Models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("SECURITY")
@Data
public class Security extends User{
    private String adharUid;
    public Security(){

    }
}
