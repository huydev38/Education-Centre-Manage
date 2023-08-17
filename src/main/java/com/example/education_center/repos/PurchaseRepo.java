package com.example.education_center.repos;

import com.example.education_center.entity.Learner;
import com.example.education_center.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface PurchaseRepo extends JpaRepository<Purchase, Integer> {

    @Query("SELECT p from Purchase p where p.user.id=:x")
    Page<Purchase> searchByUser(@Param("x") int id, Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE p.createdAt between :x and :y")
    Page<Purchase> searchByDate(@Param("x") Date start_date, @Param("y") Date end_date, Pageable pageable);
    @Query("SELECT p FROM Purchase p WHERE p.createdAt <= :x")
    Page<Purchase> searchByEndDate(@Param("x") Date end_date, Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE p.createdAt >= :y")
    Page<Purchase> searchByStartDate(@Param("y") Date start_date, Pageable pageable);
}
