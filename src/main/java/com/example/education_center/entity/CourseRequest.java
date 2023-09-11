package com.example.education_center.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseRequest extends TimeAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Learner learner;

    @ManyToOne
    private Course course;

    private int status;
    //0 deny
    //1 accept
    //2 pending

}
