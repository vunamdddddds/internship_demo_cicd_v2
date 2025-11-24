package com.example.InternShip.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.InternShip.dto.department.response.GetAllDepartmentResponse;
import com.example.InternShip.entity.Department;
import com.example.InternShip.repository.DepartmentRepository;
import com.example.InternShip.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GetAllDepartmentResponse> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();

        return departments.stream().map(department -> modelMapper.map(department, GetAllDepartmentResponse.class))
                .collect(Collectors.toList());
    }

}
