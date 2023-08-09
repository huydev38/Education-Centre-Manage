package com.example.education_center.repos;

import com.example.education_center.entity.CourseScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseScoreRepo extends JpaRepository<CourseScore, Integer> {
}
