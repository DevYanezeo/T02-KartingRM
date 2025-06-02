package com.tingeso.booking_service.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private String reservationCode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer laps;
    private Integer duration;
    private String status;
    private String clientName;
    private List<String> assignedKarts;
    private Double totalAmount;
    private String invoiceNumber;
}