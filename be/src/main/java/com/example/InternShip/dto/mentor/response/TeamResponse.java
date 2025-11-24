package com.example.InternShip.dto.mentor.response;

import lombok.Data;

import java.util.List;

import com.example.InternShip.dto.team.response.TeamMemberResponse;

@Data
public class TeamResponse {
    private Integer id;
    private String name;
    private List<TeamMemberResponse> members;
}
