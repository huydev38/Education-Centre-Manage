package com.example.education_center.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CourseScheduleDTO {
    private int id;

    @JsonIgnoreProperties("courseSchedules")
    private CourseDTO course;

    private TeacherDTO teacher;

    private int dow; //2 la thu 2, 1 la chu nhat

    @NotNull
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Date time_start;

    @NotNull
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Date time_end;

    private RoomDTO room;

}
