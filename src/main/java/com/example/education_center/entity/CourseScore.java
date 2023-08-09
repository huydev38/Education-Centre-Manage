package com.example.education_center.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseScore extends TimeAuditable{
    @Id
    private int id;

    @ManyToOne
    private Course course;

    @ManyToOne
    private Learner learner;

    private double grade;

}
