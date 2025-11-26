package com.example.InternShip.service;

import java.util.List;

import com.example.InternShip.dto.response.PagedResponse;

import com.example.InternShip.dto.internshipProgram.request.CreateInternProgramRequest;
import com.example.InternShip.dto.internshipProgram.request.UpdateInternProgramRequest;
import com.example.InternShip.dto.internshipProgram.response.GetAllInternProgramResponse;
import com.example.InternShip.dto.internshipProgram.response.GetInternProgramResponse;

import org.quartz.SchedulerException;


public interface InternshipProgramService {
    public List<GetAllInternProgramResponse> getAllPrograms();
    PagedResponse<GetInternProgramResponse> getAllInternshipPrograms(List<Integer> department, String keyword, boolean activeOnly, int page);
    public void endPublish (int programId);
    public void endReviewing (int programId);
    public void startInternship(int programId);
    public GetInternProgramResponse cancelInternProgram(int id) throws SchedulerException;
    public GetInternProgramResponse publishInternProgram(int id) throws SchedulerException;
    public Object createInternProgram(CreateInternProgramRequest request) throws SchedulerException;
    public Object updateInternProgram(UpdateInternProgramRequest request, int id) throws SchedulerException;

}
