package com.tingeso.specialday_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "special_pricing")
public class SpecialPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // "WEEKEND", "HOLIDAY", "BIRTHDAY"

    @Column
    private String dayName; // "SATURDAY", "SUNDAY", "NAVIDAD", etc.

    @Column
    private LocalDate specificDate; // Para feriados específicos

    @Column(nullable = false)
    private Double priceMultiplier = 1.0; // 1.5 = +50%

    @Column
    private Integer minGroupSize; // Para promociones grupales

    @Column
    private Integer maxGroupSize;

    @Column
    private Integer maxBirthdayPersons; // Máximo de personas con descuento

    @Column
    private Double birthdayDiscount; // 0.5 = 50%
}