package com.tingeso.booking_service.repository;

import com.tingeso.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByReservationCode(String reservationCode);
    List<Booking> findByDate(LocalDate date);
    boolean existsByReservationCode(String reservationCode);
}