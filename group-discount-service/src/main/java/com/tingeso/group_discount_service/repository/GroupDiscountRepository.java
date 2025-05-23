package com.tingeso.group_discount_service.repository;

import com.tingeso.group_discount_service.entity.GroupDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GroupDiscountRepository extends JpaRepository<GroupDiscount, Long> {

    @Query("SELECT d FROM GroupDiscount d WHERE :groupSize BETWEEN d.minPeople AND d.maxPeople")
    List<GroupDiscount> findApplicableDiscounts(@Param("groupSize") Integer groupSize);
}