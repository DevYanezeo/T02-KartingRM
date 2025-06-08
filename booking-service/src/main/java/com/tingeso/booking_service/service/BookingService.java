package com.tingeso.booking_service.service;

import com.tingeso.booking_service.dtos.*;
import com.tingeso.booking_service.entity.*;
import com.tingeso.booking_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private InvoiceService invoiceService;


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
        SpecialPricingDTO specialDayInfo = getSpecialDayInfo(date, null, groupSize, null, null);

        // Calcular cuántos cumplen años hoy
        int cumpleanerosHoy = 0;
        for (BookingParticipantDTO dto : request.getParticipantes()) {
            if (dto.getFechaNacimiento() != null &&
                dto.getFechaNacimiento().getDayOfMonth() == date.getDayOfMonth() &&
                dto.getFechaNacimiento().getMonth() == date.getMonth()) {
                cumpleanerosHoy++;
            }
        }
        int birthdayDiscountsLeft = 0;
        if (groupSize >= 3 && groupSize <= 5) {
            birthdayDiscountsLeft = Math.min(1, cumpleanerosHoy);
        } else if (groupSize >= 6 && groupSize <= 10) {
            birthdayDiscountsLeft = Math.min(2, cumpleanerosHoy);
        }

        List<BookingParticipant> participantes = new ArrayList<>();
        List<DetalleParticipanteDTO> detalleParticipantes = new ArrayList<>();
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
            double recargoEspecial = 0.0;
            double descuentoCumple = 0.0;
            double precioAntesDescuentos = precioBase;
            boolean aplicaCumple = false;

            // 3. Lógica para aplicar descuento especial
            boolean esCumple = dto.getFechaNacimiento() != null &&
                dto.getFechaNacimiento().getDayOfMonth() == date.getDayOfMonth() &&
                dto.getFechaNacimiento().getMonth() == date.getMonth();

            if (esCumple && birthdayDiscountsLeft > 0) {
                descuentoCumple = precioBase * 0.5;
                precioAntesDescuentos = precioBase - descuentoCumple;
                birthdayDiscountsLeft--;
                aplicaCumple = true;
            } else if (esCumple) {
                aplicaCumple = false;
            } else if (specialDayInfo.isSpecialDay() && ("WEEKEND".equals(specialDayInfo.getDayType()) || "HOLIDAY".equals(specialDayInfo.getDayType()))) {
                recargoEspecial = precioBase * (specialDayInfo.getPriceMultiplier() - 1.0);
                precioAntesDescuentos = precioBase + recargoEspecial;
            }

            double descuentoGrupoValor = precioAntesDescuentos * (descuentoGrupo / 100.0);
            double precioTrasGrupo = precioAntesDescuentos - descuentoGrupoValor;
            double descuentoLoyaltyValor = precioTrasGrupo * (descuentoLoyalty / 100.0);
            double precioFinal = precioTrasGrupo - descuentoLoyaltyValor;

            System.out.println("------------------------------");
            System.out.println("Participante: " + dto.getNombre() + " (" + dto.getEmail() + ")");
            System.out.println("Visitas: " + visitas);
            System.out.println("Precio base: " + precioBase);
            if (recargoEspecial > 0.0) {
                System.out.println("Recargo por día especial: +" + recargoEspecial);
            }
            if (aplicaCumple) {
                System.out.println("Descuento cumpleaños: -" + descuentoCumple + " (¡APLICADO!)");
            } else if (esCumple) {
                System.out.println("Hoy es su cumpleaños, pero ya se aplicó el máximo de descuentos de cumpleaños para el grupo.");
            } else {
                System.out.println("No es su cumpleaños.");
            }
            System.out.println("Precio antes de descuentos de grupo y loyalty: " + precioAntesDescuentos);
            System.out.println("Descuento de grupo: -" + descuentoGrupoValor);
            System.out.println("Descuento loyalty: -" + descuentoLoyaltyValor);
            System.out.println("Precio final: " + precioFinal);
            System.out.println("------------------------------");

            // Guardar detalle para el comprobante
            DetalleParticipanteDTO detalle = new DetalleParticipanteDTO();
            detalle.setNombre(dto.getNombre());
            detalle.setTarifaBase(precioBase);
            detalle.setRecargoEspecial(recargoEspecial);
            detalle.setDescuentoGrupo(descuentoGrupoValor);
            detalle.setDescuentoLoyalty(descuentoLoyaltyValor);
            detalle.setDescuentoCumple(descuentoCumple);
            detalle.setMontoFinalSinIVA(precioAntesDescuentos - descuentoGrupoValor - descuentoLoyaltyValor);
            double iva = (precioTrasGrupo - descuentoLoyaltyValor) * 0.19;
            detalle.setIva(iva);
            detalle.setMontoFinalConIVA((precioTrasGrupo - descuentoLoyaltyValor) + iva);
            detalleParticipantes.add(detalle);

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

        // Calcular totales para el comprobante
        int montoTotalSinIVA = 0;
        int montoTotalConIVA = 0;
        int ivaTotal = 0;
        for (DetalleParticipanteDTO d : detalleParticipantes) {
            montoTotalSinIVA += (int) Math.round(d.getMontoFinalSinIVA());
            ivaTotal += (int) Math.round(d.getIva());
            montoTotalConIVA += (int) Math.round(d.getMontoFinalConIVA());
        }
        String nombreResponsable = request.getParticipantes().get(0).getNombre();
        String detalleParticipantesJson = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            detalleParticipantesJson = mapper.writeValueAsString(detalleParticipantes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear y guardar el Invoice
        Invoice invoice = invoiceService.generarInvoice(booking, montoTotalSinIVA, ivaTotal, montoTotalConIVA, nombreResponsable, detalleParticipantesJson);
        // Generar el PDF y actualizar el campo pdfUrl
        String pdfPath = invoiceService.generarPdfInvoice(invoice, detalleParticipantes);
        invoice.setPdfUrl(pdfPath);
        // Guardar el Invoice actualizado (solo update, no crear uno nuevo)
        invoice = invoiceService.getInvoiceRepository().save(invoice);
        booking.setInvoice(invoice);
        booking = bookingRepository.save(booking);

        // 7. Mapear a BookingDTO para la respuesta
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setFechaReserva(booking.getFechaReserva());
        bookingDTO.setStatus(booking.getStatus().name());
        bookingDTO.setNumVueltas(booking.getNumVueltas());
        bookingDTO.setParticipantes(request.getParticipantes());
        bookingDTO.setKartsAsignados(booking.getAssignedKarts());
        // Mapear Invoice a DTO
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceCode(invoice.getInvoiceCode());
        invoiceDTO.setFechaEmision(invoice.getFechaEmision());
        invoiceDTO.setMontoTotalSinIVA(invoice.getMontoTotalSinIVA());
        invoiceDTO.setIvaTotal(invoice.getIvaTotal());
        invoiceDTO.setMontoTotalConIVA(invoice.getMontoTotalConIVA());
        invoiceDTO.setPdfUrl(invoice.getPdfUrl());
        invoiceDTO.setNombreResponsable(invoice.getNombreResponsable());
        invoiceDTO.setDetalleParticipantes(detalleParticipantes);
        bookingDTO.setInvoice(invoiceDTO);

        return bookingDTO;
    }

    // Método para llamar a special-day-service
    private SpecialPricingDTO getSpecialDayInfo(LocalDate date, Long userId, Integer groupSize, String email, LocalDate birthDate) {
        String url = "http://SPECIALDAY-SERVICE/api/special-pricing/info?date=" + date;
        if (userId != null) url += "&userId=" + userId;
        if (groupSize != null) url += "&groupSize=" + groupSize;
        if (email != null) url += "&userId=" + email;
        if (birthDate != null) url += "&birthDate=" + birthDate;
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

    // En BookingService o una clase utilitaria
    public InvoiceDTO mapToInvoiceDTO(Invoice inv) {
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
            inv.getId(),
            inv.getInvoiceCode(),
            inv.getFechaEmision(),
            inv.getMontoTotalSinIVA(),
            inv.getIvaTotal(),
            inv.getMontoTotalConIVA(),
            inv.getPdfUrl(),
            inv.getNombreResponsable(),
            detalle
        );
    }

    public List<BookingDTO> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingDTO> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingDTO dto = new BookingDTO();
            dto.setId(booking.getId());
            dto.setFechaReserva(booking.getFechaReserva());
            dto.setStatus(booking.getStatus().name());
            dto.setNumVueltas(booking.getNumVueltas());
            // Mapea participantes a BookingParticipantDTO
            List<BookingParticipantDTO> participantesDTO = booking.getParticipantes().stream()
                    .map(p -> new BookingParticipantDTO(p.getNombre(), p.getEmail(), p.getFechaNacimiento()))
                    .collect(Collectors.toList());
            dto.setParticipantes(participantesDTO);
            // Mapea karts asignados
            dto.setKartsAsignados(booking.getAssignedKarts());
            // Mapea la factura asociada
            invoiceRepository.findByBookingId(booking.getId()).ifPresent(invoice -> {
                dto.setInvoice(mapToInvoiceDTO(invoice));
            });
            // Otros campos si los necesitas
            dtos.add(dto);
        }
        return dtos;
    }
}