package com.tingeso.group_discount_service.repository;

import com.tingeso.group_discount_service.entity.GroupDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GroupDiscountRepository extends JpaRepository<GroupDiscount, Long> {
    @Query("SELECT g FROM GroupDiscount g WHERE :groupSize BETWEEN g.minPeople AND g.maxPeople")
    GroupDiscount findApplicableDiscount(@Param("groupSize") Integer groupSize);
}