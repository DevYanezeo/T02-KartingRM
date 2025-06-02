package com.tingeso.booking_service.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.tingeso.booking_service.dtos.ReceiptDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ReceiptService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    public byte[] generatePdfReceipt(ReceiptDTO receipt) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        addTitle(document, "KartingRM - Comprobante de Reserva");
        addInvoiceInfo(document, receipt);
        addBookingInfo(document, receipt);
        addParticipantsTable(document, receipt);
        addSummary(document, receipt);

        document.close();
        return out.toByteArray();
    }

    private void addTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20f);
        document.add(p);
    }

    // ... métodos para construir el PDF
}