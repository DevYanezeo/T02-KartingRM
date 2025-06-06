package com.tingeso.kart_service.repository;


import com.tingeso.kart_service.entity.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface KartRepository extends JpaRepository<Kart, String> {
    Optional<Kart> findByKartCode(String kartCode);
    boolean existsByKartCode(String kartCode);

    // Karts disponibles (no en mantenimiento y estado DISPONIBLE)
    @Query("SELECT k FROM Kart k WHERE k.underMaintenance = false")
    List<Kart> findAvailableKartsOnlyByMaintenance();

    // Contar karts disponibles
    @Query("SELECT COUNT(k) FROM Kart k WHERE k.underMaintenance = false")
    long countAvailableKartsOnlyByMaintenance();


    // Método adicional para compatibilidad (si lo necesitas)
    @Query(value = "SELECT * FROM Kart k WHERE k.under_maintenance = false", nativeQuery = true)
    List<Kart> findAvailableKartsBasic();

}