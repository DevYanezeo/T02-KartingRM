package com.tingeso.booking_service.service;

import com.lowagie.text.Font;
import com.tingeso.booking_service.entity.Invoice;
import com.tingeso.booking_service.entity.Booking;
import com.tingeso.booking_service.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.tingeso.booking_service.dtos.DetalleParticipanteDTO;

import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;
import java.io.File;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice generarInvoice(Booking booking, int montoTotalSinIVA, int ivaTotal, int montoTotalConIVA, String nombreResponsable, String detalleParticipantesJson) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceCode("INV-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setFechaEmision(LocalDateTime.now());
        invoice.setMontoTotalSinIVA(montoTotalSinIVA);
        invoice.setIvaTotal(ivaTotal);
        invoice.setMontoTotalConIVA(montoTotalConIVA);
        invoice.setBooking(booking);
        invoice.setNombreResponsable(nombreResponsable);
        invoice.setDetalleParticipantesJson(detalleParticipantesJson);
        // El campo pdfUrl lo puedes llenar después de generar el PDF
        return invoiceRepository.save(invoice);
    }

    public String generarPdfInvoice(Invoice invoice, List<DetalleParticipanteDTO> detalleParticipantes) {
        // Usar el directorio temporal del sistema para máxima compatibilidad
        String dirPath = System.getProperty("java.io.tmpdir") + File.separator + "invoices";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "invoice_" + invoice.getInvoiceCode() + ".pdf";
        String filePath = dirPath + File.separator + fileName;

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("Comprobante de Reserva Karting", titleFont));
            document.add(new Paragraph(" "));

            // Info de la reserva
            document.add(new Paragraph("Código de reserva: " + invoice.getBooking().getBookingCode()));
            document.add(new Paragraph("Fecha de emisión: " + invoice.getFechaEmision()));
            document.add(new Paragraph("Responsable: " + invoice.getNombreResponsable()));
            document.add(new Paragraph("Cantidad de personas: " + invoice.getBooking().getParticipantes().size()));
            document.add(new Paragraph("Número de vueltas: " + invoice.getBooking().getNumVueltas()));
            document.add(new Paragraph(" "));

            // Tabla de participantes
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            String[] headers = {
                "Nombre Completo",
                "Tarifa Base",
                "Recargo Día Especial",
                "Descuento Grupo",
                "Descuento Cliente Frecuente",
                "Descuento Cumpleaños",
                "Monto Final sin IVA",
                "IVA",
                "Total con IVA"
            };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new Color(230, 230, 250));
                table.addCell(cell);
            }

            // Filas de participantes
            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (DetalleParticipanteDTO d : detalleParticipantes) {
                table.addCell(new Phrase(d.getNombre(), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getTarifaBase()), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getRecargoEspecial()), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getDescuentoGrupo()), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getDescuentoLoyalty()), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getDescuentoCumple()), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getMontoFinalSinIVA()), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getIva()), rowFont));
                table.addCell(new Phrase(String.format("%,.0f", d.getMontoFinalConIVA()), rowFont));
            }
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Monto total sin IVA: " + invoice.getMontoTotalSinIVA()));
            document.add(new Paragraph("IVA: " + invoice.getIvaTotal()));
            document.add(new Paragraph("Monto total con IVA: " + invoice.getMontoTotalConIVA()));

            document.close();
            // Actualiza el campo pdfUrl y guarda el invoice
            invoice.setPdfUrl(filePath);
            invoiceRepository.save(invoice);
            System.out.println("PDF generado en: " + filePath);
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InvoiceRepository getInvoiceRepository() {
        return invoiceRepository;
    }
} 