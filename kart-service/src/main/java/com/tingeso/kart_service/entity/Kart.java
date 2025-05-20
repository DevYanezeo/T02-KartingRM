package com.tingeso.kart_service.entity;

import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kart {

    private String kartCode;

    private String model;

    private boolean underMaintenance = false;

    private String description;

    private LocalDate lastMaintenance;

}