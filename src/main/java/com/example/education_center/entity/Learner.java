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

    private double tuition_fee;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private User user;

    @OneToMany(mappedBy = "learner")
    private List<CourseScore> courseScores;

    @ManyToMany(mappedBy = "learners")
    private List<Course> courses;
}
