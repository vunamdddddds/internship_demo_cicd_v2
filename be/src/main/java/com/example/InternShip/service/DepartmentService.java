package com.example.InternShip.service;

import java.util.List;

import com.example.InternShip.dto.department.response.GetAllDepartmentResponse;

public interface DepartmentService {

    List<GetAllDepartmentResponse> getAllDepartments();
    
}
