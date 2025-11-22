package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateBuildingDto;
import com.erfan.VisitorManagement.Models.Building;
import com.erfan.VisitorManagement.Services.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/buildings")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminBuildingController {
    private final BuildingService buildingService;

    @PostMapping
    public ResponseEntity<Building> create(@Valid @RequestBody CreateBuildingDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(buildingService.createBuilding(dto));
    }

    @GetMapping
    public ResponseEntity<Page<Building>> list(Pageable pageable){
        return ResponseEntity.ok(buildingService.listBuildings(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Building> update(@PathVariable Long id, @Valid @RequestBody CreateBuildingDto dto){
        return ResponseEntity.ok(buildingService.updateBuilding(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
