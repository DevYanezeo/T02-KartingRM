package com.tingeso.booking_service.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleParticipanteDTO {
    private String nombre;
    private double tarifaBase;
    private double recargoEspecial;
    private double descuentoGrupo;
    private double descuentoLoyalty;
    private double descuentoCumple;
    private double montoFinalSinIVA;
    private double iva;
    private double montoFinalConIVA;
} 