package com.example.InternShip.service.impl;

import com.example.InternShip.dto.AllowanceResponse;
import com.example.InternShip.dto.request.AllowanceRequest;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.Allowance;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.User;
import com.example.InternShip.exception.NotFoundException;
import com.example.InternShip.exception.InvalidRequestException;
import com.example.InternShip.repository.AllowanceRepository;
import com.example.InternShip.repository.UserRepository;
import com.example.InternShip.service.AllowanceService;
import com.example.InternShip.service.AuthService;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.InternShip.repository.InternRepository;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllowanceServiceImpl implements AllowanceService {

    private final AllowanceRepository allowanceRepository;
    private final InternRepository internRepository;
    private final UserRepository userRepository; // Injected UserRepository
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AllowanceResponse> getAllAllowances(Long internshipProgramId, String keyword, String status,
            Pageable pageable) {
        Specification<Allowance> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                Join<Allowance, Intern> internJoin = root.join("intern");
                Join<Intern, User> userJoin = internJoin.join("user");
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("fullName")),
                                "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")),
                                "%" + keyword.toLowerCase() + "%")));
            }

            if (internshipProgramId != null && internshipProgramId > 0) {
                predicates.add(criteriaBuilder.equal(root.join("intern").join("internshipProgram").get("id"),
                        internshipProgramId));
            }

            if (StringUtils.hasText(status)) {
                try {
                    // We assume CANCELLED is now a valid status in the enum/entity
                    Allowance.Status statusEnum = Allowance.Status.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    log.error("Invalid status value: {}", status);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        int page = Math.max(0, pageable.getPageNumber());
        int size = 15;
        Pageable newPageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Allowance> allowancePage = allowanceRepository.findAll(spec, newPageable);
        List<AllowanceResponse> allowanceResponses = allowancePage.getContent()
                .stream()
                .map(this::mapToAllowanceResponse)
                .toList();

        return new PagedResponse<>(
                allowanceResponses,
                allowancePage.getNumber() + 1,
                allowancePage.getTotalElements(),
                allowancePage.getTotalPages(),
                allowancePage.hasNext(),
                allowancePage.hasPrevious());
    }


    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AllowanceResponse> getMyAllowances(Pageable pageable) {
        User user = authService.getUserLogin();
        Intern intern = internRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Intern not found for user: " + user.getFullName()));

        Page<Allowance> allowancePage = allowanceRepository.findByIntern_Id(intern.getId(), pageable);
        List<AllowanceResponse> allowanceResponses = allowancePage.getContent()
                .stream()
                .map(this::mapToAllowanceResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                allowanceResponses,
                allowancePage.getNumber() + 1,
                allowancePage.getTotalElements(),
                allowancePage.getTotalPages(),
                allowancePage.hasNext(),
                allowancePage.hasPrevious());
    }

    private AllowanceResponse mapToAllowanceResponse(Allowance allowance) {
        if (allowance == null) {
            return null;
        }

        String remitterName = Optional.ofNullable(allowance.getRemitter())
                .map(User::getFullName)
                .orElse(null);

        return new AllowanceResponse(
                (long) allowance.getId(),
                allowance.getIntern().getUser().getFullName(),
                allowance.getIntern().getUser().getEmail(),
                allowance.getIntern().getInternshipProgram().getName(),
                allowance.getAmount(),
                remitterName,
                allowance.getPaidAt(),
                allowance.getStatus().name());
    }
}
