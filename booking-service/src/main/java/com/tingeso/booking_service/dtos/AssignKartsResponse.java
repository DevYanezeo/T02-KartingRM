package com.tingeso.booking_service.dtos;

import lombok.*;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignKartsResponse {
    private List<String> assignedKarts;
    private boolean success;
    private String message;
} 