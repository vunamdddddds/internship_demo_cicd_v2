package com.example.InternShip.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.entity.User;
import com.example.InternShip.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
    }

    @Test
    void uploadFile_multipartFile_happyPath() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1000L);
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getBytes()).thenReturn(new byte[1000]);

        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(
                Map.of("public_id", "test_id", "secure_url", "http://example.com/test.pdf"));

        when(authService.getUserLogin()).thenReturn(user);

        FileResponse response = cloudinaryService.uploadFile(file, "documents");

        assertNotNull(response);
        assertEquals("http://example.com/test.pdf", response.getFileUrl());
    }

    @Test
    void uploadFile_byteArray_happyPath() throws IOException {
        byte[] fileBytes = "test data".getBytes();
        String fileName = "test.xlsx";
        String folder = "reports";

        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(
                Map.of("public_id", "test_id", "secure_url", "http://example.com/test.xlsx"));

        FileResponse response = cloudinaryService.uploadFile(fileBytes, fileName, folder);

        assertNotNull(response);
        assertEquals("http://example.com/test.xlsx", response.getFileUrl());
    }
}
