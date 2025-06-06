package com.tingeso.booking_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookingParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;      // Nombre del participante
    private String email;       // Email del participante

    private LocalDate fechaNacimiento;        

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking; // Referencia a la reserva

}