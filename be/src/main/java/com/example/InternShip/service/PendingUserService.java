package com.example.InternShip.service;

public interface PendingUserService {
    void verify(String token);
    void verifyForgetPassword(String token);
}
