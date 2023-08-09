package com.example.education_center.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends TimeAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private double cost;

    private Date start_date;

    private Date end_date;

    private String description;

    private int size; //size cua 1 lop hoc

    private int remain; //so luong slot con lai cho phep dang ky


    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CourseSchedule> courseSchedules;
}
