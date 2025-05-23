package com.tingeso.pricing_service.controller;

import com.tingeso.pricing_service.DTOs.PricingDTO;
import com.tingeso.pricing_service.entity.Pricing;
import com.tingeso.pricing_service.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {
    private final PricingService pricingService;

    @GetMapping("/test")
    public String test() {
        return "Servicio operativo";
    }

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping
    public ResponseEntity<List<PricingDTO>> getAllPricings() {
        List<Pricing> pricings = pricingService.getAllPricings();
        List<PricingDTO> dtos = pricings.stream()
                .map(p -> new PricingDTO(p.getId(), p.getLaps(), p.getBasePrice(), p.getTotalDuration()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Pricing> getPricingByLapsAndDuration(
            @RequestParam int laps,
            @RequestParam int duration) {
        return ResponseEntity.ok(pricingService.getPricingByLapsAndDuration(laps, duration));
    }

    @PostMapping
    public ResponseEntity<Pricing> savePricing(@RequestBody Pricing pricing) {
        return ResponseEntity.ok(pricingService.savePricing(pricing));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pricing> updatePricing(
            @PathVariable Long id,
            @RequestBody Pricing pricing) {
        return ResponseEntity.ok(pricingService.updatePricing(id, pricing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricing(@PathVariable Long id) {
        pricingService.deletePricing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pricing> getPricingById(@PathVariable Long id) {
        return ResponseEntity.ok(pricingService.getPricingById(id));
    }
}