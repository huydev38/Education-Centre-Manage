package com.example.education_center.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class CourseDTO {
    private int id;

    @NotBlank
    private String name;

    private int status;

    private AddressDTO address;

    private double cost;

    private Date start_date;

    private Date end_date;

    private String description;

    @JsonIgnoreProperties("course")
    private List<CourseScheduleDTO> courseSchedules;

    private int size;

    private int remain;

    @JsonIgnoreProperties("courses")
    private List<LearnerDTO> learners;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date createdDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date updatedAt;
}
