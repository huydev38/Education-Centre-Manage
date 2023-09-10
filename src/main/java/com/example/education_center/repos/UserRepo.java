package com.example.education_center.repos;

import com.example.education_center.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer> {
    @Query("select u from User u where u.username =:x")
    User findByUsername(@Param("x") String username);

    @Query("select u from User u where u.birthdate =:x")
    List<User> findByBirthdate(@Param("x") Date date);
}
