package com.example.InternShip.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.InternShip.dto.response.UniversityGetAllResponse;
import com.example.InternShip.entity.University;
import com.example.InternShip.repository.UniversityRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/universities")
@RequiredArgsConstructor
public class UniversityController {
    private final UniversityRepository universityRepository;

    private final ModelMapper modelMapper;

    @GetMapping // Không cần qua tầng service vì không xử lý gì nhiều
    public ResponseEntity<List<UniversityGetAllResponse>> getAllUniversity() {
        List<University> responses = universityRepository.findAll();
        List<UniversityGetAllResponse> result = responses.stream()
                .map(university -> modelMapper.map(university, UniversityGetAllResponse.class))
                .toList();
        return ResponseEntity.ok(result);
    }
}
