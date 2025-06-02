package com.tingeso.booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double iva;

    @Column(nullable = false)
    private Double total;

    @Column(columnDefinition = "TEXT")
    private String discountDetails; // JSON con descuentos

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] pdfData;

    @Column(nullable = false)
    private Boolean pdfGenerated = false;
}