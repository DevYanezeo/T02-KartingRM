package com.tingeso.specialday_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SpecialDayPricingDTO {
    private boolean isSpecialDay;
    private String dayType;
    private String dayName;
    private double priceMultiplier;
}