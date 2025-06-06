package com.tingeso.booking_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookingCode;
    private LocalDateTime fechaReserva; // Fecha y hora en que se hizo la reserva

    @Enumerated(EnumType.STRING)
    private Status status;              // Estado de la reserva

    private Integer numVueltas;         // Número de vueltas reservadas

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BookingParticipant> participantes;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Invoice invoice;

    @ElementCollection
    private List<String> assignedKarts;

    public enum Status {
        PENDIENTE, CONFIRMADA, CANCELADA
    }
}