package com.tingeso.booking_service.controller;


import com.tingeso.booking_service.entity.Invoice;
import com.tingeso.booking_service.repository.InvoiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // Obtener todas las boletas
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return ResponseEntity.ok(invoices);
    }

    // Descargar PDF de una boleta
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadInvoicePdf(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada"));
        File file = new File(invoice.getPdfUrl());
        if (!file.exists()) {
            throw new RuntimeException("Archivo PDF no encontrado");
        }
        Resource resource = new org.springframework.core.io.FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .contentLength(file.length())
                .body(resource);
    }
}