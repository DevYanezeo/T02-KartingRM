package com.tingeso.report_service.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngresosPorPersonasDTO {
    private String rangoPersonas; // "1-3", "4-6", etc.
    private Map<String, BigDecimal> meses; // "2024-01" -> monto
} 