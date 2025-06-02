package com.tingeso.booking_service.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
public class ReceiptDTO {
    private String invoiceNumber;
    private LocalDateTime issueDate;
    private String reservationCode;
    private String clientName;
    private String clientEmail;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private Integer laps;
    private Integer duration;
    private Integer participantsCount;
    private Double basePrice;
    private Double subtotal;
    private Double iva;
    private Double total;
    private Map<String, Double> appliedDiscounts;
    private List<ParticipantDetailDTO> participantDetails;

    @Data
    public static class ParticipantDetailDTO {
        private String name;
        private Double basePrice;
        private Double discounts;
        private Double subtotal;
    }
}