package com.tingeso.booking_service.dtos;

import lombok.AllArgsConstructor;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricingDTO {
    private Long id;
    private Integer laps;
    private Double basePrice;
    private Integer totalDuration;
}
