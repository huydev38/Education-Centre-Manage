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

    private int status; //0 da ket thuc, 1 dang dien ra,


    private int isOpen; //1 cho phep dang ky, 0 khong cho phep dang ky


    private double cost;

    @Temporal(TemporalType.DATE)
    private Date start_date;

    @Temporal(TemporalType.DATE)
    private Date end_date;

    private String description;

    private int size; //size cua 1 lop hoc

    private int remain; //so luong slot con lai cho phep dang ky


    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CourseSchedule> courseSchedules;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="course_register",
            joinColumns = @JoinColumn(name="course_id"),
            inverseJoinColumns = @JoinColumn(name="learner_id"))
    private List<Learner> learners;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<CourseNoti> courseNotis;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<CourseScore> courseScores;
}
