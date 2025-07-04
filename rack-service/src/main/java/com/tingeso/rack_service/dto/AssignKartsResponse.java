package com.tingeso.rack_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignKartsResponse {
    private List<String> assignedKarts;
    private boolean success;
    private String message;
} 