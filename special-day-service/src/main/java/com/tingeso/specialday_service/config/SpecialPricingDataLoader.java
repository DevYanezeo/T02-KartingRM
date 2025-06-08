package com.tingeso.specialday_service.config;

import com.tingeso.specialday_service.entity.SpecialPricing;
import com.tingeso.specialday_service.repository.SpecialPricingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;

@Component
public class SpecialPricingDataLoader implements CommandLineRunner {

    @Autowired
    private SpecialPricingRepository specialPricingRepository;

    @Override
    public void run(String... args) {
        if (specialPricingRepository.count() == 0) {
            // Fines de semana: 20% más
            specialPricingRepository.save(new SpecialPricing(null, SpecialPricing.PricingType.WEEKEND, "SATURDAY", null, 1.2, null, null, null));
            specialPricingRepository.save(new SpecialPricing(null, SpecialPricing.PricingType.WEEKEND, "SUNDAY", null, 1.2, null, null, null));
            // Feriados: 30% más (ejemplo: Navidad)
            specialPricingRepository.save(new SpecialPricing(null, SpecialPricing.PricingType.HOLIDAY, "NAVIDAD", LocalDate.of(2025, 12, 25), 1.3, null, null, null));
            // Cumpleaños: 50% de descuento (multiplicador 0.5) para grupos
            specialPricingRepository.save(new SpecialPricing(null, SpecialPricing.PricingType.BIRTHDAY, null, null, 0.5, 3, 5, 1));
            specialPricingRepository.save(new SpecialPricing(null, SpecialPricing.PricingType.BIRTHDAY, null, null, 0.5, 6, 10, 2));
            System.out.println("¡Tarifas especiales insertadas!");
        }
    }
}