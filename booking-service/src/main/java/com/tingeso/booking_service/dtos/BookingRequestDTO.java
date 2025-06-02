package com.tingeso.booking_service.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingRequestDTO {
    private LocalDate date;
    private LocalTime startTime;
    private Integer laps;
    private Long clientId;
    private List<Long> participantIds;
    private String paymentMethod;
}