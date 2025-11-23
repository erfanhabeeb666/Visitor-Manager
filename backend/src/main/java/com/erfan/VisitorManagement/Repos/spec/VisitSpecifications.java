package com.erfan.VisitorManagement.Repos.spec;

import com.erfan.VisitorManagement.Enums.VisitStatus;
import com.erfan.VisitorManagement.Models.Visit;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VisitSpecifications {
    public static Specification<Visit> hasTenantId(Long tenantId) {
        return (root, query, cb) -> tenantId == null ? cb.conjunction() : cb.equal(root.get("tenant").get("id"), tenantId);
    }

    public static Specification<Visit> hasStatus(VisitStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Visit> visitBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("visitDateTime"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("visitDateTime"), from);
            return cb.lessThanOrEqualTo(root.get("visitDateTime"), to);
        };
    }
}
