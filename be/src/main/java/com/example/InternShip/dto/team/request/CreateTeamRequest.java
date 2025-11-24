package com.example.InternShip.dto.team.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeamRequest {

    @NotBlank(message = "TEAM_NAME_INVALID")
    private String name;

    @NotNull(message = "PROGRAM_INVALID")
    private Integer internshipProgramId;

    @NotNull(message = "MENTOR_INVALID")
    private Integer mentorId;
}