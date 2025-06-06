package com.tingeso.rack_service.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AssignKartsRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int quantity;
    private String bookingCode;
} 