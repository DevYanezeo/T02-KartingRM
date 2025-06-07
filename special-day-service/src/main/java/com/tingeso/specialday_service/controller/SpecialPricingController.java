package com.tingeso.specialday_service.controller;

import com.tingeso.specialday_service.dto.SpecialPricingDTO;
import com.tingeso.specialday_service.service.SpecialPricingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/special-pricing")
public class SpecialPricingController {

    private final SpecialPricingService pricingService;

    public SpecialPricingController(SpecialPricingService pricingService) {
        this.pricingService = pricingService;
    }

    // Endpoint unificado para obtener info de día especial, feriado o cumpleaños
    @GetMapping("/info")
    public ResponseEntity<SpecialPricingDTO> getSpecialDayInfo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Integer groupSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate) {
        SpecialPricingDTO dto = pricingService.getSpecialDayInfo(date, userId, groupSize, birthDate);
        return ResponseEntity.ok(dto);
    }
}