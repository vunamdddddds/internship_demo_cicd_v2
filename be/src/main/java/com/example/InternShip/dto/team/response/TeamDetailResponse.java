package com.example.InternShip.dto.team.response;

import lombok.Data;
import java.util.List;

import com.example.InternShip.dto.intern.response.GetInternResponse;

@Data
public class TeamDetailResponse {
    private Integer id;
    private String teamName;
    private String internshipProgramName;
    private String mentorName;
    private int size;
    private List<GetInternResponse> members;
}