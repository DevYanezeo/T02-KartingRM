package com.tingeso.report_service.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleParticipanteDTO {
    private String nombre;
    private Double tarifaBase;
    private Double recargoEspecial;
    private Double descuentoGrupo;
    private Double descuentoLoyalty;
    private Double descuentoCumple;
    private Double montoFinalSinIVA;
    private Double iva;
    private Double montoFinalConIVA;
} 