package com.example.education_center.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Data
@Entity
public class CourseSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Course course;

    @ManyToOne
    private Teacher teacher;

    private boolean status; //0 het hieu luc, 1 con hieu luc

    private int dow; //2 la thu 2, 1 la chu nhat

    @Temporal(TemporalType.TIME)
    @NotNull
    private Date time_start;

    @Temporal(TemporalType.TIME)
    @NotNull
    private Date time_end;

    @ManyToOne
    private Room room;

}
