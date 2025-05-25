package com.tingeso.specialday_service.dto;

import lombok.Data;

@Data
public class BirthdayDiscountDTO {
    private Integer maxApplicablePersons;
    private Double discountPercentage;

    public BirthdayDiscountDTO(Integer maxApplicablePersons, Double discountPercentage) {
        this.maxApplicablePersons = maxApplicablePersons;
        this.discountPercentage = discountPercentage;
    }
}