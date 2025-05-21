package com.tingeso.pricing_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pricing")
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer laps;  // 10, 15 o 20 vueltas

    @Column(nullable = false)
    private Double basePrice;  // Precio base (ej: 15000 CLP)

    @Column(nullable = false)
    private Integer totalDuration;  // Duración en minutos (ej: 30)

    // Eliminamos la relación con Booking (ahora es otro microservicio)
}