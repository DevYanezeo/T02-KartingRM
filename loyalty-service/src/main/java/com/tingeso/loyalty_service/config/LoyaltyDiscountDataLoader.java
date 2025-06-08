package com.tingeso.loyalty_service.config;

import com.tingeso.loyalty_service.entity.LoyaltyDiscount;
import com.tingeso.loyalty_service.repository.LoyaltyDiscountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class LoyaltyDiscountDataLoader implements CommandLineRunner {

    @Autowired
    private LoyaltyDiscountRepository loyaltyDiscountRepository;

    @Override
    public void run(String... args) {
        if (loyaltyDiscountRepository.count() == 0) {
            loyaltyDiscountRepository.save(new LoyaltyDiscount(null, "NO_FRECUENTE", 0, 1, 0));
            loyaltyDiscountRepository.save(new LoyaltyDiscount(null, "REGULAR", 2, 4, 10));
            loyaltyDiscountRepository.save(new LoyaltyDiscount(null, "FRECUENTE", 5, 6, 20));
            loyaltyDiscountRepository.save(new LoyaltyDiscount(null, "MUY_FRECUENTE", 7, null, 30));
            System.out.println("¡Descuentos de lealtad insertados!");
        }
    }
}