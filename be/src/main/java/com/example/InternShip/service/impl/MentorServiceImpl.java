package com.example.InternShip.service.impl;

import com.example.InternShip.dto.mentor.request.CreateMentorRequest;
import com.example.InternShip.dto.mentor.request.UpdateMentorRequest;
import com.example.InternShip.dto.mentor.response.GetAllMentorResponse;
import com.example.InternShip.dto.mentor.response.GetMentorResponse;
import com.example.InternShip.dto.mentor.response.TeamResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.*;
import com.example.InternShip.service.MentorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.InternShip.service.AuthService;
import com.example.InternShip.dto.sprint.response.SprintResponse;
import com.example.InternShip.dto.team.response.TeamMemberResponse;

@Service

@RequiredArgsConstructor

public class MentorServiceImpl implements MentorService {

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final MentorRepository mentorRepository;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override

    @Transactional

    public GetMentorResponse createMentor(CreateMentorRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(ErrorCode.EMAIL_EXISTED.getMessage());

        }

        Department department = departmentRepository.findById(request.getDepartmentId())

                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.DEPARTMENT_NOT_EXISTED.getMessage()));

        User user = modelMapper.map(request, User.class);

        user.setUsername(request.getEmail());

        user.setPassword(passwordEncoder.encode("123456@Abc"));

        user.setRole(Role.MENTOR);

        User savedUser = userRepository.save(user);

        Mentor mentor = new Mentor();

        mentor.setUser(savedUser);

        mentor.setDepartment(department);

        Mentor savedMentor = mentorRepository.save(mentor);

        GetMentorResponse mentorResponse = modelMapper.map(savedUser, GetMentorResponse.class);

        mentorResponse.setId(savedMentor.getId());

        mentorResponse.setDepartmentName(department.getName());

        mentorResponse.setTotalInternOwn(0);

        return mentorResponse;

    }

    @Override

    @Transactional

    public GetMentorResponse updateMentorDepartment(Integer mentorId, UpdateMentorRequest request) {

        Mentor mentor = mentorRepository.findById(mentorId)

                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

        Department department = departmentRepository.findById(request.getDepartmentId())

                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.DEPARTMENT_NOT_EXISTED.getMessage()));

        mentor.setDepartment(department);

        Mentor savedMentor = mentorRepository.save(mentor);

        GetMentorResponse response = modelMapper.map(savedMentor.getUser(), GetMentorResponse.class);

        modelMapper.map(savedMentor, response);

        response.setTotalInternOwn(totalInternInAllGroup(mentor));

        response.setDepartmentName(mentor.getDepartment().getName());

        return response;

    }

    private int totalInternInAllGroup(Mentor mentor) {

        List<Team> teams = mentor.getTeams().stream()

                .filter(team -> team.getInternshipProgram().getStatus() == InternshipProgram.Status.ONGOING)

                .toList();

        int totalInternInAllGroup = 0;

        for (Team team : teams) {

            int totalInternInGroup = team.getInterns().size();

            totalInternInAllGroup += totalInternInGroup;

        }

        return totalInternInAllGroup;

    }

    @Override

    public PagedResponse<GetMentorResponse> getAll(List<Integer> department, String keyword, int page) {

        page = Math.max(0, page - 1);

        PageRequest pageable = PageRequest.of(page, 10);

        // Kiểm tra null vì Hibernate không coi List rỗng là null

        if (department == null || department.isEmpty()) {

            department = null;

        }

        Page<Mentor> mentors = mentorRepository.searchMentor(department, keyword, pageable);

        List<GetMentorResponse> responses = mentors.stream()

                .map(mentor -> {

                    User user = mentor.getUser();

                    GetMentorResponse res = modelMapper.map(user, GetMentorResponse.class);

                    res.setId(mentor.getId());

                    res.setTotalInternOwn(totalInternInAllGroup(mentor));

                    res.setDepartmentName(mentor.getDepartment().getName());

                    return res;

                })

                .collect(Collectors.toList());

        return new PagedResponse<>(

                responses,

                page + 1,

                mentors.getTotalElements(),

                mentors.getTotalPages(),

                mentors.hasNext(),

                mentors.hasPrevious());

    }

    @Override
    public List<GetAllMentorResponse> getAllMentor() {

        List<Mentor> mentors = mentorRepository.findAll().stream()

                .filter(m -> m.getUser().isActive())

                .toList();

        return mentors.stream()

                .map(mentor -> new GetAllMentorResponse(

                        mentor.getId(),

                        mentor.getUser().getFullName(),

                        mentor.getUser().getEmail(),

                        mentor.getDepartment().getName()

                ))

                .toList();

    }

    @Override

    public List<SprintResponse> getSprintsForCurrentUser() {

        User user = authService.getUserLogin();

        if (!user.getRole().equals(Role.MENTOR)) {

            throw new RuntimeException("Only mentors can access this resource.");

        }

        Mentor mentor = mentorRepository.findByUser(user)

                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

        return mentor.getTeams().stream()

                .flatMap(team -> team.getSprints().stream())

                .map(sprint -> {

                    SprintResponse sprintResponse = new SprintResponse();

                    sprintResponse.setId(sprint.getId());

                    sprintResponse.setName(sprint.getName());

                    sprintResponse.setGoal(sprint.getGoal());

                    sprintResponse.setStartDate(sprint.getStartDate());

                    sprintResponse.setEndDate(sprint.getEndDate());

                    sprintResponse.setTeamId(sprint.getTeam().getId());
                    return sprintResponse;

                })

                .collect(Collectors.toList());

    }

    @Override

    public List<TeamResponse> getTeamsForCurrentUser() {

        User user = authService.getUserLogin();

        if (!user.getRole().equals(Role.MENTOR)) {

            throw new RuntimeException("Only mentors can access this resource.");

        }

        Mentor mentor = mentorRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

        return mentor.getTeams().stream()

                .map(team -> {

                    TeamResponse teamResponse = new TeamResponse();

                    teamResponse.setId(team.getId());

                    teamResponse.setName(team.getName());

                    List<TeamMemberResponse> members = team.getInterns().stream()

                            .map(intern -> new TeamMemberResponse(intern.getId(), intern.getUser().getFullName()))

                            .collect(Collectors.toList());

                    teamResponse.setMembers(members);

                    return teamResponse;

                })

                .collect(Collectors.toList());

    }

}