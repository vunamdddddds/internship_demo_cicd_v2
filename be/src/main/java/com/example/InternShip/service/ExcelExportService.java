package com.example.InternShip.service;

import com.example.InternShip.entity.Intern;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExcelExportService {
    ByteArrayInputStream exportInternEvaluations(List<Intern> interns);
}