package com.example.education_center.repos;

import com.example.education_center.entity.Learner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepo extends JpaRepository<Learner, Integer> {
}
