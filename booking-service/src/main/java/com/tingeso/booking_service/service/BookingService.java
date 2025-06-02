package com.tingeso.booking_service.service;


import com.tingeso.booking_service.dtos.*;
import com.tingeso.booking_service.entity.*;
import com.tingeso.booking_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReceiptService receiptService;

    @Transactional
    public BookingResponseDTO    createBooking(BookingRequestDTO request) {
        // 1. Validar horario comercial
        validateBusinessHours(request);

        // 2. Obtener información del cliente
        ClientDTO owner = getClientInfo(request.getClientId());

        // 3. Calcular duración
        Integer duration = getDuration(request.getLaps());

        // 4. Asignar karts
        List<String> assignedKarts = assignKarts(request.getDate(), request.getStartTime(),
                duration, request.getParticipantIds().size() + 1);

        // 5. Obtener tarifa
        PricingDTO pricing = getPricingInfo(request.getLaps(), duration);

        // 6. Calcular precios y descuentos
        PriceCalculationResult priceResult = calculateFinalPrice(request, pricing.getBasePrice());

        // 7. Crear reserva
        Booking booking = createBookingEntity(request, duration, assignedKarts, pricing.getId());
        booking = bookingRepository.save(booking);

        // 8. Crear factura
        Invoice invoice = createInvoice(booking, priceResult, owner);
        invoice = invoiceRepository.save(invoice);

        // 9. Actualizar booking con invoiceId
        booking.setInvoiceId(invoice.getId());
        bookingRepository.save(booking);

        // 10. Actualizar visitas de clientes
        updateClientVisits(request.getClientId(), request.getParticipantIds());

        return mapToBookingResponse(booking, invoice, assignedKarts, owner);
    }

    private void validateBusinessHours(BookingRequestDTO request) {
        Boolean isValid = restTemplate.getForObject(
                "http://business-hour-service/api/business-hours/validate?" +
                        "date=" + request.getDate() +
                        "&startTime=" + request.getStartTime() +
                        "&duration=" + getDuration(request.getLaps()),
                Boolean.class);

        if (!Boolean.TRUE.equals(isValid)) {
            throw new IllegalArgumentException("El horario de reserva está fuera del horario comercial");
        }
    }

    // ... otros métodos auxiliares
}