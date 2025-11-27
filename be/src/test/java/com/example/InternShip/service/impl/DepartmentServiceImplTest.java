package com.example.InternShip.service.impl;

import com.example.InternShip.dto.department.response.GetAllDepartmentResponse;
import com.example.InternShip.entity.Department;
import com.example.InternShip.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private GetAllDepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1);
        department.setName("Engineering");

        departmentResponse = new GetAllDepartmentResponse();
        departmentResponse.setId(1);
        departmentResponse.setName("Engineering");
    }

    @Test
    void getAllDepartments_happyPath() {
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));
        when(modelMapper.map(department, GetAllDepartmentResponse.class)).thenReturn(departmentResponse);

        List<GetAllDepartmentResponse> response = departmentService.getAllDepartments();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Engineering", response.get(0).getName());
    }
}
