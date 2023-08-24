package com.example.education_center.repos;

import com.example.education_center.entity.Course;
import com.example.education_center.entity.CourseNoti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseNotiRepo extends JpaRepository<CourseNoti, Integer> {
}
