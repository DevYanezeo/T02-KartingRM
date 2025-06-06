package com.tingeso.kart_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Integer monthlyVisits = 0;

    // Método para determinar la categoría del cliente según visitas mensuales (PDF página 3)
    public String getClientCategory() {
        if (monthlyVisits >= 7) return "MUY_FRECUENTE";
        if (monthlyVisits >= 5) return "FRECUENTE";
        if (monthlyVisits >= 2) return "REGULAR";
        return "NO_FRECUENTE";
    }

    // Método para incrementar visitas mensuales
    public void incrementMonthlyVisits() {
        this.monthlyVisits += 1;
    }


    // Método para verificar si hoy es su cumpleaños (PDF página 3)
    public boolean isBirthdayToday() {
        LocalDate today = LocalDate.now();
        return birthDate.getMonth() == today.getMonth()
                && birthDate.getDayOfMonth() == today.getDayOfMonth();
    }
}
