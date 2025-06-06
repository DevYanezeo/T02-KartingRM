package com.tingeso.rack_service.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class KartAvailabilityRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int quantity;
}