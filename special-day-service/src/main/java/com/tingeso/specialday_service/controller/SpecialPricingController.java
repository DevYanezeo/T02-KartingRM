package com.tingeso.specialday_service.controller;

import com.tingeso.specialday_service.dto.BirthdayDiscountDTO;
import com.tingeso.specialday_service.dto.SpecialDayPricingDTO;
import com.tingeso.specialday_service.entity.SpecialPricing;
import com.tingeso.specialday_service.service.SpecialPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/special-pricing")
public class SpecialPricingController {

    private final SpecialPricingService pricingService;

    public SpecialPricingController(SpecialPricingService pricingService) {
        this.pricingService = pricingService;
    }

    // 1. Consultar tarifa para un día especial (fin de semana o feriado)
    @GetMapping("/check-day")
    public String checkSpecialDay(@RequestParam(required = false) LocalDate date) {
        if (date == null) date = LocalDate.now();

        // Verificar si es fin de semana
        if (date.getDayOfWeek().name().equals("SATURDAY") || date.getDayOfWeek().name().equals("SUNDAY")) {
            SpecialPricing pricing = pricingService.findWeekendPricing(date.getDayOfWeek().name());
            return "Hoy es " + pricing.getDayName() + ". Precio especial: " + pricing.getPriceMultiplier() + "x";
        }

        // Verificar si es feriado
        SpecialPricing holidayPricing = pricingService.findHolidayPricing(date);
        if (holidayPricing != null) {
            return "¡Hoy es " + holidayPricing.getDayName() + "! Precio especial: " + holidayPricing.getPriceMultiplier() + "x";
        }

        return "Hoy es un día normal. No hay descuentos.";
    }

    // 2. Consultar descuento por cumpleaños (con datos FAKE)
    @GetMapping("/birthday-discount")
    public String checkBirthdayDiscount(
            @RequestParam Long userId,
            @RequestParam Integer groupSize) {

        // Verificar si el usuario cumple años (FAKE: solo userId=1 cumple)
        if (!pricingService.isUserBirthdayToday(userId)) {
            return "Hoy no es tu cumpleaños. No hay descuento.";
        }

        // Verificar promoción según tamaño de grupo
        SpecialPricing birthdayPricing = pricingService.findBirthdayPricing(groupSize);
        if (birthdayPricing == null) {
            return "El tamaño del grupo no aplica para descuento (debe ser 3-10 personas).";
        }

        return " ¡Tienes descuento de cumpleaños! " +
                birthdayPricing.getMaxBirthdayPersons() + " persona(s) obtienen " +
                (birthdayPricing.getPriceMultiplier() * 100) + "% de descuento.";
    }
}