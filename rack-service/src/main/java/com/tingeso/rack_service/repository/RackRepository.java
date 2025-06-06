package com.tingeso.rack_service.repository;

import com.tingeso.rack_service.entity.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {
    List<Rack> findByFecha(LocalDate fecha);

    @Query("SELECT r FROM Rack r WHERE r.fecha = :date AND r.horaInicio < :endTime AND r.horaFin > :startTime")
    List<Rack> findOverlappingBlocks(@Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);
}
