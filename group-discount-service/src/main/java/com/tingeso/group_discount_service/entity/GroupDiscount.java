package com.tingeso.group_discount_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "group_discounts")
@AllArgsConstructor
@NoArgsConstructor
public class GroupDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_people", nullable = false)
    private Integer minPeople;  // Ej: 1, 3, 6, 11

    @Column(name = "max_people", nullable = false)
    private Integer maxPeople;  // Ej: 2, 5, 10, 15

    @Column(name = "percentage", nullable = false)
    private Integer percentage; // Ej: 0, 10, 20, 30
}