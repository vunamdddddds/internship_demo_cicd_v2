package com.example.InternShip.service.impl;

import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.user.request.ChangeMyPasswordRequest;
import com.example.InternShip.dto.user.request.CreateUserRequest;
import com.example.InternShip.dto.user.request.ForgetPasswordRequest;
import com.example.InternShip.dto.user.request.GetAllUserRequest;
import com.example.InternShip.dto.user.request.UpdateInfoRequest;
import com.example.InternShip.dto.user.request.UpdateUserRequest;
import com.example.InternShip.dto.user.response.GetUserResponse;
import com.example.InternShip.entity.PendingUser;
import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.PendingUserRepository;
import com.example.InternShip.repository.UserRepository;
import com.example.InternShip.service.CloudinaryService;
import com.example.InternShip.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import java.time.LocalDateTime;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AuthServiceImpl authService;
    private final PendingUserRepository pendingUserRepository;
    private final PendingUserServiceImpl pendingUserService;
    private final CloudinaryService cloudinaryService;

    public PagedResponse<GetUserResponse> getAllUser(GetAllUserRequest request) {
        int page = Math.max(0, request.getPage() - 1);
        int size = 10;
        Role role = parseRole(request.getRole());
        PageRequest pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<User> users = userRepository.searchUsers(role, request.getKeyword(), pageable);

        return new PagedResponse<>(
                users.map(user -> modelMapper.map(user, GetUserResponse.class)).getContent(),
                page + 1,
                users.getTotalElements(),
                users.getTotalPages(),
                users.hasNext(),
                users.hasPrevious());
    }

    @Override
    public GetUserResponse findById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTED.getMessage()));
        return modelMapper.map(user, GetUserResponse.class);
    }

    public GetUserResponse getUserInfo() {
        return findById(authService.getUserLogin().getId());
    }

    public Role parseRole(String role) {
        if (role != null && !role.isBlank()) {
            try {
                return Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(ErrorCode.ROLE_INVALID.getMessage());
            }
        }
        return null;
    }

    public GetUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(ErrorCode.EMAIL_EXISTED.getMessage());
        }
        Role role = parseRole(request.getRole());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = modelMapper.map(request, User.class);
        user.setRole(role);
        user.setUsername(request.getEmail());
        user.setPassword(encoder.encode("12345678"));
        userRepository.save(user);
        return modelMapper.map(user, GetUserResponse.class);
    }

    public GetUserResponse updateUser(UpdateUserRequest request, int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTED.getMessage()));
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException(ErrorCode.EDIT_USER_INVALID.getMessage());
        }
        modelMapper.map(request, user);
        user.setRole(parseRole(request.getRole()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return modelMapper.map(user, GetUserResponse.class);
    }

    public GetUserResponse updateUserInfo(UpdateInfoRequest request) {
        User user = authService.getUserLogin();
        modelMapper.map(request, user);
        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            FileResponse fileResponse = cloudinaryService.uploadFile(request.getAvatarFile(), "avatars");
            user.setAvatarUrl(fileResponse.getFileUrl());
        }
        userRepository.save(user);
        return modelMapper.map(user, GetUserResponse.class);
    }

    @Override
    public void forgetPassword(ForgetPasswordRequest request) {
        userRepository.findByUsernameOrEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(ErrorCode.EMAIL_INVALID.getMessage()));
        String token = UUID.randomUUID().toString();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        PendingUser pendingUser = modelMapper.map(request, PendingUser.class);
        pendingUser.setPassword(encoder.encode(request.getPassword()));
        pendingUser.setToken(token);
        pendingUser.setExpiryDate(LocalDateTime.now().plusMinutes(20));
        pendingUserRepository.save(pendingUser);
        String verifyLink = "http://localhost:8082/api/v1/pendingUsers/verifyForgetPassword?token=" + token;
        pendingUserService.sendVerification(request.getEmail(), verifyLink);

    }

    @Override
    public void changePassword(ChangeMyPasswordRequest request) {
        User user = authService.getUserLogin();
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        if (!bcrypt.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException(ErrorCode.PASSWORD_INVALID.getMessage());
        }
        user.setPassword(bcrypt.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public List<GetUserResponse> getAllHr() {
        return userRepository.findByRole(Role.HR)
                .stream()
                .map(user -> modelMapper.map(user, GetUserResponse.class))
                .toList();
    }

}
