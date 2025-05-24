package com.tingeso.loyalty_service.controller;

import com.tingeso.loyalty_service.entity.LoyaltyDiscount;
import com.tingeso.loyalty_service.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @GetMapping("/discount")
    public ResponseEntity<Integer> calculateDiscount(@RequestParam Integer visits) {
        return ResponseEntity.ok(loyaltyService.getDiscountPercentage(visits));
    }

    @GetMapping("/tier")
    public ResponseEntity<LoyaltyDiscount> getTier(@RequestParam Integer visits) {
        LoyaltyDiscount tier = loyaltyService.getTierInfo(visits);
        return tier != null ? ResponseEntity.ok(tier) : ResponseEntity.notFound().build();
    }
}