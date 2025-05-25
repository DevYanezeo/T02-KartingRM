package com.tingeso.specialday_service.dto;

import lombok.Data;

@Data
public class DateMultiplierDTO {
    private String dayType; // "WEEKEND", "HOLIDAY", "REGULAR"
    private String dayName; // "SATURDAY", "NAVIDAD", etc.
    private Double multiplier;

    public DateMultiplierDTO(String dayType, String dayName, Double multiplier) {
        this.dayType = dayType;
        this.dayName = dayName;
        this.multiplier = multiplier;
    }
}