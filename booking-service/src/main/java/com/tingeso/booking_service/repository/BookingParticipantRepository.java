package com.tingeso.booking_service.repository;

import com.tingeso.booking_service.entity.Booking;
import com.tingeso.booking_service.entity.BookingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingParticipantRepository extends JpaRepository<BookingParticipant, Long> {
    List<BookingParticipant> findByBooking(Booking booking);
}