package com.tingeso.group_discount_service.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class GroupDiscountService {

    // Tabla de descuentos (hardcodeada según requisitos)
    private static final Map<String, Integer> DISCOUNT_TABLE = Map.of(
            "1-2", 0,
            "3-5", 10,
            "6-10", 20,
            "11-15", 30
    );

    // Obtiene el descuento para un tamaño de grupo
    public Integer getDiscountPercentage(Integer groupSize) {
        if (groupSize >= 1 && groupSize <= 2) return DISCOUNT_TABLE.get("1-2");
        if (groupSize >= 3 && groupSize <= 5) return DISCOUNT_TABLE.get("3-5");
        if (groupSize >= 6 && groupSize <= 10) return DISCOUNT_TABLE.get("6-10");
        if (groupSize >= 11 && groupSize <= 15) return DISCOUNT_TABLE.get("11-15");
        return 0; // Por defecto si no cumple rangos
    }

    // Calcula el monto con descuento (para integración con booking-service)
    public Double applyDiscount(Double baseAmount, Integer groupSize) {
        Integer discount = getDiscountPercentage(groupSize);
        return baseAmount * (1 - discount / 100.0);
    }
}