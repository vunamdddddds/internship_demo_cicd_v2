package com.example.InternShip.service;

import java.util.List;

import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.supportRequest.request.CreateSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.request.RejectSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.request.UpdateSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.response.GetSupportRequestResponse;

public interface SupportRequestService {
    //HR
    PagedResponse<GetSupportRequestResponse> getAllSupportRequest(String keyword, String status, int page, int size);

    GetSupportRequestResponse approveSupportRequest(Integer supportId);

    GetSupportRequestResponse inProgressSupportRequest(Integer supportId);

    GetSupportRequestResponse rejectSupportRequest(Integer supportId, RejectSupportRequestRequest request);

    //Intern
    GetSupportRequestResponse createSupportRequest(CreateSupportRequestRequest request);

    GetSupportRequestResponse updateRequest(Integer id, UpdateSupportRequestRequest request);

    List<GetSupportRequestResponse> getMyList();

    void cancelSupportRequest(Integer supportId);
}
