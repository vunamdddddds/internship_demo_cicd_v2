package com.example.InternShip.dto.application.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class HandleApplicationRequest {
    @NotEmpty(message = "LIST_APPLICATION_INVALID")
    Set<Integer> applicationIds;
    @NotEmpty(message = "ACTION_INVALID")
    private String action;
}