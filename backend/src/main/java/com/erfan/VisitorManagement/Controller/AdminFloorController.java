package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateFloorDto;
import com.erfan.VisitorManagement.Models.Floor;
import com.erfan.VisitorManagement.Services.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/floors")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminFloorController {
    private final FloorService floorService;

    @PostMapping
    public ResponseEntity<Floor> create(@Valid @RequestBody CreateFloorDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(floorService.createFloor(dto));
    }

    @GetMapping("/by-building/{buildingId}")
    public ResponseEntity<List<Floor>> listByBuilding(@PathVariable Long buildingId){
        return ResponseEntity.ok(floorService.getFloorsForBuilding(buildingId));
    }
}

