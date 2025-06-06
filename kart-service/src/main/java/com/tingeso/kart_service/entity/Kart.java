package com.tingeso.kart_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Kart")
public class Kart {
    @Id
    @Column(name = "kart_code")
    private String kartCode;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private boolean underMaintenance = false;

    private String description;

    @Column(name = "last_maintenance")
    private LocalDate lastMaintenance;

}
