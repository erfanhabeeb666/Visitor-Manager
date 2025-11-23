package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateVisitDto;
import com.erfan.VisitorManagement.Dtos.VisitResponseDto;
import com.erfan.VisitorManagement.Enums.VisitStatus;
import com.erfan.VisitorManagement.Models.Visit;
import com.erfan.VisitorManagement.Repos.VisitRepository;
import com.erfan.VisitorManagement.Repos.spec.VisitSpecifications;
import com.erfan.VisitorManagement.Services.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;
    private final VisitRepository visitRepository;

    @PostMapping
    public ResponseEntity<VisitResponseDto> create(@Valid @RequestBody CreateVisitDto dto) {
        return ResponseEntity.ok(visitService.createVisit(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitResponseDto> get(@PathVariable Long id) {
        Visit v = visitRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(VisitResponseDto.fromEntity(v));
    }

    @GetMapping
    public ResponseEntity<Page<VisitResponseDto>> list(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) VisitStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Specification<Visit> spec = Specification
                .where(VisitSpecifications.hasTenantId(tenantId))
                .and(VisitSpecifications.hasStatus(status))
                .and(VisitSpecifications.visitBetween(from, to));
        Page<Visit> page = visitRepository.findAll(spec, pageable);
        Page<VisitResponseDto> mapped = page.map(VisitResponseDto::fromEntity);
        return ResponseEntity.ok(mapped);
    }
}
