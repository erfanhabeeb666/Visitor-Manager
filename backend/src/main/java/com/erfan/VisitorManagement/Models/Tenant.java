package com.erfan.VisitorManagement.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantName;
    private String phoneNumber;
    private Boolean active;

    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
