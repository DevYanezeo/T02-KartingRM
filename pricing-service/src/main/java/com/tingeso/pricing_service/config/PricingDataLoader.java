package com.tingeso.pricing_service.config;

import com.tingeso.pricing_service.entity.Pricing;
import com.tingeso.pricing_service.repository.PricingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class PricingDataLoader implements CommandLineRunner {

    @Autowired
    private PricingRepository pricingRepository;

    @Override
    public void run(String... args) {
        if (pricingRepository.count() == 0) {
            pricingRepository.save(new Pricing(null, 10, 15000.0, 30));
            pricingRepository.save(new Pricing(null, 15, 20000.0, 35));
            pricingRepository.save(new Pricing(null, 20, 25000.0, 40));
            System.out.println("¡Tarifas base insertadas!");
        }
    }
}