package com.tingeso.rack_service.controller;

import com.tingeso.rack_service.entity.Rack;
import com.tingeso.rack_service.service.RackService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.tingeso.rack_service.dto.KartAvailabilityRequest;
import com.tingeso.rack_service.dto.KartAvailabilityResponse;
import com.tingeso.rack_service.dto.KartDTO;
import com.tingeso.rack_service.dto.AssignKartsRequest;
import com.tingeso.rack_service.dto.AssignKartsResponse;

@RestController
@RequestMapping("/api/rack")
@RequiredArgsConstructor
public class RackController {
    private final RackService rackService;

    @GetMapping("/occupied")
    public ResponseEntity<List<Rack>> getOverlappingBlocks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return ResponseEntity.ok(rackService.getOverlappingBlocks(date, startTime, endTime));
    }

    @PostMapping("/assign")
    public ResponseEntity<AssignKartsResponse> assignKarts(@RequestBody AssignKartsRequest request) {
        // 1. Obtener karts activos
        List<KartDTO> activeKarts = rackService.getAvailableKarts();
    
        // 2. Buscar bloques ocupados en ese horario
        List<Rack> overlappingBlocks = rackService.getOverlappingBlocks(
            request.getDate(), request.getStartTime(), request.getEndTime()
        );
        List<String> occupiedKarts = overlappingBlocks.stream()
            .flatMap(r -> r.getKartsAsignados().stream())
            .toList();
    
        // 3. Filtrar karts disponibles
        List<String> availableKarts = activeKarts.stream()
            .map(KartDTO::getKartCode)
            .filter(k -> !occupiedKarts.contains(k))
            .toList();
    
        AssignKartsResponse response = new AssignKartsResponse();
    
        if (availableKarts.size() >= request.getQuantity()) {
            // 4. Asignar los primeros N karts disponibles
            List<String> assignedKarts = availableKarts.subList(0, request.getQuantity());
    
            // 5. Crear y guardar el bloque Rack
            Rack rack = new Rack();
            rack.setFecha(request.getDate());
            rack.setHoraInicio(request.getStartTime());
            rack.setHoraFin(request.getEndTime());
            rack.setKartsAsignados(assignedKarts);
            rack.setBookingCode(request.getBookingCode());
            rackService.saveRackBlock(rack);
    
            // 6. Devolver la lista de karts asignados
            response.setAssignedKarts(assignedKarts);
        } else {
            response.setAssignedKarts(List.of());
        }
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<Rack>> getRackByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(rackService.getRackByDate(date));
    }

    @PostMapping("/available")
    public ResponseEntity<KartAvailabilityResponse> checkAvailability(@RequestBody KartAvailabilityRequest request) {
        // 1. Consulta a kart-service los karts activos
        List<KartDTO> activeKarts = rackService.getAvailableKarts();

        // 2. Filtra los ocupados en ese bloque horario usando la entidad Rack
        List<Rack> overlappingBlocks = rackService.getOverlappingBlocks(
                request.getDate(), request.getStartTime(), request.getEndTime());
        // Recolecta todos los karts ocupados en ese bloque
        List<String> occupiedKarts = overlappingBlocks.stream()
                .flatMap(r -> r.getKartsAsignados().stream())
                .toList();

        // Filtra los karts disponibles (activos y no ocupados)
        List<String> availableKarts = activeKarts.stream()
                .map(KartDTO::getKartCode)
                .filter(k -> !occupiedKarts.contains(k))
                .toList();

        KartAvailabilityResponse response = new KartAvailabilityResponse();
        if (availableKarts.size() >= request.getQuantity()) {
            response.setAvailable(true);
            response.setAvailableKarts(availableKarts.subList(0, request.getQuantity()));
            response.setMessage("Karts disponibles para la reserva.");
        } else {
            response.setAvailable(false);
            response.setAvailableKarts(availableKarts);
            response.setMessage("No hay suficientes karts disponibles para la fecha y hora solicitadas.");
        }
        return ResponseEntity.ok(response);
    }
}
