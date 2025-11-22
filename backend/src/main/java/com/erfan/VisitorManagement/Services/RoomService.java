package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Dtos.CreateRoomDto;
import com.erfan.VisitorManagement.Exception.BadRequestException;
import com.erfan.VisitorManagement.Exception.ResourceNotFoundException;
import com.erfan.VisitorManagement.Models.Floor;
import com.erfan.VisitorManagement.Models.Room;
import com.erfan.VisitorManagement.Repos.FloorRepository;
import com.erfan.VisitorManagement.Repos.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepo;
    private final FloorRepository floorRepo;

    public Room createRoom(CreateRoomDto dto) {
        if (!floorRepo.existsById(dto.getFloorId())) throw new ResourceNotFoundException("Floor");
        if (roomRepo.existsByFloorIdAndRoomNumber(dto.getFloorId(), dto.getRoomNumber()))
            throw new BadRequestException("Room already exists in this floor");

        Floor floor = floorRepo.getReferenceById(dto.getFloorId());
        Room r = Room.builder().roomNumber(dto.getRoomNumber()).floor(floor).build();
        return roomRepo.save(r);
    }

    public List<Room> listRooms(Long floorId) {
        return roomRepo.findByFloorId(floorId);
    }
}
