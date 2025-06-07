package com.tingeso.booking_service.dtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private String invoiceCode;
    private LocalDateTime fechaEmision;
    private Integer montoTotalSinIVA;
    private Integer ivaTotal;
    private Integer montoTotalConIVA;
    private String pdfUrl;
    private String nombreResponsable;
    private List<DetalleParticipanteDTO> detalleParticipantes;
}