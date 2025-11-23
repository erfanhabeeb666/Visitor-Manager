package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateBuildingDto;
import com.erfan.VisitorManagement.Dtos.BuildingDto;
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
    public ResponseEntity<BuildingDto> create(@Valid @RequestBody CreateBuildingDto dto){
        Building created = buildingService.createBuilding(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(BuildingDto.fromEntity(created));
    }

    @GetMapping
    public ResponseEntity<Page<BuildingDto>> list(Pageable pageable){
        Page<BuildingDto> page = buildingService.listBuildings(pageable).map(BuildingDto::fromEntity);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingDto> update(@PathVariable Long id, @Valid @RequestBody CreateBuildingDto dto){
        Building updated = buildingService.updateBuilding(id, dto);
        return ResponseEntity.ok(BuildingDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
