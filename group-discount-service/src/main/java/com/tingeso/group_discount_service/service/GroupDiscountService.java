package com.tingeso.group_discount_service.service;

import com.tingeso.group_discount_service.entity.GroupDiscount;
import com.tingeso.group_discount_service.repository.GroupDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class GroupDiscountService {
    private final GroupDiscountRepository repository;

    public Integer getDiscountPercentage(Integer groupSize) {
        return repository.findApplicableDiscounts(groupSize)
                .stream()
                .findFirst()
                .map(GroupDiscount::getPercentage)
                .orElse(0); // Default si no hay coincidencia
    }
}