package com.tingeso.specialday_service.controller;

import com.tingeso.specialday_service.dto.BirthdayDiscountDTO;
import com.tingeso.specialday_service.dto.DateMultiplierDTO;
import com.tingeso.specialday_service.dto.SpecialPricingRequestDTO;
import com.tingeso.specialday_service.service.SpecialPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/special-pricing")
public class SpecialPricingController {

    @Autowired
    private SpecialPricingService pricingService;

    @GetMapping("/date-info")
    public ResponseEntity<DateMultiplierDTO> getDateMultiplierInfo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(pricingService.getDateMultiplierInfo(date));
    }

    @GetMapping("/birthday-discount")
    public ResponseEntity<BirthdayDiscountDTO> getBirthdayDiscount(
            @RequestParam Integer groupSize) {
        return ResponseEntity.ok(pricingService.calculateBirthdayDiscount(groupSize));
    }

    @PostMapping
    public ResponseEntity<Void> createSpecialPricing(
            @RequestBody SpecialPricingRequestDTO pricingDTO) {
        // Implementación para crear nueva configuración
        return ResponseEntity.ok().build();
    }
}