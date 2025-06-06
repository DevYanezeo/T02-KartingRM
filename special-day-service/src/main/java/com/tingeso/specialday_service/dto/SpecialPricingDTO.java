package com.tingeso.specialday_service.dto;

import com.tingeso.specialday_service.entity.SpecialPricing;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SpecialPricingDTO {
    private SpecialPricing.PricingType type;
    private String dayName;
    private LocalDate specificDate;
    private Double priceMultiplier;
    private Integer minGroupSize;
    private Integer maxGroupSize;
    private Integer maxBirthdayPersons;
}