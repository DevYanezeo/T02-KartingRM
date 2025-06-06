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

// DTO para recibir la respuesta de special-day-service
class SpecialPricingDTO {
    private boolean isSpecialDay;
    private String dayType;
    private String dayName;
    private double priceMultiplier;
    private Integer maxApplicablePersons;
    private double discountPercentage;
    private String message;
    // Getters y setters
    public boolean isSpecialDay() { return isSpecialDay; }
    public void setSpecialDay(boolean specialDay) { isSpecialDay = specialDay; }
    public String getDayType() { return dayType; }
    public void setDayType(String dayType) { this.dayType = dayType; }
    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }
    public double getPriceMultiplier() { return priceMultiplier; }
    public void setPriceMultiplier(double priceMultiplier) { this.priceMultiplier = priceMultiplier; }
    public Integer getMaxApplicablePersons() { return maxApplicablePersons; }
    public void setMaxApplicablePersons(Integer maxApplicablePersons) { this.maxApplicablePersons = maxApplicablePersons; }
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

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

        // Obtener descuento de grupo según el tamaño del grupo
        int groupSize = request.getParticipantes().size();
        Integer descuentoGrupo = restTemplate.getForObject(
            "http://GROUP-DISCOUNT-SERVICE/group-discount/" + groupSize, Integer.class
        );
        System.out.println("Descuento de grupo para " + groupSize + " personas: " + descuentoGrupo + "%");

        // Llamar a special-day-service una vez para saber si es fin de semana o feriado
        SpecialPricingDTO specialDayInfo = getSpecialDayInfo(date, null, groupSize, null);
        boolean esCumple = false;
        int birthdayDiscountsLeft = 0;
        if (specialDayInfo.isSpecialDay() && "BIRTHDAY".equals(specialDayInfo.getDayType()) && specialDayInfo.getMaxApplicablePersons() != null) {
            birthdayDiscountsLeft = specialDayInfo.getMaxApplicablePersons();
        }

        List<BookingParticipant> participantes = new ArrayList<>();
        for (BookingParticipantDTO dto : request.getParticipantes()) {
            BookingParticipant participante = new BookingParticipant();
            participante.setNombre(dto.getNombre());
            participante.setEmail(dto.getEmail());
            participante.setFechaNacimiento(dto.getFechaNacimiento());
            participante.setBooking(booking);

            // 1. Contar visitas previas de este email
            int visitas = participantRepository.countByEmail(dto.getEmail());

            // 2. Llamar a loyalty-service para obtener el descuento
            Integer descuentoLoyalty = restTemplate.getForObject(
                "http://LOYALTY-SERVICE/loyalty/discount?visits=" + visitas, Integer.class
            );

            double precioBase = pricing.getBasePrice();
            double precioConEspecial = precioBase;

            // 3. Lógica para aplicar descuento especial
            // Si es cumpleaños, verifica si el participante cumple años hoy
            boolean aplicaCumple = false;
            SpecialPricingDTO birthdayInfo = getSpecialDayInfo(date, null, groupSize, dto.getEmail());
            if (birthdayInfo.isSpecialDay() && "BIRTHDAY".equals(birthdayInfo.getDayType()) && birthdayInfo.getDiscountPercentage() > 0 && birthdayDiscountsLeft > 0) {
                precioConEspecial = precioBase - (precioBase * birthdayInfo.getDiscountPercentage() / 100.0);
                birthdayDiscountsLeft--;
                aplicaCumple = true;
            } else if (specialDayInfo.isSpecialDay() && ("WEEKEND".equals(specialDayInfo.getDayType()) || "HOLIDAY".equals(specialDayInfo.getDayType()))) {
                precioConEspecial = precioBase * specialDayInfo.getPriceMultiplier();
            }

            // 4. Aplica los otros descuentos sobre el precio ajustado
            double precioConDescuentoGrupo = precioConEspecial - (precioConEspecial * descuentoGrupo / 100.0);
            double precioConDescuentoTotal = precioConDescuentoGrupo - (precioConDescuentoGrupo * descuentoLoyalty / 100.0);

            System.out.println("Participante: " + dto.getNombre() + " (" + dto.getEmail() + ")");
            System.out.println("Visitas: " + visitas);
            System.out.println("Descuento Loyalty: " + descuentoLoyalty + "%");
            System.out.println("Descuento Grupo: " + descuentoGrupo + "%");
            System.out.println("Precio base: " + precioBase);
            System.out.println("Precio con descuento especial: " + precioConEspecial);
            System.out.println("Precio con descuento de grupo: " + precioConDescuentoGrupo);
            System.out.println("Precio final con ambos descuentos: " + precioConDescuentoTotal);

            // participante.setPrecioFinal(precioConDescuentoTotal); // Si tienes este campo
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

    // Método para llamar a special-day-service
    private SpecialPricingDTO getSpecialDayInfo(LocalDate date, Long userId, Integer groupSize, String email) {
        String url = "http://SPECIAL-DAY-SERVICE/api/special-pricing/info?date=" + date;
        if (userId != null) url += "&userId=" + userId;
        if (groupSize != null) url += "&groupSize=" + groupSize;
        if (email != null) url += "&userId=" + email;
        return restTemplate.getForObject(url, SpecialPricingDTO.class);
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