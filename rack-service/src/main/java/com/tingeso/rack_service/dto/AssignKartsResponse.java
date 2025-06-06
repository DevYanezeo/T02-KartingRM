package com.tingeso.rack_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssignKartsResponse {
    private List<String> assignedKarts;
    private boolean success;
    private String message;
} 