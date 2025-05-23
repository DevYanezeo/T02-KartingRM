package com.tingeso.group_discount_service.controller;

import com.tingeso.group_discount_service.service.GroupDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group-discount")
@RequiredArgsConstructor
public class GroupDiscountController {
    private final GroupDiscountService service;

    @GetMapping("/{groupSize}")
    public Integer getDiscount(@PathVariable Integer groupSize) {
        return service.getDiscountPercentage(groupSize);
    }
}