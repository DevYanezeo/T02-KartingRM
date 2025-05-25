package com.tingeso.specialday_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SpecialPricingRequestDTO {
    private String type;
    private String dayName;
    private LocalDate specificDate;
    private Double priceMultiplier;
    private Integer minGroupSize;
    private Integer maxGroupSize;
    private Integer maxBirthdayPersons;
    private Double birthdayDiscount;
    private Boolean isActive;
}