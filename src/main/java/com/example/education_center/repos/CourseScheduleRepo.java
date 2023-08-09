package com.example.education_center.repos;

import com.example.education_center.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseScheduleRepo extends JpaRepository<CourseSchedule,Integer> {
}
