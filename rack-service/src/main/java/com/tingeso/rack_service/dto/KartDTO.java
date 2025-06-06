package com.tingeso.rack_service.dto;

import lombok.Data;

@Data
public class KartDTO {
    private String kartCode;
    private String model;
    private boolean underMaintenance;
    private String description;
} 