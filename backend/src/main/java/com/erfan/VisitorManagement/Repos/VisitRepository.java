package com.erfan.VisitorManagement.Repos;


import com.erfan.VisitorManagement.Enums.VisitStatus;
import com.erfan.VisitorManagement.Models.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {

    List<Visit> findByTenantIdOrderByVisitDateTimeDesc(Long tenantId);

    List<Visit> findByStatus(VisitStatus status);

    List<Visit> findByExpiryTimeAfter(LocalDateTime now);

    List<Visit> findByStatusAndExpiryTimeAfter(VisitStatus status, LocalDateTime now);

    List<Visit> findByStatusAndExpiryTimeBefore(VisitStatus status, LocalDateTime now);

    List<Visit> findByBuildingId(Long buildingId);

    List<Visit> findByVisitDateTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<Visit> findByQrCodePath(String qrPath);

    List<Visit> findByStatusIn(List<VisitStatus> pending);

    long deleteByCreatedAtBefore(LocalDateTime cutoff);
}

