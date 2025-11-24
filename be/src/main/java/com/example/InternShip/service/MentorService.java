package com.example.InternShip.service;

import com.example.InternShip.dto.mentor.request.CreateMentorRequest;
import com.example.InternShip.dto.mentor.request.UpdateMentorRequest;
import com.example.InternShip.dto.mentor.response.GetAllMentorResponse;
import com.example.InternShip.dto.mentor.response.GetMentorResponse;
import com.example.InternShip.dto.mentor.response.TeamResponse;
import com.example.InternShip.dto.sprint.response.SprintResponse;

import java.util.List;

public interface MentorService {
    GetMentorResponse createMentor(CreateMentorRequest request);
    GetMentorResponse updateMentorDepartment(Integer mentorId, UpdateMentorRequest request);
    Object getAll(List<Integer> department, String keyword, int page);
    public List<GetAllMentorResponse> getAllMentor();
    List<SprintResponse> getSprintsForCurrentUser();
    List<TeamResponse> getTeamsForCurrentUser();
}