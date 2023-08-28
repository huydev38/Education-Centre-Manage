package com.example.education_center.repos;

import com.example.education_center.entity.Course;
import com.example.education_center.entity.CourseNoti;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface CourseNotiRepo extends JpaRepository<CourseNoti, Integer> {

    @Query("select n from CourseNoti n where n.course.id = :x")
    Page<CourseNoti> searchByCourse(@Param("x") int id, Pageable pageable);


    @Query("select n from CourseNoti n where n.course.id = :x and n.createdAt between :s and :e")
    Page<CourseNoti> searchByCourseAndDate(@Param("x") int id, @Param("s") Date start_date, @Param("e") Date end_date, Pageable pageable);
}
