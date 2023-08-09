package com.example.education_center.dto;

import lombok.Data;

@Data
public class CourseNotiDTO {
    private int id;

    private CourseDTO course;

    private String msg;
}
