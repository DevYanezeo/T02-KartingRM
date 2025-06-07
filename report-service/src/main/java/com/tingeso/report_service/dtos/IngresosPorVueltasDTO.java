package com.tingeso.report_service.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngresosPorVueltasDTO {
    private String tipo; // "10 vueltas", "15 vueltas", etc.
    private Map<String, BigDecimal> meses; // "2024-01" -> monto
} 