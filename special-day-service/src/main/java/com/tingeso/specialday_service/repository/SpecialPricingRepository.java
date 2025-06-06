package com.tingeso.specialday_service.repository;

import com.tingeso.specialday_service.entity.SpecialPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialPricingRepository extends JpaRepository<SpecialPricing, Long> {
}