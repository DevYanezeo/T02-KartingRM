package com.tingeso.booking_service.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialPricingDTO {
    private boolean isSpecialDay;
    private String dayType;
    private String dayName;
    private double priceMultiplier;
    private Integer maxApplicablePersons;
    private double discountPercentage;
    private String message;
}