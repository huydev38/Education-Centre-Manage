package com.example.education_center.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Data;

@Data
public class CourseNotiDTO {
    private int id;

    @JsonIncludeProperties({"id","name"})
    private CourseDTO course;

    private String msg;
}
