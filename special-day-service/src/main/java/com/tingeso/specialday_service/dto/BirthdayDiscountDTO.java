package com.tingeso.specialday_service.dto;

import lombok.Data;

@Data
public class BirthdayDiscountDTO {
    private boolean qualifies;
    private Integer maxApplicablePersons;
    private double discountPercentage;
    private String message;
}