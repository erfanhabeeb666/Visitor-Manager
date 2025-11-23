package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateTenantDto;
import com.erfan.VisitorManagement.Dtos.TenantDto;
import com.erfan.VisitorManagement.Models.Tenant;
import com.erfan.VisitorManagement.Services.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminTenantController {
    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantDto> create(@Valid @RequestBody CreateTenantDto dto){
        Tenant created = tenantService.createTenant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(TenantDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDto> update(@PathVariable Long id, @Valid @RequestBody CreateTenantDto dto){
        Tenant updated = tenantService.updateTenant(id, dto);
        return ResponseEntity.ok(TenantDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TenantDto>> listAll(){
        List<TenantDto> response = tenantService.listAll()
                .stream()
                .map(TenantDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}

