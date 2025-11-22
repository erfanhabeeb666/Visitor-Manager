package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.VisitResponseDto;
import com.erfan.VisitorManagement.Models.Visit;
import com.erfan.VisitorManagement.Security.JwtService;
import com.erfan.VisitorManagement.Security.JwtUtils;
import com.erfan.VisitorManagement.Services.VisitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/security")
@PreAuthorize("hasAuthority('SECURITY')")
public class SecurityController {
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final JwtUtils jwtUtils;
    private final VisitService visitService;

    // ------------------------------
    // 1. QR CHECK-IN
    // ------------------------------
    @PostMapping("/check-in/{visitId}")
    public ResponseEntity<VisitResponseDto> checkIn(@PathVariable Long visitId) {
        Long securityId = Long.valueOf(jwtService.extractId(jwtUtils.getJwtFromRequest(request)));
        Visit visit = visitService.checkIn(visitId,securityId);
        VisitResponseDto dto = VisitResponseDto.fromEntity(visit);

        return ResponseEntity.ok(dto);
    }

    // ------------------------------
    // 2. QR CHECK-OUT
    // ------------------------------
    @PostMapping("/check-out/{visitId}")
    public ResponseEntity<VisitResponseDto> checkOut(@PathVariable Long visitId) {
        Long securityId = Long.valueOf(jwtService.extractId(jwtUtils.getJwtFromRequest(request)));
        Visit visit = visitService.checkOut(visitId,securityId);
        VisitResponseDto dto = VisitResponseDto.fromEntity(visit);

        return ResponseEntity.ok(dto);
    }
    @PostMapping("/scan/{visitId}")
    public ResponseEntity<VisitResponseDto> scanVisit(@PathVariable Long visitId) {

        Long securityId = Long.valueOf(
                jwtService.extractId(jwtUtils.getJwtFromRequest(request))
        );

        Visit visit = visitService.scanVisit(visitId, securityId);
        VisitResponseDto dto = VisitResponseDto.fromEntity(visit);

        return ResponseEntity.ok(dto);
    }


    // ------------------------------
    // 3. Security Dashboard: Active Visitors (Currently inside property)
    // ------------------------------
    @GetMapping("/active-visits")
    public ResponseEntity<List<VisitResponseDto>> listActiveVisits() {

        List<Visit> visits = visitService.listActiveVisits();
        List<VisitResponseDto> response = visits.stream()
                .map(VisitResponseDto::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }


}
