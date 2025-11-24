package com.example.InternShip.service;

import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.team.request.AddMemberRequest;
import com.example.InternShip.dto.team.request.CreateTeamRequest;
import com.example.InternShip.dto.team.request.UpdateTeamRequest;
import com.example.InternShip.dto.team.response.GetAllTeamResponse;
import com.example.InternShip.dto.team.response.TeamDetailResponse;
import com.example.InternShip.entity.Team;

import java.util.List;

public interface TeamService {
    TeamDetailResponse createTeam(CreateTeamRequest request);
    TeamDetailResponse getTeamById(int id);
    TeamDetailResponse addMember(Integer teamId, AddMemberRequest request);
    TeamDetailResponse removeMember(Integer internId);
    PagedResponse<TeamDetailResponse> getTeams(Integer internshipTerm, Integer mentor, String keyword, int page);
    TeamDetailResponse updateTeam(Integer teamId, UpdateTeamRequest request);
    List<GetAllTeamResponse> getAllTeam();
    List<TeamDetailResponse> getTeamsByCurrentMentor();
    TeamDetailResponse mapToTeamDetailResponse(Team team);
    List<GetAllTeamResponse> getAllTeamByIP(Integer internshipProgramId);
}
