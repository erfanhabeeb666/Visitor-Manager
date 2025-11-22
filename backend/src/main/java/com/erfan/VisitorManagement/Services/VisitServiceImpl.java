package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Dtos.CreateVisitDto;
import com.erfan.VisitorManagement.Dtos.VisitResponseDto;
import com.erfan.VisitorManagement.Enums.VisitStatus;
import com.erfan.VisitorManagement.Exception.BadRequestException;
import com.erfan.VisitorManagement.Exception.ResourceNotFoundException;
import com.erfan.VisitorManagement.Models.*;
import com.erfan.VisitorManagement.Repos.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final TenantRepository tenantRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final QrCodeServiceImpl qrService;
    private final SecurityRepository securityRepository;

    @Override
    public VisitResponseDto createVisit(CreateVisitDto dto) {

        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (tenant.getRoom() == null || tenant.getFloor() == null || tenant.getBuilding() == null) {
            throw new BadRequestException("Tenant is not fully assigned to a building/floor/room");
        }

        Visit visit = Visit.builder()
                .visitorName(dto.getVisitorName())
                .visitorPhone(dto.getVisitorPhone())
                .visitDateTime(dto.getVisitDateTime())
                .expiryTime(dto.getExpiryTime())
                .status(VisitStatus.PENDING)
                .tenant(tenant)
                .building(tenant.getBuilding())
                .floor(tenant.getFloor())
                .room(tenant.getRoom())
                .build();

        Visit saved = visitRepository.save(visit);

        String qrPath = qrService.generateVisitQr(visit.getId());

        saved.setQrCodePath(qrPath);
        visitRepository.save(saved);

        return mapToDto(saved);
    }

    @Override
    public Visit approveVisit(Long visitId) {
        Visit visit = getVisit(visitId);

        if (visit.getStatus() != VisitStatus.PENDING) {
            throw new BadRequestException("Only pending visits can be approved");
        }

        visit.setStatus(VisitStatus.APPROVED);
        return visitRepository.save(visit);
    }

    @Override
    public Visit rejectVisit(Long visitId) {
        Visit visit = getVisit(visitId);

        if (visit.getStatus() != VisitStatus.PENDING) {
            throw new BadRequestException("Only pending visits can be rejected");
        }

        visit.setStatus(VisitStatus.REJECTED);
        return visitRepository.save(visit);
    }

    @Override
    public Visit checkIn(Long visitId, Long securityId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR Code"));

        if (visit.getStatus() != VisitStatus.APPROVED) {
            throw new BadRequestException("Only approved visits can check in");
        }

        Security security = securityRepository.findById(securityId)
                .orElseThrow(() -> new ResourceNotFoundException("Security not found"));

        visit.setStatus(VisitStatus.IN);
        visit.setCheckInTime(LocalDateTime.now());
        visit.setCheckedInSecurity(security);

        return visitRepository.save(visit);
    }


    @Override
    public Visit checkOut(Long visitId, Long securityId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR Code"));

        if (visit.getStatus() != VisitStatus.IN) {
            throw new BadRequestException("Visit must be checked-in before checkout");
        }

        Security security = securityRepository.findById(securityId)
                .orElseThrow(() -> new ResourceNotFoundException("Security not found"));

        visit.setStatus(VisitStatus.OUT);
        visit.setCheckOutTime(LocalDateTime.now());
        visit.setCheckedOutSecurity(security);

        return visitRepository.save(visit);
    }
    @Override
    @Transactional
    public Visit scanVisit(Long visitId, Long securityId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR Code"));

        Security security = securityRepository.findById(securityId)
                .orElseThrow(() -> new ResourceNotFoundException("Security not found"));

        // CASE 1 – Approved → Check-In
        if (visit.getStatus() == VisitStatus.APPROVED) {
            visit.setStatus(VisitStatus.IN);
            visit.setCheckInTime(LocalDateTime.now());
            visit.setCheckedInSecurity(security);
            return visitRepository.save(visit);
        }

        // CASE 2 – Already IN → Check-Out
        if (visit.getStatus() == VisitStatus.IN) {
            visit.setStatus(VisitStatus.OUT);
            visit.setCheckOutTime(LocalDateTime.now());
            visit.setCheckedOutSecurity(security);
            return visitRepository.save(visit);
        }

        // INVALID CASE
        throw new BadRequestException("Visit cannot be scanned for this status: " + visit.getStatus());
    }



    @Override
    public List<Visit> listActiveVisits() {
        return visitRepository.findByStatusIn(
                List.of(VisitStatus.PENDING, VisitStatus.APPROVED, VisitStatus.IN)
        );
    }

    // -------------------------------------------
    // Helpers
    // -------------------------------------------

    private Visit getVisit(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
    }

    private VisitResponseDto mapToDto(Visit visit) {
        return VisitResponseDto.builder()
                .id(visit.getId())
                .visitorName(visit.getVisitorName())
                .visitorPhone(visit.getVisitorPhone())
                .visitDateTime(visit.getVisitDateTime())
                .expiryTime(visit.getExpiryTime())
                .status(visit.getStatus().name())
                .qrCodePath(visit.getQrCodePath())
                .tenantName(visit.getTenant().getTenantName())
                .roomNumber(visit.getRoom().getRoomNumber())
                .floorNumber(visit.getFloor().getFloorNumber())
                .buildingName(visit.getBuilding().getName())
                .build();
    }

}
