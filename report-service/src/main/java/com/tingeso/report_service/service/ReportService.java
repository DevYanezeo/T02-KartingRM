package com.tingeso.report_service.service;

import com.tingeso.report_service.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final RestTemplate restTemplate;

    @Value("${booking-service.url:http://BOOKING-SERVICE/api/bookings}")
    private String bookingServiceUrl;

    // Rangos fijos para RF9
    private static final List<int[]> RANGOS_PERSONAS = List.of(
            new int[]{1, 3},
            new int[]{4, 6},
            new int[]{7, 9}
    );
    private static final List<String> NOMBRES_RANGOS = List.of("1-3", "4-6", "7-9");

    public List<IngresosPorVueltasDTO> obtenerIngresosPorVueltas(LocalDateTime desde, LocalDateTime hasta) {
        BookingDTO[] bookings = restTemplate.getForObject(bookingServiceUrl, BookingDTO[].class);
        if (bookings == null) return Collections.emptyList();

        // Determinar todos los tipos de vueltas presentes
        Set<Integer> tiposVueltas = Arrays.stream(bookings)
                .map(BookingDTO::getNumVueltas)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Determinar todos los meses del rango
        List<String> meses = obtenerMesesEnRango(desde, hasta);

        List<IngresosPorVueltasDTO> resultado = new ArrayList<>();
        for (Integer tipo : tiposVueltas) {
            Map<String, BigDecimal> ingresosPorMes = new LinkedHashMap<>();
            for (String mes : meses) {
                BigDecimal total = Arrays.stream(bookings)
                        .filter(b -> b.getNumVueltas() != null && b.getNumVueltas().equals(tipo))
                        .filter(b -> b.getInvoice() != null && b.getInvoice().getFechaEmision() != null)
                        .filter(b -> mes.equals(formatearMes(b.getInvoice().getFechaEmision())))
                        .map(b -> BigDecimal.valueOf(Optional.ofNullable(b.getInvoice().getMontoTotalConIVA()).orElse(0)))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                ingresosPorMes.put(mes, total);
            }
            resultado.add(new IngresosPorVueltasDTO(tipo + " vueltas", ingresosPorMes));
        }
        return resultado;
    }

    public List<IngresosPorPersonasDTO> obtenerIngresosPorPersonas(LocalDateTime desde, LocalDateTime hasta) {
        BookingDTO[] bookings = restTemplate.getForObject(bookingServiceUrl, BookingDTO[].class);
        if (bookings == null) return Collections.emptyList();

        List<String> meses = obtenerMesesEnRango(desde, hasta);
        List<IngresosPorPersonasDTO> resultado = new ArrayList<>();
        for (int i = 0; i < RANGOS_PERSONAS.size(); i++) {
            int[] rango = RANGOS_PERSONAS.get(i);
            String nombreRango = NOMBRES_RANGOS.get(i);
            Map<String, BigDecimal> ingresosPorMes = new LinkedHashMap<>();
            for (String mes : meses) {
                BigDecimal total = Arrays.stream(bookings)
                        .filter(b -> b.getParticipantes() != null)
                        .filter(b -> {
                            int n = b.getParticipantes().size();
                            return n >= rango[0] && n <= rango[1];
                        })
                        .filter(b -> b.getInvoice() != null && b.getInvoice().getFechaEmision() != null)
                        .filter(b -> mes.equals(formatearMes(b.getInvoice().getFechaEmision())))
                        .map(b -> BigDecimal.valueOf(Optional.ofNullable(b.getInvoice().getMontoTotalConIVA()).orElse(0)))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                ingresosPorMes.put(mes, total);
            }
            resultado.add(new IngresosPorPersonasDTO(nombreRango, ingresosPorMes));
        }
        return resultado;
    }

    private List<String> obtenerMesesEnRango(LocalDateTime desde, LocalDateTime hasta) {
        List<String> meses = new ArrayList<>();
        LocalDateTime actual = desde.withDayOfMonth(1);
        while (!actual.isAfter(hasta)) {
            meses.add(formatearMes(actual));
            actual = actual.plusMonths(1);
        }
        return meses;
    }

    private String formatearMes(LocalDateTime fecha) {
        return fecha.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
} 