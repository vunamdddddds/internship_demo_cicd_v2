package com.example.InternShip.service;

import com.example.InternShip.dto.allowancepackage.request.CreateAllowancePackageRequest;
import com.example.InternShip.dto.allowancepackage.response.AllowancePackageResponse;
import com.example.InternShip.entity.AllowancePackage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AllowancePackageService {
    AllowancePackageResponse createAllowancePackage(CreateAllowancePackageRequest request);
    AllowancePackageResponse getAllowancePackageById(Integer id);
    Page<AllowancePackageResponse> getAllAllowancePackages(String keyword, Pageable pageable);
    AllowancePackageResponse updateAllowancePackage(Integer id, CreateAllowancePackageRequest request);
    void deleteAllowancePackage(Integer id);
    List<AllowancePackageResponse> getActiveAllowancePackagesByProgramId(Integer internshipProgramId);
    List<AllowancePackage> findActiveAllowancePackageEntitiesByProgramId(Integer internshipProgramId);
}
