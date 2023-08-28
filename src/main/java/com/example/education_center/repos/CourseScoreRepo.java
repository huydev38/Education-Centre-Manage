package com.example.education_center.repos;

import com.example.education_center.entity.CourseScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface CourseScoreRepo extends JpaRepository<CourseScore, Integer> {

    @Query("select s from CourseScore s where s.course.id =:x")
    Page<CourseScore> searchByCourse(@Param("x") int id, Pageable pageable);

    @Query("select s from CourseScore s where s.course.id =:x and s.createdAt between :s and :e")
    Page<CourseScore> searchByCourseAndDate(@Param("x") int id, @Param("s") Date start_date, @Param("e") Date end_date, Pageable pageable);

    @Query("select s from CourseScore s where s.learner.id =:x and s.createdAt between :s and :e")
    Page<CourseScore> searchByLearnerAndDate(@Param("x") int id,@Param("s") Date start_date, @Param("e") Date end_date, Pageable pageable);

    @Query("select s from CourseScore s where s.learner.id =:x")
    Page<CourseScore> searchByLearner(@Param("x") int id, Pageable pageable);
}
