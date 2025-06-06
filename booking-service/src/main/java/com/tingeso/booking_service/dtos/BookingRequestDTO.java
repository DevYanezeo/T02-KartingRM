package com.tingeso.booking_service.dtos;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private LocalDateTime fechaUso;
    private Integer numVueltas;
    private List<BookingParticipantDTO> participantes;
}