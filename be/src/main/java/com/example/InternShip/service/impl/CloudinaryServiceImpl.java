package com.example.InternShip.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.entity.User;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.CloudinaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;
    private final AuthService authService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public FileResponse uploadFile(MultipartFile file, String folder) {
        User user = authService.getUserLogin();
        try {
            // Pass folder to validation method
            validateFile(file, folder);

            String originalFilename = file.getOriginalFilename();
            String fileName = removeEmailDomain(user.getEmail());

            // Let Cloudinary auto-detect the resource type
            @SuppressWarnings("rawtypes")
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", folder,
                            "public_id", fileName,
                            "overwrite", true,
                            "unique_filename", false
                    ));

            String publicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");

            return FileResponse.builder()
                    .fileName(originalFilename)
                    .fileUrl(secureUrl)
                    .publicId(publicId)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .uploadDate(LocalDateTime.now())
                    .message("Upload thành công!")
                    .build();

        } catch (IOException e) {
            log.error("Error uploading file: ", e);
            throw new RuntimeException(ErrorCode.UPLOAD_FILE_FAILED.getMessage());
        }
    }

    private void validateFile(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new RuntimeException(ErrorCode.FILE_NOT_NULL.getMessage());
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException(ErrorCode.FILE_INVALID.getMessage());
        }

        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();

        // Apply validation based on the folder
        if ("avatars".equals(folder)) {
            List<String> allowedImageExtensions = List.of("png", "jpg", "jpeg", "gif");
            if (!allowedImageExtensions.contains(extension)) {
                throw new IllegalArgumentException(ErrorCode.TYPE_AVATAR_FILE_INVALID.getMessage());
            }
        } else {
            // Assuming other folders are for documents like CVs
            List<String> allowedDocumentExtensions = List.of("pdf", "docx", "doc", "png", "jpg", "jpeg");
            if (!allowedDocumentExtensions.contains(extension)) {
                throw new IllegalArgumentException(ErrorCode.TYPE_FILE_INVALID.getMessage());
            }
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String removeEmailDomain(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.substring(0, email.indexOf("@"));
    }
}
