package com.tingeso.specialday_service.repository;

import com.tingeso.specialday_service.entity.SpecialPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialPricingRepository extends JpaRepository<SpecialPricing, Long> {

    @Query("SELECT sp FROM SpecialPricing sp WHERE " +
            "sp.type = 'WEEKEND' AND sp.dayName = :dayName")
    Optional<SpecialPricing> findWeekendPricing(@Param("dayName") String dayName);

    @Query("SELECT sp FROM SpecialPricing sp WHERE " +
            "sp.type = 'HOLIDAY' AND " +
            "(sp.dayName = :holidayName OR sp.specificDate = :date)")
    Optional<SpecialPricing> findHolidayPricing(
            @Param("holidayName") String holidayName,
            @Param("date") LocalDate date);

    @Query("SELECT sp FROM SpecialPricing sp WHERE " +
            "sp.type = 'BIRTHDAY' AND " +
            ":groupSize BETWEEN sp.minGroupSize AND sp.maxGroupSize")
    Optional<SpecialPricing> findBirthdayPricing(@Param("groupSize") Integer groupSize);
}