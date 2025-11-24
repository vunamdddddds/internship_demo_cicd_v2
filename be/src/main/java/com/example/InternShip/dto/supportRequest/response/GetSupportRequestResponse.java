package com.example.InternShip.dto.supportRequest.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSupportRequestResponse {
    private Integer id;
    private String title;
    private String description;
    private String evidenceFileUrl;
    private String internName;
    private String internEmail;
    private String handlerName;
    private String hrResponse;
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime resolvedAt;
    private String status;
}
