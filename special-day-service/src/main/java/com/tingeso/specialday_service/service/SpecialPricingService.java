package com.tingeso.specialday_service.service;

import com.tingeso.specialday_service.dto.BirthdayDiscountDTO;
import com.tingeso.specialday_service.dto.DateMultiplierDTO;
import com.tingeso.specialday_service.entity.SpecialPricing;
import com.tingeso.specialday_service.repository.SpecialPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class SpecialPricingService {

    @Autowired
    private SpecialPricingRepository pricingRepository;

    public DateMultiplierDTO getDateMultiplierInfo(LocalDate date) {
        String dayName = date.getDayOfWeek().name();
        List<SpecialPricing> pricingList = pricingRepository.findActivePricingForDate(date, dayName);

        if (!pricingList.isEmpty()) {
            SpecialPricing pricing = pricingList.get(0);
            return new DateMultiplierDTO(
                    pricing.getType(),
                    pricing.getDayName() != null ? pricing.getDayName() : "REGULAR",
                    pricing.getPriceMultiplier()
            );
        }
        return new DateMultiplierDTO("REGULAR", "NORMAL_DAY", 1.0);
    }

    public BirthdayDiscountDTO calculateBirthdayDiscount(Integer groupSize) {
        SpecialPricing promo = pricingRepository.findBirthdayPromotion(groupSize);

        if (promo != null && promo.getBirthdayDiscount() != null) {
            return new BirthdayDiscountDTO(
                    promo.getMaxBirthdayPersons(),
                    promo.getBirthdayDiscount() * 100 // Convertir a porcentaje
            );
        }
        return new BirthdayDiscountDTO(0, 0.0);
    }

    public Double getDateMultiplier(LocalDate date) {
        return getDateMultiplierInfo(date).getMultiplier();
    }
}