package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateFloorDto;
import com.erfan.VisitorManagement.Dtos.FloorDto;
import com.erfan.VisitorManagement.Models.Floor;
import com.erfan.VisitorManagement.Services.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/floors")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminFloorController {
    private final FloorService floorService;

    @PostMapping
    public ResponseEntity<FloorDto> create(@Valid @RequestBody CreateFloorDto dto){
        Floor created = floorService.createFloor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(FloorDto.fromEntity(created));
    }

    @GetMapping("/by-building/{buildingId}")
    public ResponseEntity<List<FloorDto>> listByBuilding(@PathVariable Long buildingId){
        List<FloorDto> response = floorService.getFloorsForBuilding(buildingId)
                .stream()
                .map(FloorDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}

