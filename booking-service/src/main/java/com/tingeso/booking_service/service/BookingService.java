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
    private InvoiceRepository InvoiceRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public BookingDTO createBooking(BookingRequestDTO request) {
        // 1. Obtener tarifa y duración desde pricing-service
        PricingDTO pricing = getPricingByLaps(request.getNumVueltas());

        // 2. Calcular fecha, hora de inicio y fin, cantidad de karts
        LocalDate date = request.getFechaUso().toLocalDate();
        LocalTime startTime = request.getFechaUso().toLocalTime();
        int duration = pricing.getTotalDuration();
        LocalTime endTime = startTime.plusMinutes(duration);
        int quantity = request.getParticipantes().size();

        // 3. Consultar disponibilidad de karts en rack-service
        KartAvailabilityRequest availabilityRequest = new KartAvailabilityRequest();
        availabilityRequest.setDate(date);
        availabilityRequest.setStartTime(startTime);
        availabilityRequest.setEndTime(endTime);
        availabilityRequest.setQuantity(quantity);

        KartAvailabilityResponse availabilityResponse = checkKartAvailability(availabilityRequest);

        if (!availabilityResponse.isAvailable()) {
            throw new RuntimeException("No hay suficientes karts disponibles para la fecha y hora solicitadas.");
        }

        // 4. Crear Booking y participantes (aún sin karts asignados)
        Booking booking = new Booking();
        booking.setBookingCode(generarCodigoReserva());
        booking.setFechaReserva(LocalDateTime.now());
        booking.setStatus(Booking.Status.PENDIENTE);
        booking.setNumVueltas(request.getNumVueltas());

        List<BookingParticipant> participantes = new ArrayList<>();
        for (BookingParticipantDTO dto : request.getParticipantes()) {
            BookingParticipant participante = new BookingParticipant();
            participante.setNombre(dto.getNombre());
            participante.setEmail(dto.getEmail());
            participante.setFechaNacimiento(dto.getFechaNacimiento());
            participante.setBooking(booking);
            participantes.add(participante);
        }
        booking.setParticipantes(participantes);
        booking.setAssignedKarts(new ArrayList<>()); // Se llenará después

        booking = bookingRepository.save(booking);

        // 5. Solicitar asignación definitiva de karts a rack-service
        AssignKartsRequest assignRequest = new AssignKartsRequest();
        assignRequest.setDate(date);
        assignRequest.setStartTime(startTime);
        assignRequest.setEndTime(endTime);
        assignRequest.setQuantity(quantity);
        assignRequest.setBookingCode(booking.getBookingCode());

        // rack-service debe devolver la lista de karts asignados
        AssignKartsResponse assignResponse = restTemplate.postForObject(
            "http://RACK-SERVICE/api/rack/assign", assignRequest, AssignKartsResponse.class
        );

        if (assignResponse == null || assignResponse.getAssignedKarts() == null || assignResponse.getAssignedKarts().isEmpty()) {
            throw new RuntimeException("No se pudieron asignar karts a la reserva.");
        }

        // 6. Guardar los karts asignados en la reserva
        booking.setAssignedKarts(assignResponse.getAssignedKarts());
        booking = bookingRepository.save(booking);

        // 7. Mapear a BookingDTO para la respuesta
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setFechaReserva(booking.getFechaReserva());
        bookingDTO.setStatus(booking.getStatus().name());
        bookingDTO.setNumVueltas(booking.getNumVueltas());
        bookingDTO.setParticipantes(request.getParticipantes());
        bookingDTO.setKartsAsignados(booking.getAssignedKarts());

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

    public KartAvailabilityResponse checkKartAvailability(KartAvailabilityRequest request) {
        String url = "http://RACK-SERVICE/api/rack/available";
        return restTemplate.postForObject(url, request, KartAvailabilityResponse.class);
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