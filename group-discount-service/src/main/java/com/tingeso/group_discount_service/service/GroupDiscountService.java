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
        GroupDiscount discount = repository.findApplicableDiscount(groupSize);
        return discount != null ? discount.getPercentage() : 0;
    }

}