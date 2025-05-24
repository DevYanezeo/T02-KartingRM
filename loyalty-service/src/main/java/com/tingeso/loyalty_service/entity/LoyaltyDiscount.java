package com.tingeso.loyalty_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "loyalty_discounts")
public class LoyaltyDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tier; // "NO_FRECUENTE", "REGULAR", etc.

    @Column(nullable = false)
    private Integer minVisits; // Límite inferior de visitas

    private Integer maxVisits; // Límite superior (puede ser null para "7+")

    @Column(nullable = false)
    private Integer discountPercentage; // 0, 10, 20, 30
}