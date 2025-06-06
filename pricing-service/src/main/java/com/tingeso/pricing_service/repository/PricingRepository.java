package com.tingeso.pricing_service.repository;

import com.tingeso.pricing_service.entity.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Optional<Pricing> findByLapsAndTotalDuration(Integer laps, Integer totalDuration);

    @Query("SELECT p.totalDuration FROM Pricing p WHERE p.laps = :laps")
    Optional<Integer> findDurationByLaps(@Param("laps") int laps);

    Optional<Pricing> findByLaps(int laps);
}