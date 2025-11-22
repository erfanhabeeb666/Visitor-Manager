package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Dtos.CreateTenantDto;
import com.erfan.VisitorManagement.Models.Tenant;

import java.util.List;

public interface TenantService {

    Tenant createTenant(CreateTenantDto tenant);

    Tenant getTenantById(Long id);

    List<Tenant> getAllTenants();

    Tenant updateTenant(Long id, CreateTenantDto tenant);

    void deleteTenant(Long id);

    List<Tenant> listAll();
}
