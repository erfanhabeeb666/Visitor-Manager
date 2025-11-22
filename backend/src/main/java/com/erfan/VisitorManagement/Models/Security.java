package com.erfan.VisitorManagement.Models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SECURITY")
public class Security extends User{

    private String adharUid;
    Security(){

    }
}
