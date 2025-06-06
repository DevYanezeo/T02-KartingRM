package com.tingeso.booking_service.dtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private LocalDateTime fechaReserva;
    private String status;
    private Integer numVueltas;
    private List<BookingParticipantDTO> participantes;
    private InvoiceDTO invoice;
    private List<String> kartsAsignados;
    private String observaciones;
} 