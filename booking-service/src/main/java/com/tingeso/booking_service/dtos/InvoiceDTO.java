package com.tingeso.booking_service.dtos;

import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Long id;
    private String invoiceCode;
    private LocalDateTime fechaEmision;
    private Integer montoTotalSinIVA;
    private Integer ivaTotal;
    private Integer montoTotalConIVA;
    private String pdfUrl;
}