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
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer groupSize) {
        SpecialPricingDTO dto = pricingService.getSpecialDayInfo(date, userId, groupSize);
        return ResponseEntity.ok(dto);
    }
}