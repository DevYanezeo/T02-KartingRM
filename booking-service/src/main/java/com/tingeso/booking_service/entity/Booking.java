package com.tingeso.booking_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 12)
    private String reservationCode; // Ej: RES-ABC123

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer duration; // En minutos: 30, 35, 40

    @Column(nullable = false)
    private Integer laps; // 10, 15 o 20

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CONFIRMED;

    @Column(nullable = false)
    private Long clientId; // ID del cliente dueño

    @Column(columnDefinition = "TEXT")
    private String participantIds; // JSON: [1, 2, 3]

    @Column(columnDefinition = "TEXT")
    private String assignedKarts; // JSON: ["K001", "K002"]

    @Column(nullable = false)
    private Long pricingId; // ID de tarifa

    @Column(nullable = false)
    private Long invoiceId; // Relación con Invoice

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED
    }
}