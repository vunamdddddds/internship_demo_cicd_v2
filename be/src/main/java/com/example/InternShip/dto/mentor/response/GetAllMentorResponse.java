package com.example.InternShip.dto.mentor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllMentorResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String departmentName;
}
