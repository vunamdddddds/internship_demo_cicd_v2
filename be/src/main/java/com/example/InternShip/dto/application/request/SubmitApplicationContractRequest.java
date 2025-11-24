package com.example.InternShip.dto.application.request;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubmitApplicationContractRequest {
    @NotNull(message = "FILE_NOTNULL")
    private MultipartFile applicationContractFile; //hdtt
}

