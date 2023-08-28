package com.example.education_center.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Learner extends TimeAuditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private double tuition_fee=0;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private User user;

    @OneToMany(mappedBy = "learner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseScore> courseScores;

    @ManyToMany(mappedBy = "learners", fetch = FetchType.EAGER)
    private List<Course> courses;
}
