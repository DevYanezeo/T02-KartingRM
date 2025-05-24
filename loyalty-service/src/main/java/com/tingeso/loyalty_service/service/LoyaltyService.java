package com.tingeso.loyalty_service.service;

import com.tingeso.loyalty_service.entity.LoyaltyDiscount;
import com.tingeso.loyalty_service.repository.LoyaltyDiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyService {

    @Autowired
    private LoyaltyDiscountRepository loyaltyRepo;

    public Integer getDiscountPercentage(Integer visits) {
        if (visits == null || visits < 0) return 0;

        LoyaltyDiscount tier = loyaltyRepo.findTierByVisits(visits);
        return tier != null ? tier.getDiscountPercentage() : 0;
    }

    public LoyaltyDiscount getTierInfo(Integer visits) {
        if (visits == null || visits < 0) return null;
        return loyaltyRepo.findTierByVisits(visits);
    }
}