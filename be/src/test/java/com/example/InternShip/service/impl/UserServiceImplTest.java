package com.example.InternShip.service.impl;

import com.example.InternShip.dto.user.request.CreateUserRequest;
import com.example.InternShip.dto.user.request.GetAllUserRequest;
import com.example.InternShip.dto.user.request.UpdateInfoRequest;
import com.example.InternShip.dto.user.request.UpdateUserRequest;
import com.example.InternShip.dto.user.response.GetUserResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private GetUserResponse getUserResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFullName("Test User");
        user.setEmail("test@example.com");

        getUserResponse = new GetUserResponse();
        getUserResponse.setId(1);
        getUserResponse.setFullName("Test User");
    }

    @Test
    void getAllUser_happyPath() {
        GetAllUserRequest request = new GetAllUserRequest();
        request.setPage(1);
        Page<User> page = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.searchUsers(any(), any(), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(user, GetUserResponse.class)).thenReturn(getUserResponse);

        PagedResponse<GetUserResponse> response = userService.getAllUser(request);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findById_happyPath() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, GetUserResponse.class)).thenReturn(getUserResponse);

        GetUserResponse response = userService.findById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    void getUserInfo_happyPath() {
        when(authService.getUserLogin()).thenReturn(user);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, GetUserResponse.class)).thenReturn(getUserResponse);

        GetUserResponse response = userService.getUserInfo();

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    void createUser_happyPath() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@example.com");
        request.setRole("HR");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, GetUserResponse.class)).thenReturn(getUserResponse);

        GetUserResponse response = userService.createUser(request);

        assertNotNull(response);
    }

    @Test
    void updateUser_happyPath() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole("MENTOR");
        user.setRole(Role.HR);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        org.mockito.Mockito.doNothing().when(modelMapper).map(any(UpdateUserRequest.class), any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, GetUserResponse.class)).thenReturn(getUserResponse);


        GetUserResponse response = userService.updateUser(request, 1);

        assertNotNull(response);
    }

    @Test
    void updateUserInfo_happyPath() {
        UpdateInfoRequest request = new UpdateInfoRequest();
        request.setFullName("Updated Name");
        when(authService.getUserLogin()).thenReturn(user);
        org.mockito.Mockito.doNothing().when(modelMapper).map(any(UpdateInfoRequest.class), any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, GetUserResponse.class)).thenReturn(getUserResponse);

        GetUserResponse response = userService.updateUserInfo(request);

        assertNotNull(response);
    }

    @Test
    void getAllHr_happyPath() {
        when(userRepository.findByRole(Role.HR)).thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, GetUserResponse.class)).thenReturn(getUserResponse);

        List<GetUserResponse> response = userService.getAllHr();

        assertNotNull(response);
        assertEquals(1, response.size());
    }
}
