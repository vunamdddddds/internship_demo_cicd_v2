package com.example.InternShip.service;

import com.example.InternShip.dto.application.request.ApplicationRequest;
import com.example.InternShip.dto.application.request.HandleApplicationRequest;
import com.example.InternShip.dto.application.request.SubmitApplicationContractRequest;
import com.example.InternShip.dto.application.response.ApplicationResponse;
import com.example.InternShip.dto.response.PagedResponse;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse submitApplication(ApplicationRequest request);
    List<ApplicationResponse> getMyApplication(); // <-- mới
    PagedResponse<ApplicationResponse> getAllApplication(Integer internshipTerm, Integer university, Integer major, String applicantName, String status, int page);
    void submitApplicationContract(SubmitApplicationContractRequest request);
    void withdrawApplication(Integer applicationId);
    void handleApplicationAction (HandleApplicationRequest request);
}

