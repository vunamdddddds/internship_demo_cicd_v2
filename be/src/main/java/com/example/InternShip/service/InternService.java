package com.example.InternShip.service;

import java.util.List;

import com.example.InternShip.dto.intern.request.CreateInternRequest;
import com.example.InternShip.dto.intern.request.GetAllInternRequest;
import com.example.InternShip.dto.intern.request.UpdateInternRequest;
import com.example.InternShip.dto.intern.response.GetInternResponse;
import com.example.InternShip.dto.intern.response.MyProfileResponse;
import com.example.InternShip.dto.response.PagedResponse;

public interface InternService {
    PagedResponse<GetInternResponse> getAllIntern (GetAllInternRequest request);
    GetInternResponse createIntern(CreateInternRequest request);
    GetInternResponse updateIntern(Integer id,UpdateInternRequest updateInternRequest);
    Object getAllInternNoTeam(Integer teamId);
    MyProfileResponse getMyProfile();
    Integer getAuthenticatedInternTeamId();
    List<GetInternResponse> getAllInternByTeamId(Integer teamId);
}

