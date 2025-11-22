package com.erfan.VisitorManagement.Repos;

import com.erfan.VisitorManagement.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByFloorId(Long floorId);
    boolean existsByFloorIdAndRoomNumber(Long floorId, String roomNumber);
}
