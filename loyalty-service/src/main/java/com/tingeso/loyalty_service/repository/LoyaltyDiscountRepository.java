package com.tingeso.loyalty_service.repository;

import com.tingeso.loyalty_service.entity.LoyaltyDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyDiscountRepository extends JpaRepository<LoyaltyDiscount, Long> {

    @Query("SELECT ld FROM LoyaltyDiscount ld WHERE " +
            "(:visits BETWEEN ld.minVisits AND ld.maxVisits) OR " +
            "(ld.maxVisits IS NULL AND :visits >= ld.minVisits)")
    LoyaltyDiscount findTierByVisits(Integer visits);
}