package com.example.InternShip.controller;

import com.example.InternShip.dto.auth.request.GoogleLoginRequest;
import com.example.InternShip.dto.auth.request.LoginRequest;
import com.example.InternShip.dto.auth.request.RegisterRequest;
import com.example.InternShip.dto.auth.response.TokenResponse;
import com.example.InternShip.dto.user.request.ChangeMyPasswordRequest;
import com.example.InternShip.dto.user.request.ForgetPasswordRequest;
import com.example.InternShip.dto.auth.request.RefreshTokenRequest;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) throws JOSEException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            TokenResponse tokenResponse = authService.loginWithGoogle(request);
            return ResponseEntity.ok(tokenResponse);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws JOSEException, ParseException {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequest request) {
        userService.forgetPassword(request);
        return ResponseEntity.ok().body("Verification code has been sent");
    }

    @PostMapping("/link-google")
    public ResponseEntity<Void> linkGoogleAccount(@RequestBody GoogleLoginRequest request) throws Exception {
        authService.linkGoogleAccount(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangeMyPasswordRequest request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully!");
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }

}
