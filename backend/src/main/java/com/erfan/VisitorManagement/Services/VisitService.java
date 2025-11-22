package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Dtos.CreateVisitDto;
import com.erfan.VisitorManagement.Dtos.VisitResponseDto;
import com.erfan.VisitorManagement.Models.Visit;

import java.util.List;

public interface VisitService {

    VisitResponseDto createVisit(CreateVisitDto dto);

    Visit approveVisit(Long visitId);

    Visit rejectVisit(Long visitId);

    Visit checkIn(Long visitId,Long securityId);

    Visit checkOut(Long visitId,Long securityId);

    List<Visit> listActiveVisits();// For security guard dashboard

    Visit scanVisit(Long visitId, Long securityId);
}

