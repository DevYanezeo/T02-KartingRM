package com.tingeso.specialday_service.service;

import com.tingeso.specialday_service.dto.BirthdayDiscountDTO;
import com.tingeso.specialday_service.entity.SpecialPricing;
import com.tingeso.specialday_service.repository.SpecialPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.*;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
@Service
@AllArgsConstructor
public class SpecialPricingService {

    // Datos FAKE para probar sin base de datos
    public SpecialPricing findWeekendPricing(String dayName) {
        if (dayName.equals("SATURDAY")) {
            return new SpecialPricing(1L, SpecialPricing.PricingType.WEEKEND, "SATURDAY", null, 1.5, null, null, null);
        } else if (dayName.equals("SUNDAY")) {
            return new SpecialPricing(2L, SpecialPricing.PricingType.WEEKEND, "SUNDAY", null, 1.3, null, null, null);
        }
        return null;
    }

    public SpecialPricing findHolidayPricing(LocalDate date) {
        if (date.equals(LocalDate.of(2023, 12, 25))) { // Navidad
            return new SpecialPricing(3L, SpecialPricing.PricingType.HOLIDAY, "NAVIDAD", date, 2.0, null, null, null);
        }
        return null;
    }

    public SpecialPricing findBirthdayPricing(Integer groupSize) {
        if (groupSize >= 3 && groupSize <= 5) {
            return new SpecialPricing(4L, SpecialPricing.PricingType.BIRTHDAY, null, null, 0.5, 3, 5, 1);
        } else if (groupSize >= 6 && groupSize <= 10) {
            return new SpecialPricing(5L, SpecialPricing.PricingType.BIRTHDAY, null, null, 0.5, 6, 10, 2);
        }
        return null;
    }

    // Método para simular si un usuario cumple años (sin UserService)
    public boolean isUserBirthdayToday(Long userId) {
        return userId == 1; // Usuario 1 siempre cumple años (para pruebas)
    }
}