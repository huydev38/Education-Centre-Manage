package com.example.education_center.repos;

import com.example.education_center.entity.Learner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LearnerRepo extends JpaRepository<Learner, Integer> {

    @Query("select l from Learner l where l.name like :x")
    Page<Learner> searchByName(@Param("x") String keyword, Pageable pageable);

    @Query("select l from Learner l where l.user.phone = :x")
    Page<Learner> searchByPhone(@Param("x") String phone, Pageable pageable);

    @Query("select l from Learner l where l.user.email = :x")
    Page<Learner> seachByEmail(@Param("x") String email, Pageable pageable);

    @Query("select l FROM Learner l where l.user.id =:x")
    Learner findByUserId(@Param("x") int id);
}
