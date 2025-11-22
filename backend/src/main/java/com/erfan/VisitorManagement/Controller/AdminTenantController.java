package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateTenantDto;
import com.erfan.VisitorManagement.Models.Tenant;
import com.erfan.VisitorManagement.Services.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminTenantController {
    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<Tenant> create(@Valid @RequestBody CreateTenantDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> update(@PathVariable Long id, @Valid @RequestBody CreateTenantDto dto){
        return ResponseEntity.ok(tenantService.updateTenant(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Tenant>> listAll(){ return ResponseEntity.ok(tenantService.listAll()); }
}

