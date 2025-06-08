package com.tingeso.group_discount_service.config;

import com.tingeso.group_discount_service.entity.GroupDiscount;
import com.tingeso.group_discount_service.repository.GroupDiscountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class GroupDiscountDataLoader implements CommandLineRunner {

    @Autowired
    private GroupDiscountRepository groupDiscountRepository;

    @Override
    public void run(String... args) {
        if (groupDiscountRepository.count() == 0) {
            groupDiscountRepository.save(new GroupDiscount(null, 1, 2, 0));
            groupDiscountRepository.save(new GroupDiscount(null, 3, 5, 10));
            groupDiscountRepository.save(new GroupDiscount(null, 6, 10, 20));
            groupDiscountRepository.save(new GroupDiscount(null, 11, 15, 30));
            System.out.println("¡Descuentos por grupo insertados!");
        }
    }
}