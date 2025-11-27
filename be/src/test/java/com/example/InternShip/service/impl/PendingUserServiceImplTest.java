package com.example.InternShip.service.impl;

import com.example.InternShip.entity.PendingUser;
import com.example.InternShip.entity.User;
import com.example.InternShip.repository.PendingUserRepository;
import com.example.InternShip.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingUserServiceImplTest {

    @Mock
    private PendingUserRepository pendingUserRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private PendingUserServiceImpl pendingUserService;

    private PendingUser pendingUser;
    private User user;

    @BeforeEach
    void setUp() {
        pendingUser = new PendingUser();
        pendingUser.setToken("test-token");
        pendingUser.setExpiryDate(LocalDateTime.now().plusHours(1));
        pendingUser.setEmail("test@example.com");
        pendingUser.setPassword("password");

        user = new User();
        user.setEmail("test@example.com");
        ReflectionTestUtils.setField(pendingUserService, "fromMail", "test@example.com");
    }

    @Test
    void verify_happyPath() {
        when(pendingUserRepository.findByToken("test-token")).thenReturn(Optional.of(pendingUser));
        when(modelMapper.map(pendingUser, User.class)).thenReturn(user);

        assertDoesNotThrow(() -> pendingUserService.verify("test-token"));
    }

    @Test
    void verifyForgetPassword_happyPath() {
        when(pendingUserRepository.findByToken("test-token")).thenReturn(Optional.of(pendingUser));
        when(userRepository.findByUsernameOrEmail("test@example.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> pendingUserService.verifyForgetPassword("test-token"));
    }

    @Test
    void sendVerification_happyPath() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(mailSender).send(any(MimeMessage.class));
        assertDoesNotThrow(() -> pendingUserService.sendVerification("test@example.com", "http://verify.link"));
    }
}
