package com.erfan.VisitorManagement.Services;


import com.erfan.VisitorManagement.Dtos.CreateTenantDto;
import com.erfan.VisitorManagement.Enums.Status;
import com.erfan.VisitorManagement.Exception.ResourceNotFoundException;
import com.erfan.VisitorManagement.Models.Room;
import com.erfan.VisitorManagement.Models.Tenant;
import com.erfan.VisitorManagement.Repos.RoomRepository;
import com.erfan.VisitorManagement.Repos.TenantRepository;
import com.erfan.VisitorManagement.Services.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;

    @Override
    public Tenant createTenant(CreateTenantDto tenant) {
        Room room = roomRepository.getById(tenant.getRoomId());
        Tenant saveTenant=new Tenant();
        saveTenant.setTenantName(tenant.getTenantName());
        saveTenant.setStatus(Status.ACTIVE);
        saveTenant.setPhoneNumber(tenant.getPhoneNumber());
        saveTenant.setRoom(room);
        saveTenant.setBuilding(room.getFloor().getBuilding());
        saveTenant.setFloor(room.getFloor());
        return tenantRepository.save(saveTenant);
    }

    @Override
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Tenant updateTenant(Long id, CreateTenantDto updated) {

        Tenant existing = getTenantById(id);

        if (updated.getTenantName() != null && !updated.getTenantName().isBlank()) {
            existing.setTenantName(updated.getTenantName());
        }

        if (updated.getPhoneNumber() != null && !updated.getPhoneNumber().isBlank()) {
            existing.setPhoneNumber(updated.getPhoneNumber());
        }

        if (updated.getRoomId() != null) {
            Room room = roomRepository.getById(updated.getRoomId());
            existing.setRoom(room);
        }

        return tenantRepository.save(existing);
    }


    @Override
    public void deleteTenant(Long id) {
        Tenant tenant = getTenantById(id);
        tenant.setStatus(Status.INACTIVE);
        tenant.setRoom(null);
        tenantRepository.save(tenant);
    }

    @Override
    public List<Tenant> listAll() {
        return tenantRepository.findAll();
    }
}
