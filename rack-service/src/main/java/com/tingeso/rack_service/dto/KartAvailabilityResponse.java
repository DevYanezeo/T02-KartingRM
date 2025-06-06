package com.tingeso.rack_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class KartAvailabilityResponse {
    private boolean available;
    private List<String> availableKarts;
    private String message;
}