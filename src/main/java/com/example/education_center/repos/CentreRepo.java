package com.example.education_center.repos;

import com.example.education_center.entity.Centre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CentreRepo extends JpaRepository<Centre, Integer> {

    @Query("SELECT c FROM Centre c WHERE c.name like :x")
    Page<Centre> searchByName(@Param("x") String s, Pageable pageable);
}
