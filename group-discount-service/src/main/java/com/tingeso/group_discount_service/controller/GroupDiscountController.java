package com.tingeso.group_discount_service.controller;

import com.tingeso.group_discount_service.service.GroupDiscountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group-discount")
public class GroupDiscountController {
    private final GroupDiscountService service;

    public GroupDiscountController(GroupDiscountService service) {
        this.service = service;
    }

    // Endpoint para booking-service
    @GetMapping("/{groupSize}")
    public Integer getDiscount(@PathVariable Integer groupSize) {
        return service.getDiscountPercentage(groupSize);
    }

    // Endpoint alternativo para cálculos directos
    @GetMapping("/calculate")
    public Double calculateFinalPrice(
            @RequestParam Double baseAmount,
            @RequestParam Integer groupSize) {
        return service.applyDiscount(baseAmount, groupSize);
    }
}