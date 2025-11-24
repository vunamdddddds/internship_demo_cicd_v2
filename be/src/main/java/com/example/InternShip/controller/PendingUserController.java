package com.example.InternShip.controller;

import com.example.InternShip.service.PendingUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pendingUsers")
@RequiredArgsConstructor
public class PendingUserController {
    private final PendingUserService pendingUserService;

    @GetMapping("/verify")
    public void verify(@RequestParam String token, HttpServletResponse response) throws IOException {
        pendingUserService.verify(token);
        response.sendRedirect("http://localhost:3000");
    }

    @GetMapping("/verifyForgetPassword")
    public void verifyForgetPassword(@RequestParam String token, HttpServletResponse response) throws IOException {
        pendingUserService.verifyForgetPassword(token);
        response.sendRedirect("http://localhost:3000");
    }
}
