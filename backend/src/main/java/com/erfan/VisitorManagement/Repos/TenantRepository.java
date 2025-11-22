package com.erfan.VisitorManagement.Repos;

import com.erfan.VisitorManagement.Models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByPhoneNumber(String phone);
    boolean existsByRoomId(Long roomId);
}
