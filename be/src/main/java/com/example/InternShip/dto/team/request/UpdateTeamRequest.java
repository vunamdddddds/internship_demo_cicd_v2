package com.example.InternShip.dto.team.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeamRequest {
    @NotBlank(message = "TEAM_NAME_INVALID")
    private String name;
    @NotNull(message = "MENTOR_INVALID")
    private Integer mentorId;
}
