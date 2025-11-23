package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Services.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/visit")
@RequiredArgsConstructor
public class TenantVisitDecisionController {
    private final VisitService visitService;

    @PostMapping("/approve/{visitId}")
    public ResponseEntity<Void> approve(@PathVariable Long visitId) {
        visitService.approveVisit(visitId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{visitId}")
    public ResponseEntity<Void> reject(@PathVariable Long visitId) {
        visitService.rejectVisit(visitId);
        return ResponseEntity.ok().build();
    }
}
