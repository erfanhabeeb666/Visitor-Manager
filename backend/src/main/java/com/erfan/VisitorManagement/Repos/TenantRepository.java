package com.erfan.VisitorManagement.Repos;

import com.erfan.VisitorManagement.Models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.List;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByPhoneNumber(String phone);
    boolean existsByRoomId(Long roomId);

    @Override
    @EntityGraph(attributePaths = {"building", "floor", "room"})
    List<Tenant> findAll();

    @EntityGraph(attributePaths = {"building", "floor", "room"})
    Optional<Tenant> findById(Long id);
}
