package com.tingeso.booking_service.service;

import com.tingeso.booking_service.dtos.*;
import com.tingeso.booking_service.entity.*;
import com.tingeso.booking_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingParticipantRepository participantRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public BookingDTO createBooking(BookingRequestDTO request) {
        // 1. Obtener tarifa base y duración desde pricing-service usando laps
        PricingDTO pricing = getPricingByLaps(request.getNumVueltas());

        // 2. Crear lista de participantes (aún sin descuentos)
        List<BookingParticipant> participantes = new ArrayList<>();
        for (BookingParticipantDTO dto : request.getParticipantes()) {
            BookingParticipant participante = new BookingParticipant();
            participante.setNombre(dto.getNombre());
            participante.setEmail(dto.getEmail());
            participante.setFechaNacimiento(dto.getFechaNacimiento());
            participante.setBooking(null); // Se asignará después de guardar Booking
            participantes.add(participante);
        }

        // 3. Crear Booking
        Booking booking = new Booking();
        booking.setBookingCode(generarCodigoReserva());
        booking.setFechaReserva(LocalDateTime.now());
        booking.setStatus(Booking.Status.PENDIENTE);
        booking.setNumVueltas(request.getNumVueltas());
        booking.setParticipantes(participantes);
        booking.setKartsAsignados(new ArrayList<>()); // Por ahora vacío
        // Asigna la relación inversa
        for (BookingParticipant participante : participantes) {
            participante.setBooking(booking);
        }
        booking = bookingRepository.save(booking);

        // 4. Mapear a BookingDTO para la respuesta
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setFechaReserva(booking.getFechaReserva());
        bookingDTO.setStatus(booking.getStatus().name());
        bookingDTO.setNumVueltas(booking.getNumVueltas());
        bookingDTO.setParticipantes(request.getParticipantes());
        bookingDTO.setKartsAsignados(booking.getKartsAsignados());
        // No se incluye invoice aún
        return bookingDTO;
    }

    // Métodos auxiliares (solo firmas, implementa según tus necesidades):

    private void validateBusinessHoursAndTrack(BookingRequestDTO request) {
        // Llama a track-service y/o business-hour-service para validar horario y disponibilidad
    }

    private PricingDTO getPricingByLaps(Integer laps) {
        String url = "http://PRICING-SERVICE/api/pricing/laps/" + laps;
        return restTemplate.getForObject(url, PricingDTO.class);
    }

    private int getGroupDiscount(int numPersonas) {
        // Llama a group-discount-service
        return restTemplate.getForObject("http://group-discount-service/api/discount?groupSize=" + numPersonas, Integer.class);
    }

    private int getLoyaltyDiscount(List<BookingParticipantDTO> participantes) {
        // Llama a loyalty-service para cada participante y suma los descuentos
        return 0; // Implementa la lógica real
    }

    private int getSpecialDayDiscount(BookingRequestDTO request) {
        // Llama a special-day-service para verificar si aplica descuento especial
        return 0; // Implementa la lógica real
    }

    private List<BookingParticipant> calcularPreciosPorParticipante(
        List<BookingParticipantDTO> participantesDTO,
        PricingDTO pricing,
        int descuentoGrupo,
        int descuentoFrecuente,
        int descuentoEspecial
    ) {
        // Calcula el precio final para cada participante, aplicando los descuentos
        // Devuelve la lista de BookingParticipant
        return new ArrayList<>();
    }

    private List<String> assignKarts(LocalDateTime fechaUso, int numPersonas) {
        // Llama a track-service para asignar karts disponibles
        return new ArrayList<>();
    }

    private Invoice generarInvoice(Booking booking, List<BookingParticipant> participantes) {
        // Genera el comprobante de pago (Invoice) con los totales, IVA, etc.
        return new Invoice();
    }

    private BookingDTO mapToBookingDTO(Booking booking, List<BookingParticipant> participantes, Invoice invoice) {
        // Mapea las entidades a un DTO de respuesta
        return new BookingDTO();
    }

    private String generarCodigoReserva() {
        String alfanumerico = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "RES-" + alfanumerico;
    }
}