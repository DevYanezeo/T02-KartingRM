package com.tingeso.booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceCode; // Código único del comprobante
    private LocalDateTime fechaEmision; // Fecha de emisión del comprobante

    private Integer montoTotalSinIVA; // Suma de todos los montos finales sin IVA
    private Integer ivaTotal;         // Suma total del IVA
    private Integer montoTotalConIVA; // Suma total con IVA

    private String pdfUrl; // URL o ruta del comprobante PDF generado

    private String nombreResponsable; // (opcional)

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking; // Referencia a la reserva

    // Si quieres guardar el detalle de cada participante como JSON:
    @Lob
    private String detalleParticipantesJson; // (opcional, para trazabilidad)
}