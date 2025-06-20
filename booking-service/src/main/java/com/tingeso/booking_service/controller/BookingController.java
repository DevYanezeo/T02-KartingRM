package com.tingeso.booking_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tingeso.booking_service.dtos.BookingDTO;
import com.tingeso.booking_service.dtos.BookingRequestDTO;
import com.tingeso.booking_service.dtos.DetalleParticipanteDTO;
import com.tingeso.booking_service.dtos.InvoiceDTO;
import com.tingeso.booking_service.entity.Invoice;
import com.tingeso.booking_service.repository.InvoiceRepository;
import com.tingeso.booking_service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    // Crear una nueva reserva
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingRequestDTO request) {
        BookingDTO booking = bookingService.createBooking(request);
        return ResponseEntity.ok(booking);
    }

    // Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceDTO> dtos = invoices.stream()
                .map(inv -> {
                    // Si guardas detalleParticipantes como JSON en la entidad, deserialízalo aquí
                    List<DetalleParticipanteDTO> detalle = new ArrayList<>();
                    try {
                        if (inv.getDetalleParticipantesJson() != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            detalle = Arrays.asList(mapper.readValue(inv.getDetalleParticipantesJson(), DetalleParticipanteDTO[].class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new InvoiceDTO(
                            inv.getId(), // <--- agrega el id aquí
                            inv.getInvoiceCode(),
                            inv.getFechaEmision(),
                            inv.getMontoTotalSinIVA(),
                            inv.getIvaTotal(),
                            inv.getMontoTotalConIVA(),
                            inv.getPdfUrl(),
                            inv.getNombreResponsable(),
                            detalle
                    );
                })
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // Descargar PDF de una boleta
    @GetMapping("/invoices/{id}/download")
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