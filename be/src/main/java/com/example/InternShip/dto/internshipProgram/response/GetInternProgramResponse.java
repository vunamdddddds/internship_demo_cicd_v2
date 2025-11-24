package com.example.InternShip.dto.internshipProgram.response;

import com.example.InternShip.entity.InternshipProgram;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetInternProgramResponse {
    private Integer id;
    private String name;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endPublishedTime;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endReviewingTime;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime timeStart;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime timeEnd;
    private String department;

    private InternshipProgram.Status status;
}

