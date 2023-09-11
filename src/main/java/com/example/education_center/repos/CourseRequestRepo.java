package com.example.education_center.repos;


import com.example.education_center.entity.CourseRequest;
import com.example.education_center.entity.Learner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface CourseRequestRepo extends JpaRepository<CourseRequest, Integer> {

    @Query("select r from CourseRequest r where r.learner.id = :x")
    List<CourseRequest> findByLearner(@Param("x") int learner);

    @Query("select r from CourseRequest r where r.course.id = :x")
    List<CourseRequest>  findByCourse(@Param("x") int course_id);
}
