package com.example.education_center.repos;

import com.example.education_center.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepo extends JpaRepository<Room, Integer> {

    @Query("SELECT r FROM Room r WHERE r.address.location like :x")
    Page<Room> searchByLocation(@Param("x") String s, Pageable pageable);

    @Query("select r from Room r where r.address.id = :x")
    Page<Room> searchByAddress(@Param("x") int id, Pageable pageable);

    @Query("select r from Room r where r.address.id=:x and r.address.location like :y")
    Page<Room> searchByCentreAndLocation(@Param("x") int id, @Param("y") String s, Pageable pageable);
}
