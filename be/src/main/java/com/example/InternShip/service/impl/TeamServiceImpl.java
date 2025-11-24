package com.example.InternShip.service.impl;

import com.example.InternShip.dto.intern.response.GetInternResponse;
import com.example.InternShip.dto.response.*;
import com.example.InternShip.dto.team.request.AddMemberRequest;
import com.example.InternShip.dto.team.request.CreateTeamRequest;
import com.example.InternShip.dto.team.request.UpdateTeamRequest;
import com.example.InternShip.dto.team.response.GetAllTeamResponse;
import com.example.InternShip.dto.team.response.TeamDetailResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.InternshipProgramRepository;
import com.example.InternShip.repository.MentorRepository;
import com.example.InternShip.repository.TeamRepository;
import com.example.InternShip.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.InternShip.service.AuthService;
import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final InternRepository internRepository;
    private final InternshipProgramRepository programRepository;
    private final MentorRepository mentorRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Override
    @Transactional
    public TeamDetailResponse createTeam(CreateTeamRequest request) {
        InternshipProgram program = programRepository.findById(request.getInternshipProgramId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PROGRAM_NOT_EXISTED.getMessage()));

        if (program.getStatus() != InternshipProgram.Status.ONGOING) {
            throw new IllegalArgumentException(ErrorCode.STATUS_INTERNSHIP_PROGRAM_INVALID.getMessage());
        }
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

        if (teamRepository.existsByNameAndInternshipProgram(request.getName(), program)) {
            throw new IllegalArgumentException(ErrorCode.TEAM_NAME_EXISTED.getMessage());
        }

        Team newTeam = new Team();
        newTeam.setName(request.getName());
        newTeam.setInternshipProgram(program);
        newTeam.setMentor(mentor);

        Team savedTeam = teamRepository.save(newTeam);

        return mapToTeamDetailResponse(savedTeam);
    }

    @Override
    public TeamDetailResponse getTeamById(int id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_EXISTED.getMessage()));
        return mapToTeamDetailResponse(team);
    }

    @Override
    public TeamDetailResponse updateTeam(Integer teamId, UpdateTeamRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_EXISTED.getMessage()));
        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

        if (!team.getName().equals(request.getName())) {
            InternshipProgram program = team.getInternshipProgram();
            if (teamRepository.existsByNameAndInternshipProgram(request.getName().trim(), program)) {
                throw new IllegalArgumentException(ErrorCode.TEAM_NAME_EXISTED.getMessage());
            }
        }

        team.setName(request.getName());
        team.setMentor(mentor);

        Team savedTeam = teamRepository.save(team);

        return mapToTeamDetailResponse(savedTeam);
    }

    @Override
    public TeamDetailResponse addMember(Integer teamId, AddMemberRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_EXISTED.getMessage()));

        List<Intern> internsToUpdate = new ArrayList<>();

        for (Integer internId : request.getInternIds()) {
            Intern intern = internRepository.findById(internId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_EXISTED.getMessage()));

            if (intern.getTeam() != null) {
                throw new IllegalStateException(ErrorCode.INTERN_INVALID.getMessage());
            }

            intern.setTeam(team);
            internsToUpdate.add(intern);
        }
        internRepository.saveAll(internsToUpdate);

        return mapToTeamDetailResponse(team);
    }

    @Override
    @Transactional
    public TeamDetailResponse removeMember(Integer internId) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_EXISTED.getMessage()));
        Team team = intern.getTeam();
        if (team == null) {
            throw new IllegalArgumentException(ErrorCode.INTERN_NOT_IN_TEAM.getMessage());
        }
        team.getInterns().remove(intern);
        intern.setTeam(null);
        internRepository.save(intern);

        return mapToTeamDetailResponse(team);
    }

    @Override
    public PagedResponse<TeamDetailResponse> getTeams(Integer internshipProgram, Integer mentor, String keyword,
            int page) {
        page = Math.max(0, page - 1);
        PageRequest pageable = PageRequest.of(page, 10);

        Page<Team> teams = teamRepository.searchTeam(internshipProgram, mentor, keyword, pageable);

        List<TeamDetailResponse> responses = teams.stream()
                .map(this::mapToTeamDetailResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page + 1,
                teams.getTotalElements(),
                teams.getTotalPages(),
                teams.hasNext(),
                teams.hasPrevious());
    }

    public List<GetAllTeamResponse> getAllTeam() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(team -> {
                    GetAllTeamResponse response = modelMapper.map(team, GetAllTeamResponse.class);
                    response.setInternshipProgramName(team.getInternshipProgram().getName());
                    return response;
                }).toList();
    }

    @Override
    public List<TeamDetailResponse> getTeamsByCurrentMentor() {
        User currentUser = authService.getUserLogin();
        if (currentUser.getRole() != Role.MENTOR) {
            throw new AccessDeniedException(ErrorCode.NOT_PERMISSION.getMessage());
        }

        Mentor mentor = mentorRepository.findByUser(currentUser)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

        return mentor.getTeams().stream()
                .map(this::mapToTeamDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeamDetailResponse mapToTeamDetailResponse(Team team) {
        TeamDetailResponse response = new TeamDetailResponse();
        response.setId(team.getId());
        response.setTeamName(team.getName());
        response.setInternshipProgramName(team.getInternshipProgram().getName());
        response.setMentorName(team.getMentor().getUser().getFullName());
        response.setSize(team.getInterns().size());

        List<GetInternResponse> members = team.getInterns().stream()
                .map(this::mapToInternResponse)
                .collect(Collectors.toList());
        response.setMembers(members);

        return response;
    }

    private GetInternResponse mapToInternResponse(Intern intern) {
        GetInternResponse dto = modelMapper.map(intern.getUser(), GetInternResponse.class);
        modelMapper.map(intern, dto);
        dto.setMajor(intern.getMajor().getName());
        dto.setUniversity(intern.getUniversity().getName());
        return dto;
    }

    @Override
    public List<GetAllTeamResponse> getAllTeamByIP(Integer internshipProgramId) {
        List<Team> teams = teamRepository.findAllByInternshipProgram_id(internshipProgramId);
        return teams.stream().map(team -> modelMapper.map(team, GetAllTeamResponse.class)).toList();
    }
}
