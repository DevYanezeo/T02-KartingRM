package com.tingeso.booking_service.dtos;

import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingParticipantDTO {
    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;
}