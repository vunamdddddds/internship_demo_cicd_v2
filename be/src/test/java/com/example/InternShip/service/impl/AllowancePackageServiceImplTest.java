package com.example.InternShip.service.impl;

import com.example.InternShip.dto.allowancepackage.request.CreateAllowancePackageRequest;
import com.example.InternShip.dto.allowancepackage.response.AllowancePackageResponse;
import com.example.InternShip.entity.AllowancePackage;
import com.example.InternShip.entity.InternshipProgram;
import com.example.InternShip.repository.AllowancePackageRepository;
import com.example.InternShip.repository.InternshipProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;


@ExtendWith(MockitoExtension.class)
class AllowancePackageServiceImplTest {

    @Mock
    private AllowancePackageRepository allowancePackageRepository;

    @Mock
    private InternshipProgramRepository internshipProgramRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AllowancePackageServiceImpl allowancePackageService;

    private AllowancePackage allowancePackage;
    private InternshipProgram internshipProgram;
    private CreateAllowancePackageRequest createRequest;
    private AllowancePackageResponse allowancePackageResponse;

    @BeforeEach
    void setUp() {
        internshipProgram = new InternshipProgram();
        internshipProgram.setId(1);

        allowancePackage = new AllowancePackage();
        allowancePackage.setId(1);
        allowancePackage.setName("Test Package");
        allowancePackage.setAmount(BigDecimal.valueOf(1000));
        allowancePackage.setRequiredWorkDays(20);
        allowancePackage.setInternshipProgram(internshipProgram);
        allowancePackage.setStatus(AllowancePackage.Status.ACTIVE);

        createRequest = new CreateAllowancePackageRequest();
        createRequest.setName("Test Package");
        createRequest.setAmount(BigDecimal.valueOf(1000));
        createRequest.setRequiredWorkDays(20);
        createRequest.setInternshipProgramId(1);

        allowancePackageResponse = new AllowancePackageResponse();
        allowancePackageResponse.setId(1);
        allowancePackageResponse.setName("Test Package");
        allowancePackageResponse.setAmount(BigDecimal.valueOf(1000));
    }

    @Test
    void createAllowancePackage_happyPath() {
        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(internshipProgram));
        when(allowancePackageRepository.save(any(AllowancePackage.class))).thenReturn(allowancePackage);
        when(modelMapper.map(allowancePackage, AllowancePackageResponse.class)).thenReturn(allowancePackageResponse);

        AllowancePackageResponse response = allowancePackageService.createAllowancePackage(createRequest);

        assertNotNull(response);
        assertEquals("Test Package", response.getName());
    }

    @Test
    void getAllowancePackageById_happyPath() {
        when(allowancePackageRepository.findById(1)).thenReturn(Optional.of(allowancePackage));
        when(modelMapper.map(allowancePackage, AllowancePackageResponse.class)).thenReturn(allowancePackageResponse);

        AllowancePackageResponse response = allowancePackageService.getAllowancePackageById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    void getAllAllowancePackages_happyPath() {
        Page<AllowancePackage> page = new PageImpl<>(Collections.singletonList(allowancePackage));
        when(allowancePackageRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(allowancePackage, AllowancePackageResponse.class)).thenReturn(allowancePackageResponse);

        Page<AllowancePackageResponse> response = allowancePackageService.getAllAllowancePackages(null, Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void updateAllowancePackage_happyPath() {
        when(allowancePackageRepository.findById(1)).thenReturn(Optional.of(allowancePackage));
        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(internshipProgram));
        when(allowancePackageRepository.save(any(AllowancePackage.class))).thenReturn(allowancePackage);
        when(modelMapper.map(allowancePackage, AllowancePackageResponse.class)).thenReturn(allowancePackageResponse);

        AllowancePackageResponse response = allowancePackageService.updateAllowancePackage(1, createRequest);

        assertNotNull(response);
        assertEquals("Test Package", response.getName());
    }

    @Test
    void deleteAllowancePackage_happyPath() {
        when(allowancePackageRepository.findById(1)).thenReturn(Optional.of(allowancePackage));
        when(allowancePackageRepository.save(any(AllowancePackage.class))).thenReturn(allowancePackage);

        allowancePackageService.deleteAllowancePackage(1);

        assertEquals(AllowancePackage.Status.INACTIVE, allowancePackage.getStatus());
    }

    @Test
    void getActiveAllowancePackagesByProgramId_happyPath() {
        when(allowancePackageRepository.findAll()).thenReturn(Collections.singletonList(allowancePackage));
        when(modelMapper.map(allowancePackage, AllowancePackageResponse.class)).thenReturn(allowancePackageResponse);

        List<AllowancePackageResponse> response = allowancePackageService.getActiveAllowancePackagesByProgramId(1);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void findActiveAllowancePackageEntitiesByProgramId_happyPath() {
        when(allowancePackageRepository.findAll()).thenReturn(Collections.singletonList(allowancePackage));

        List<AllowancePackage> response = allowancePackageService.findActiveAllowancePackageEntitiesByProgramId(1);

        assertNotNull(response);
        assertEquals(1, response.size());
    }
}
