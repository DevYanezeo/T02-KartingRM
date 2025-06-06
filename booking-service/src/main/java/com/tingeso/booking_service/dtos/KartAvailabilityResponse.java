package com.tingeso.booking_service.dtos;

import lombok.Data;
import java.util.List;

@Data
public class KartAvailabilityResponse {
    private boolean available;
    private List<String> availableKarts;
    private String message;
}