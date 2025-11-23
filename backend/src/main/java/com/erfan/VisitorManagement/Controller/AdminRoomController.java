package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateRoomDto;
import com.erfan.VisitorManagement.Dtos.RoomDto;
import com.erfan.VisitorManagement.Models.Room;
import com.erfan.VisitorManagement.Services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminRoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> create(@Valid @RequestBody CreateRoomDto dto){
        Room created = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoomDto.fromEntity(created));
    }

    @GetMapping("/by-floor/{floorId}")
    public ResponseEntity<List<RoomDto>> listByFloor(@PathVariable Long floorId){
        List<RoomDto> response = roomService.listRooms(floorId)
                .stream()
                .map(RoomDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
