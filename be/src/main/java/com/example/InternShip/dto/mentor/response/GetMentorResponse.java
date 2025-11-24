package com.example.InternShip.dto.mentor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMentorResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;

    private String departmentName; // Tên phòng ban
    private Integer totalInternOwn; // Số lượng intern đang có
}

