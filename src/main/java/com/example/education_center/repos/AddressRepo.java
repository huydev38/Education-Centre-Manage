package com.example.education_center.repos;

import com.example.education_center.dto.CentreDTO;
import com.example.education_center.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepo extends JpaRepository<Address,Integer> {

    @Query("SELECT a FROM Address a where a.location like :x")
    Page<Address> searchByLocation(@Param("x") String s, Pageable pageable);


    @Query("SELECT a from Address a where a.centre.id = :x")
    Page<Address> searchByCentre(@Param("x") int id, Pageable pageable);

    @Query("SELECT a from Address a where a.centre.id = :x and a.location like :y")
    Page<Address> searchByCentreAndLocation(@Param("x") int id, @Param("y") String s, Pageable pageable);
}
