package com.example.education_center.repos;

import com.example.education_center.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CourseRepo extends JpaRepository<Course,Integer> {

    @Query("select c from Course c where c.name like :x and c.status = :y")
    Page<Course> searchByName(@Param("x") String s, @Param("y") int status, Pageable pageable);

    @Query("select c from Course c where c.name like :x and c.status=:y and c.start_date between :s and :e ")
    Page<Course> searchByNameAndDate(@Param("x") String s, @Param("y") int status, @Param("s") Date start_date,@Param("e") Date end_date, Pageable pageable);

    @Query("select c from Course c where c.status=:y and c.start_date between :s and :e ")
    Page<Course> searchByDate(@Param("s") Date start_date, @Param("e") Date end_date, @Param("y") int status, Pageable pageable);

    @Query("select c from Course c WHERE c.status=1 and c.end_date <= :x")
    List<Course> searchByEndDateAndStatus(@Param("x") Date date);

    @Query("select c from Course c WHERE c.isOpen=1 and c.start_date >= :x")
    List<Course> searchByStartDateAndIsOpen(@Param("x") Date date);


}
