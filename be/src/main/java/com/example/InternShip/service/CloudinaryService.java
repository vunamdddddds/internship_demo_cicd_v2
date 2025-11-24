package com.example.InternShip.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.InternShip.dto.cloudinary.response.FileResponse;

public interface CloudinaryService {
    FileResponse uploadFile(MultipartFile file,String folder);
}
