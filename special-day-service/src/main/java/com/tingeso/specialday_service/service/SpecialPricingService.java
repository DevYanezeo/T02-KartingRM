package com.tingeso.specialday_service.service;

import com.tingeso.specialday_service.dto.SpecialPricingDTO;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class SpecialPricingService {

    public SpecialPricingDTO getSpecialDayInfo(LocalDate date, Long userId, Integer groupSize) {
        SpecialPricingDTO dto = new SpecialPricingDTO();
        if (date == null) date = LocalDate.now();

        // Verificar si es fin de semana
        String dayOfWeek = date.getDayOfWeek().name();
        if (dayOfWeek.equals("SATURDAY")) {
            dto.setSpecialDay(true);
            dto.setDayType("WEEKEND");
            dto.setDayName("SATURDAY");
            dto.setPriceMultiplier(1.5);
            dto.setMessage("Hoy es sábado. Precio especial: 1.5x");
            return dto;
        } else if (dayOfWeek.equals("SUNDAY")) {
            dto.setSpecialDay(true);
            dto.setDayType("WEEKEND");
            dto.setDayName("SUNDAY");
            dto.setPriceMultiplier(1.3);
            dto.setMessage("Hoy es domingo. Precio especial: 1.3x");
            return dto;
        }

        // Verificar si es feriado (ejemplo: Navidad)
        if (date.equals(LocalDate.of(2023, 12, 25))) {
            dto.setSpecialDay(true);
            dto.setDayType("HOLIDAY");
            dto.setDayName("NAVIDAD");
            dto.setPriceMultiplier(2.0);
            dto.setMessage("¡Hoy es NAVIDAD! Precio especial: 2.0x");
            return dto;
        }

        // Verificar cumpleaños (solo si se pasan userId y groupSize)
        if (userId != null && groupSize != null && isUserBirthdayToday(userId)) {
            if (groupSize >= 3 && groupSize <= 5) {
                dto.setSpecialDay(true);
                dto.setDayType("BIRTHDAY");
                dto.setDayName(null);
                dto.setPriceMultiplier(0.5);
                dto.setMaxApplicablePersons(1);
                dto.setDiscountPercentage(50.0);
                dto.setMessage("¡Tienes descuento de cumpleaños! 1 persona obtiene 50% de descuento.");
                return dto;
            } else if (groupSize >= 6 && groupSize <= 10) {
                dto.setSpecialDay(true);
                dto.setDayType("BIRTHDAY");
                dto.setDayName(null);
                dto.setPriceMultiplier(0.5);
                dto.setMaxApplicablePersons(2);
                dto.setDiscountPercentage(50.0);
                dto.setMessage("¡Tienes descuento de cumpleaños! 2 personas obtienen 50% de descuento.");
                return dto;
            } else {
                dto.setSpecialDay(false);
                dto.setDayType("BIRTHDAY");
                dto.setMessage("El tamaño del grupo no aplica para descuento (debe ser 3-10 personas).");
                return dto;
            }
        }

        // Día normal
        dto.setSpecialDay(false);
        dto.setDayType("NORMAL");
        dto.setDayName(dayOfWeek);
        dto.setPriceMultiplier(1.0);
        dto.setMessage("Hoy es un día normal. No hay descuentos.");
        return dto;
    }

    // Método para simular si un usuario cumple años (sin UserService)
    public boolean isUserBirthdayToday(Long userId) {
        return userId == 1; // Usuario 1 siempre cumple años (para pruebas)
    }
}