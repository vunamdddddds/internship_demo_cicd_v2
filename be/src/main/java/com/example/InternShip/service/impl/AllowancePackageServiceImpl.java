package com.example.InternShip.service.impl;

import com.example.InternShip.dto.allowancepackage.request.CreateAllowancePackageRequest;
import com.example.InternShip.dto.allowancepackage.response.AllowancePackageResponse;
import com.example.InternShip.entity.AllowancePackage;
import com.example.InternShip.entity.InternshipProgram;
import com.example.InternShip.exception.ResourceNotFoundException;
import com.example.InternShip.repository.AllowancePackageRepository;
import com.example.InternShip.repository.InternshipProgramRepository;
import com.example.InternShip.service.AllowancePackageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AllowancePackageServiceImpl implements AllowancePackageService {

    @Autowired
    private AllowancePackageRepository allowancePackageRepository;
    @Autowired
    private InternshipProgramRepository internshipProgramRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AllowancePackageResponse createAllowancePackage(CreateAllowancePackageRequest request) {
        InternshipProgram program = internshipProgramRepository.findById(request.getInternshipProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("InternshipProgram not found with id: " + request.getInternshipProgramId()));

        AllowancePackage allowancePackage = new AllowancePackage();
        allowancePackage.setName(request.getName());
        allowancePackage.setAmount(request.getAmount());
        allowancePackage.setRequiredWorkDays(request.getRequiredWorkDays());
        allowancePackage.setInternshipProgram(program);
        allowancePackage.setStatus(AllowancePackage.Status.ACTIVE);

        AllowancePackage savedPackage = allowancePackageRepository.save(allowancePackage);
        return modelMapper.map(savedPackage, AllowancePackageResponse.class);
    }

    @Override
    public AllowancePackageResponse getAllowancePackageById(Integer id) {
        AllowancePackage allowancePackage = allowancePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AllowancePackage not found with id: " + id));
        return modelMapper.map(allowancePackage, AllowancePackageResponse.class);
    }

    @Override
    public Page<AllowancePackageResponse> getAllAllowancePackages(String keyword, Pageable pageable) {
        Specification<AllowancePackage> spec = (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
            }
            return null;
        };

        Page<AllowancePackage> page = allowancePackageRepository.findAll(spec, pageable);
        return page.map(pkg -> modelMapper.map(pkg, AllowancePackageResponse.class));
    }

    @Override
    public AllowancePackageResponse updateAllowancePackage(Integer id, CreateAllowancePackageRequest request) {
        AllowancePackage allowancePackage = allowancePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AllowancePackage not found with id: " + id));

        allowancePackage.setName(request.getName());
        allowancePackage.setAmount(request.getAmount());
        allowancePackage.setRequiredWorkDays(request.getRequiredWorkDays());

        if (request.getInternshipProgramId() != null) {
            InternshipProgram program = internshipProgramRepository.findById(request.getInternshipProgramId())
                    .orElseThrow(() -> new ResourceNotFoundException("InternshipProgram not found with id: " + request.getInternshipProgramId()));
            allowancePackage.setInternshipProgram(program);
        }

        AllowancePackage updatedPackage = allowancePackageRepository.save(allowancePackage);
        return modelMapper.map(updatedPackage, AllowancePackageResponse.class);
    }

    @Override
    public void deleteAllowancePackage(Integer id) {
        AllowancePackage allowancePackage = allowancePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AllowancePackage not found with id: " + id));
        allowancePackage.setStatus(AllowancePackage.Status.INACTIVE);
        allowancePackageRepository.save(allowancePackage);
    }

    @Override
    public List<AllowancePackageResponse> getActiveAllowancePackagesByProgramId(Integer internshipProgramId) {
        return allowancePackageRepository.findAll().stream()
                .filter(ap -> ap.getStatus() == AllowancePackage.Status.ACTIVE &&
                        ap.getInternshipProgram().getId().equals(internshipProgramId))
                .map(pkg -> modelMapper.map(pkg, AllowancePackageResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AllowancePackage> findActiveAllowancePackageEntitiesByProgramId(Integer internshipProgramId) {
        return allowancePackageRepository.findAll().stream()
                .filter(ap -> ap.getStatus() == AllowancePackage.Status.ACTIVE &&
                        ap.getInternshipProgram() != null &&
                        ap.getInternshipProgram().getId().equals(internshipProgramId))
                .collect(Collectors.toList());
    }
}
