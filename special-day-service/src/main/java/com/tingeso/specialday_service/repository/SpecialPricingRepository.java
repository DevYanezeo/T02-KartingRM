package com.tingeso.specialday_service.repository;

import com.tingeso.specialday_service.entity.SpecialPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpecialPricingRepository extends JpaRepository<SpecialPricing, Long> {

    @Query("SELECT sp FROM SpecialPricing sp WHERE " +
            "sp.isActive = true AND (" +
            "(sp.type = 'WEEKEND' AND sp.dayName = :dayName) OR " +
            "(sp.type = 'HOLIDAY' AND (sp.specificDate = :date OR " +
            "(EXTRACT(MONTH FROM sp.specificDate) = EXTRACT(MONTH FROM :date) AND " +
            "EXTRACT(DAY FROM sp.specificDate) = EXTRACT(DAY FROM :date))))")
    List<SpecialPricing> findActivePricingForDate(
            @Param("date") LocalDate date,
            @Param("dayName") String dayName);

    @Query("SELECT sp FROM SpecialPricing sp WHERE " +
            "sp.isActive = true AND " +
            "sp.type = 'BIRTHDAY' AND " +
            ":groupSize BETWEEN sp.minGroupSize AND sp.maxGroupSize")
    SpecialPricing findBirthdayPromotion(@Param("groupSize") Integer groupSize);
}