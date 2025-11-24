package com.example.InternShip.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.InternShip.dto.response.MajorGetAllResponse;
import com.example.InternShip.entity.Major;
import com.example.InternShip.repository.MajorRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/majors")
@RequiredArgsConstructor
public class MajorController {
    private final MajorRepository majorRepository;

    private final ModelMapper modelMapper;

    @GetMapping // Không cần qua tầng service vì không xử lý gì nhiều
    @Operation(summary = "GetAllMajor", description = "Show a List Majors")
    public ResponseEntity<List<MajorGetAllResponse>> getAllMajor() {
        List<Major> responses = majorRepository.findAll();
        List<MajorGetAllResponse> result = responses.stream()
                .map(major -> modelMapper.map(major, MajorGetAllResponse.class))
                .toList();
        return ResponseEntity.ok(result);
    }

}
