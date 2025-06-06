package com.tingeso.rack_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rack")
public class Rack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;         // Día de la reserva
    private LocalTime horaInicio;    // Hora de inicio del bloque
    private LocalTime horaFin;       // Hora de fin del bloque

    @ElementCollection
    private List<String> kartsAsignados; // Lista de códigos de karts ocupados en este bloque

    private String bookingCode;      // Código de la reserva asociada
}
