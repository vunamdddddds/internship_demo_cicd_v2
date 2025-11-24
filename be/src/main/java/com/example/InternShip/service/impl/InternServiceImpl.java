package com.example.InternShip.service.impl;

import com.example.InternShip.dto.intern.request.CreateInternRequest;
import com.example.InternShip.dto.intern.request.GetAllInternRequest;
import com.example.InternShip.dto.intern.request.UpdateInternRequest;
import com.example.InternShip.dto.intern.response.GetAllInternNoTeamResponse;
import com.example.InternShip.dto.intern.response.GetInternResponse;
import com.example.InternShip.dto.intern.response.MyProfileResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.*;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.team.response.TeamDetailResponse;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.InternService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.InternShip.service.TeamService;

@Service
@RequiredArgsConstructor
public class InternServiceImpl implements InternService {

        private final UserRepository userRepository;
        private final InternRepository internRepository;
        private final UniversityRepository universityRepository;
        private final MajorRepository majorRepository;
        private final InternshipProgramRepository internshipProgramRepository;
        private final TeamRepository teamRepository;
        private final AuthService authService;
        private final TeamService teamService;
        private final ModelMapper modelMapper;
        
        @Override
        public MyProfileResponse getMyProfile() {
                User currentUser = authService.getUserLogin();
                Intern intern = internRepository.findByUser(currentUser)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                ErrorCode.INTERN_NOT_EXISTED.getMessage()));

                // Map Intern details
                GetInternResponse internResponse = modelMapper.map(intern.getUser(), GetInternResponse.class);
                internResponse.setId(intern.getId());
                internResponse.setUniversity(intern.getUniversity().getName());
                internResponse.setMajor(intern.getMajor().getName());
                internResponse.setInternshipProgram(intern.getInternshipProgram().getName());
                internResponse.setStatus(intern.getStatus());

                // Map Team details
                TeamDetailResponse teamResponse = null;
                if (intern.getTeam() != null) {
                        teamResponse = teamService.mapToTeamDetailResponse(intern.getTeam());
                }

                // Combine into MyProfileResponse
                MyProfileResponse myProfileResponse = new MyProfileResponse();
                myProfileResponse.setInternDetails(internResponse);
                myProfileResponse.setTeamDetails(teamResponse);

                return myProfileResponse;
        }

        @Override
        public GetInternResponse updateIntern(Integer id, UpdateInternRequest updateInternRequest) {
                University university = universityRepository.findById(updateInternRequest.getUniversityId())
                                .orElseThrow(() -> new RuntimeException(ErrorCode.UNIVERSITY_NOT_EXISTED.getMessage()));

                Major major = majorRepository.findById(updateInternRequest.getMajorId())
                                .orElseThrow(() -> new RuntimeException(ErrorCode.MAJOR_NOT_EXISTED.getMessage()));

                Intern intern = internRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(ErrorCode.INTERN_NOT_EXISTED.getMessage()));

                try {
                        intern.setStatus(Intern.Status.valueOf(updateInternRequest.getStatus().toUpperCase()));
                } catch (Exception e) {
                        throw new RuntimeException(ErrorCode.STATUS_INVALID.getMessage());
                }
                intern.setUniversity(university);
                intern.setMajor(major);
                internRepository.save(intern);

                GetInternResponse response = modelMapper.map(intern.getUser(), GetInternResponse.class);
                response.setId(intern.getId());
                response.setUniversity(university.getName());
                response.setMajor(major.getName());
                response.setInternshipProgram(intern.getInternshipProgram().getName());
                response.setStatus(intern.getStatus());
                return response;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        @Override
        @Transactional
        public GetInternResponse createIntern(CreateInternRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException(ErrorCode.EMAIL_EXISTED.getMessage());
                }

                InternshipProgram internshipProgram = internshipProgramRepository
                                .findById(request.getInternshipProgramId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                ErrorCode.INTERNSHIP_PROGRAM_NOT_EXISTED.getMessage()));
                if (internshipProgram.getStatus() != InternshipProgram.Status.PUBLISHED) {
                        throw new RuntimeException(ErrorCode.TIME_APPLY_INVALID.getMessage());
                }

                University university = universityRepository.findById(request.getUniversityId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                ErrorCode.UNIVERSITY_NOT_EXISTED.getMessage()));
                Major major = majorRepository.findById(request.getMajorId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                ErrorCode.MAJOR_NOT_EXISTED.getMessage()));

                User user = modelMapper.map(request, User.class);
                user.setUsername(request.getEmail());
                user.setPassword(passwordEncoder.encode("123456@Abc"));
                user.setRole(Role.INTERN);
                User savedUser = userRepository.save(user);

                Intern intern = new Intern();
                intern.setUser(savedUser);
                intern.setStatus(Intern.Status.ACTIVE);
                intern.setMajor(major);
                intern.setUniversity(university);
                intern.setInternshipProgram(internshipProgram);
                Intern savedIntern = internRepository.save(intern);

                GetInternResponse internResponse = modelMapper.map(savedUser, GetInternResponse.class);
                internResponse.setId(savedIntern.getId());
                internResponse.setMajor(savedIntern.getMajor().getName());
                internResponse.setInternshipProgram(savedIntern.getInternshipProgram().getName());
                internResponse.setUniversity(savedIntern.getUniversity().getName());
                internResponse.setStatus(savedIntern.getStatus());

                return internResponse;
        }

        public PagedResponse<GetInternResponse> getAllIntern(GetAllInternRequest request) {
                int page = Math.max(0, request.getPage() - 1);
                int size = 15;
                PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
                Page<Intern> interns = internRepository.searchInterns(request.getMajorId(), request.getUniversityId(),
                                request.getKeyWord(), pageable);

                List<GetInternResponse> response = interns.map(intern -> {
                        GetInternResponse dto = modelMapper.map(intern.getUser(), GetInternResponse.class);
                        modelMapper.map(intern, dto);
                        dto.setMajor(intern.getMajor().getName());
                        dto.setUniversity(intern.getUniversity().getName());
                        dto.setInternshipProgram(intern.getInternshipProgram().getName());
                        return dto;
                }).getContent();

                return new PagedResponse<>(
                                response,
                                page + 1,
                                interns.getTotalElements(),
                                interns.getTotalPages(),
                                interns.hasNext(),
                                interns.hasPrevious());
        }

        /*
         * Hàm lấy ra danh sách intern chưa có nhóm,
         * status ACTIVE, và có kỳ thực tập trùng với nhóm
         */
        @Override // Thật ra nguyên đoạn dưới chỉ cần viết bằng 1 repository với @Query là xong
        public List<GetAllInternNoTeamResponse> getAllInternNoTeam(Integer teamId) {
                // Lấy ra team để lấy ra InternshipProgram
                Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                ErrorCode.TEAM_NOT_EXISTED.getMessage()));

                InternshipProgram internshipProgram = team.getInternshipProgram();

                // Lấy ra intern thỏa mãn điều kiện
                List<Intern> interns = internRepository.findAll().stream()
                                .filter(intern -> intern.getStatus() == Intern.Status.ACTIVE
                                                && intern.getInternshipProgram() == internshipProgram
                                                && intern.getTeam() == null)
                                .toList();

                return interns.stream()
                                .map(intern -> {
                                        GetAllInternNoTeamResponse res = modelMapper.map(intern.getUser(),
                                                        GetAllInternNoTeamResponse.class);
                                        res.setId(intern.getId());
                                        return res;
                                })
                                .toList();
        }

        @Override
        public Integer getAuthenticatedInternTeamId() {
                User user = authService.getUserLogin();
                Intern intern = internRepository.findByUser(user)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                ErrorCode.INTERN_NOT_EXISTED.getMessage()));

                if (intern.getTeam() != null) {
                        return intern.getTeam().getId();
                }
                return null; // Intern is not assigned to a team
        }

        @Override // Hàm lấy intern theo ID nhóm
        public List<GetInternResponse> getAllInternByTeamId(Integer teamId) {
                Team team = teamRepository.findById(teamId).orElseThrow(
                                () -> new EntityNotFoundException(ErrorCode.TEAM_NOT_EXISTED.getMessage()));
                List<Intern> interns = team.getInterns();                
                List<GetInternResponse> response = interns.stream().map(intern -> {
                        GetInternResponse dto = modelMapper.map(intern.getUser(), GetInternResponse.class);
                        modelMapper.map(intern, dto);
                        dto.setMajor(intern.getMajor().getName());
                        dto.setUniversity(intern.getUniversity().getName());
                        dto.setInternshipProgram(intern.getInternshipProgram().getName());
                        return dto;
                }).toList();
                
                return response;
        }
}
