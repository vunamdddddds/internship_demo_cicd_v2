package com.example.InternShip.dto.intern.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInternRequest {
    @NotNull(message = "MAJOR_NOT_NULL")
    private Integer majorId;
    @NotNull(message = "UNIVERSITY_NOT_NULL")
    private Integer universityId;
    @NotBlank(message = "STATUS_NOT_BLANK")
    private String status; 
}
