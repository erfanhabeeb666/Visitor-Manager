package com.erfan.VisitorManagement.Models;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public Admin(Long id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public Admin() {
    }
}
