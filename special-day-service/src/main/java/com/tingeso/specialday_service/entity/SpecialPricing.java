package com.tingeso.specialday_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "special_pricing")
public class SpecialPricing {
    public enum PricingType {
        WEEKEND,
        HOLIDAY,
        BIRTHDAY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingType type;

    // Para WEEKEND: "SATURDAY"/"SUNDAY"
    // Para HOLIDAY: nombre del feriado ("NAVIDAD", "FIESTAS_PATRIAS")
    @Column
    private String dayName;

    // Para HOLIDAY: fecha específica (opcional)
    @Column
    private LocalDate specificDate;

    @Column(nullable = false)
    private Double priceMultiplier = 1.0;

    // Para BIRTHDAY:
    @Column
    private Integer minGroupSize;

    @Column
    private Integer maxGroupSize;

    @Column
    private Integer maxBirthdayPersons;

}