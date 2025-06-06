package com.tingeso.specialday_service.dto;

import lombok.Data;

@Data
public class SpecialPricingDTO {
    private boolean isSpecialDay;         // ¿Es un día especial?
    private String dayType;               // "WEEKEND", "HOLIDAY", "BIRTHDAY", "NORMAL"
    private String dayName;               // "SATURDAY", "SUNDAY", "NAVIDAD", etc.
    private double priceMultiplier;       // Multiplicador de precio (ej: 1.5, 2.0, 0.5)
    private Integer maxApplicablePersons; // Solo para cumpleaños, null en otros casos
    private double discountPercentage;    // Solo para cumpleaños, 0 en otros casos
    private String message;               // Mensaje opcional para mostrar en frontend
}