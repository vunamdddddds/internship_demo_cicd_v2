package com.example.InternShip.service;

import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.user.request.ChangeMyPasswordRequest;
import com.example.InternShip.dto.user.request.CreateUserRequest;
import com.example.InternShip.dto.user.request.ForgetPasswordRequest;
import com.example.InternShip.dto.user.request.GetAllUserRequest;
import com.example.InternShip.dto.user.request.UpdateInfoRequest;
import com.example.InternShip.dto.user.request.UpdateUserRequest;
import com.example.InternShip.dto.user.response.GetUserResponse;

import java.util.List;

public interface UserService {
    PagedResponse<GetUserResponse> getAllUser(GetAllUserRequest request);
    GetUserResponse getUserInfo();
    GetUserResponse findById(int id);
    GetUserResponse createUser(CreateUserRequest request);
    GetUserResponse updateUser(UpdateUserRequest request, int id);
    GetUserResponse updateUserInfo(UpdateInfoRequest request);
    void forgetPassword(ForgetPasswordRequest request);
    void changePassword(ChangeMyPasswordRequest request);

    List<GetUserResponse> getAllHr();

}
