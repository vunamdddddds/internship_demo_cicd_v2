package com.example.InternShip.dto.intern.response;

import com.example.InternShip.dto.team.response.TeamDetailResponse;

import lombok.Data;

@Data
public class MyProfileResponse {
    private GetInternResponse internDetails;
    private TeamDetailResponse teamDetails;
}
