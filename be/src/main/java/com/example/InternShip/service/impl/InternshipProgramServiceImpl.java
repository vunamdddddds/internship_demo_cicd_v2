package com.example.InternShip.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.InternShip.dto.internshipProgram.request.CreateInternProgramRequest;
import com.example.InternShip.dto.internshipProgram.request.UpdateInternProgramRequest;
import com.example.InternShip.dto.internshipProgram.response.GetAllInternProgramResponse;
import com.example.InternShip.dto.internshipProgram.response.GetInternProgramResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.job.EndPublishJob;
import com.example.InternShip.job.EndReviewJob;
import com.example.InternShip.job.StartInternship;
import com.example.InternShip.repository.*;
import com.example.InternShip.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.InternShip.service.InternshipProgramService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InternshipProgramServiceImpl implements InternshipProgramService {
    private final InternshipProgramRepository internshipProgramRepository;
    private final InternshipApplicationRepository internshipApplicationRepository;
    private final DepartmentRepository departmentRepository;
    private final AuthServiceImpl authService;
    private final ModelMapper modelMapper;
    private final Scheduler scheduler;
    private final EmailService emailService;

    @Override
    public List<GetAllInternProgramResponse> getAllPrograms() {
        Role role = authService.getUserLogin().getRole();
        List<InternshipProgram> results = null;
        if (role.equals(Role.VISITOR)) {
            results = internshipProgramRepository.findAllByStatus(InternshipProgram.Status.PUBLISHED);
        }else {
            results = internshipProgramRepository.findAll();
        }
        return results.stream()
                .map(result -> modelMapper.map(result, GetAllInternProgramResponse.class))
                .toList();
    }

    @Override // Tùng
    public PagedResponse<GetInternProgramResponse> getAllInternshipPrograms(List<Integer> department,
                                                                            String keyword, int page) {
        page = Math.max(0, page - 1);
        PageRequest pageable = PageRequest.of(page, 10, Sort.by("id").descending());

        // Kiểm tra null vì Hibernate không coi List rỗng là null
        if (department == null || department.isEmpty()) {
            department = null;
        }

        Page<InternshipProgram> internshipPrograms = internshipProgramRepository.searchInternshipProgram(department,
                keyword, pageable);

        List<GetInternProgramResponse> responses = internshipPrograms.stream()
                .map(internshipProgram -> {
                    GetInternProgramResponse response = modelMapper.map(internshipProgram, GetInternProgramResponse.class);
                    response.setDepartment(internshipProgram.getDepartment().getName());
                    return response;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page + 1,
                internshipPrograms.getTotalElements(),
                internshipPrograms.getTotalPages(),
                internshipPrograms.hasNext(),
                internshipPrograms.hasPrevious());
    }

    // thêm InternProgram
    @Override
    public GetInternProgramResponse createInternProgram (CreateInternProgramRequest request) throws SchedulerException {
        if (!(LocalDateTime.now().isBefore(request.getEndPublishedTime()) &&
                request.getEndPublishedTime().isBefore(request.getEndReviewingTime()) &&
                request.getEndReviewingTime().isBefore(request.getTimeStart()))) {
            throw new IllegalArgumentException(ErrorCode.TIME_INVALID.getMessage());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.DEPARTMENT_NOT_EXISTED.getMessage()));

        InternshipProgram.Status status = request.isDraft() ? InternshipProgram.Status.DRAFT
                : InternshipProgram.Status.PUBLISHED;

        InternshipProgram internshipProgram = modelMapper.map(request, InternshipProgram.class);
        internshipProgram.setDepartment(department);
        internshipProgram.setStatus(status);
        internshipProgramRepository.save(internshipProgram);
        if (!request.isDraft()) {
            try {
                scheduleInternship(internshipProgram.getId(), EndPublishJob.class, internshipProgram.getEndPublishedTime());
                scheduleInternship(internshipProgram.getId(), EndReviewJob.class, internshipProgram.getEndReviewingTime());
                scheduleInternship(internshipProgram.getId(), StartInternship.class, internshipProgram.getTimeStart());
            }catch (SchedulerException e){
                throw new SchedulerException(ErrorCode.SCHEDULER_FAILED.getMessage());
            }
        }
        GetInternProgramResponse response = modelMapper.map(internshipProgram, GetInternProgramResponse.class);
        response.setDepartment(internshipProgram.getDepartment().getName());
        return response;
    }

    // sửa InternProgram
    @Override
    public GetInternProgramResponse updateInternProgram(UpdateInternProgramRequest request, int id) throws SchedulerException {
        if (!(request.getEndPublishedTime().isBefore(request.getEndReviewingTime()) &&
                request.getEndReviewingTime().isBefore(request.getTimeStart()))) {
            throw new IllegalArgumentException(ErrorCode.TIME_INVALID.getMessage());
        }

        InternshipProgram internshipProgram = internshipProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_PROGRAM_NOT_EXISTED.getMessage()));

        if (internshipProgram.getStatus() == InternshipProgram.Status.DRAFT) {
            modelMapper.map(request, internshipProgram);
        }else {
            internshipProgram.setName(request.getName());
            if (internshipProgram.getEndPublishedTime().isAfter(LocalDateTime.now())) {
                internshipProgram.setEndPublishedTime(request.getEndPublishedTime());
                scheduleInternship(internshipProgram.getId(), EndPublishJob.class, internshipProgram.getEndPublishedTime());
            }
            if (internshipProgram.getEndReviewingTime().isAfter(LocalDateTime.now())) {
                internshipProgram.setEndReviewingTime(request.getEndReviewingTime());
                scheduleInternship(internshipProgram.getId(), EndReviewJob.class, internshipProgram.getEndReviewingTime());
            }
            if (internshipProgram.getTimeStart().isAfter(LocalDateTime.now())) {
                internshipProgram.setTimeStart(request.getTimeStart());
                scheduleInternship(internshipProgram.getId(), StartInternship.class, internshipProgram.getTimeStart());
            }
        }
        internshipProgramRepository.save(internshipProgram);

        return modelMapper.map(internshipProgram, GetInternProgramResponse.class);
    }

    // hủy InternProgram
    public GetInternProgramResponse cancelInternProgram(int id) throws SchedulerException {
        InternshipProgram internshipProgram = internshipProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_PROGRAM_NOT_EXISTED.getMessage()));

        if (internshipProgram.getStatus() == InternshipProgram.Status.COMPLETED ||
                internshipProgram.getStatus() == InternshipProgram.Status.ONGOING) {
            throw new IllegalArgumentException(ErrorCode.STATUS_INVALID.getMessage());
        }

        internshipProgram.setStatus(InternshipProgram.Status.CANCELLED);
        internshipProgram.getApplications()
                .forEach(app -> app.setStatus(InternshipApplication.Status.REJECTED));

        internshipProgramRepository.save(internshipProgram);

        deleteAllJobsForProgram(id);

        return modelMapper.map(internshipProgram, GetInternProgramResponse.class);
    }

    public GetInternProgramResponse publishInternProgram(int id) throws SchedulerException {
        InternshipProgram internshipProgram = internshipProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_PROGRAM_NOT_EXISTED.getMessage()));

        if (internshipProgram.getStatus() != InternshipProgram.Status.DRAFT) {
            throw new IllegalArgumentException(ErrorCode.STATUS_INVALID.getMessage());
        }
        if (internshipProgram.getEndPublishedTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ErrorCode.TIME_INVALID.getMessage());
        }

        internshipProgram.setStatus(InternshipProgram.Status.PUBLISHED);
        internshipProgramRepository.save(internshipProgram);

        try {
            scheduleInternship(internshipProgram.getId(), EndPublishJob.class, internshipProgram.getEndPublishedTime());
            scheduleInternship(internshipProgram.getId(), EndReviewJob.class, internshipProgram.getEndReviewingTime());
            scheduleInternship(internshipProgram.getId(), StartInternship.class, internshipProgram.getTimeStart());
        }catch (SchedulerException e){
            throw new SchedulerException(ErrorCode.SCHEDULER_FAILED.getMessage());
        }

        return modelMapper.map(internshipProgram, GetInternProgramResponse.class);
    }

    @Transactional
    public void endPublish (int programId){
        InternshipProgram internshipProgram = internshipProgramRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_TERM_NOT_EXISTED.getMessage()));
        List<InternshipApplication> applications = internshipProgram.getApplications();
        List<InternshipApplication> toUpdate = new ArrayList<>();

        for (InternshipApplication app : applications){
            if (app.getStatus() == InternshipApplication.Status.SUBMITTED){
                app.setStatus(InternshipApplication.Status.UNDER_REVIEW);
                toUpdate.add(app);
            }
        }
        internshipProgram.setStatus(InternshipProgram.Status.REVIEWING);
        internshipProgramRepository.save(internshipProgram);
        internshipApplicationRepository.saveAll(toUpdate);
    }

    @Transactional
    public void endReviewing (int programId){
        InternshipProgram internshipProgram = internshipProgramRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_TERM_NOT_EXISTED.getMessage()));
        List<InternshipApplication> applications = internshipProgram.getApplications();
        List<InternshipApplication> toUpdate = new ArrayList<>();

        for (InternshipApplication app : applications){
            if (app.getStatus() == InternshipApplication.Status.UNDER_REVIEW){
                app.setStatus(InternshipApplication.Status.REJECTED);
                toUpdate.add(app);
            }
        }
        internshipProgram.setStatus(InternshipProgram.Status.PENDING);
        internshipProgramRepository.save(internshipProgram);
        internshipApplicationRepository.saveAll(toUpdate);
        for (InternshipApplication app : applications){
            emailService.sendApplicationStatusEmail(app);
        }
    }

    @Transactional
    public void startInternship(int programId){
        InternshipProgram internshipProgram = internshipProgramRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_TERM_NOT_EXISTED.getMessage()));
        List<InternshipApplication> applications = internshipProgram.getApplications();
        List<InternshipApplication> toUpdateApp = new ArrayList<>();

        for (InternshipApplication app : applications){
            if (app.getStatus() == InternshipApplication.Status.APPROVED) {
                app.setStatus(InternshipApplication.Status.NOT_CONTRACT);
                toUpdateApp.add(app);
            }
        }
        internshipProgram.setStatus(InternshipProgram.Status.ONGOING);
        internshipProgramRepository.save(internshipProgram);
        internshipApplicationRepository.saveAll(toUpdateApp);
    }

    public void scheduleInternship (int programId, Class<? extends Job> jobClass, LocalDateTime timeStart) throws SchedulerException {
        Date startDate = Date.from(timeStart.atZone(ZoneId.systemDefault()).toInstant());

        JobKey jobKey = new JobKey("job_" + programId, jobClass.getSimpleName());
        TriggerKey triggerKey = new TriggerKey("trigger_" + programId, jobClass.getSimpleName());

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .usingJobData("programId", programId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(startDate)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    // Xóa job của internProgram có id là programId
    private void deleteAllJobsForProgram(int programId) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if (jobKey.getName().startsWith("job_" + programId)) {
                    scheduler.deleteJob(jobKey);
                }
            }
        }
    }
}
