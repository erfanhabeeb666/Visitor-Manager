package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateRoomDto;
import com.erfan.VisitorManagement.Models.Room;
import com.erfan.VisitorManagement.Services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminRoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> create(@Valid @RequestBody CreateRoomDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(dto));
    }

    @GetMapping("/by-floor/{floorId}")
    public ResponseEntity<List<Room>> listByFloor(@PathVariable Long floorId){
        return ResponseEntity.ok(roomService.listRooms(floorId));
    }
}
