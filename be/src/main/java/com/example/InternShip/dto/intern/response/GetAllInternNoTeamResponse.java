package com.example.InternShip.dto.intern.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllInternNoTeamResponse {
    private Integer id;
    private String fullName;
    private String email;
}
