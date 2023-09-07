package com.example.education_center.repos;

import com.example.education_center.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TeacherRepo extends JpaRepository<Teacher, Integer> {
    @Query("select t from Teacher t where t.user.phone=:x")
    Page<Teacher> searchByPhone(@Param("x") String phone, Pageable pageable);



    @Query("select t from Teacher t where t.name like :x")
    Page<Teacher> searchByName(@Param("x") String s, Pageable pageable);

    @Query("select t from Teacher t where t.user.email =:x")
    Page<Teacher> searchByMail(@Param("x") String email, Pageable pageRequest);
}
