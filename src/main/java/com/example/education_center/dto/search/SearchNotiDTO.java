package com.example.education_center.dto.search;

import com.example.education_center.dto.CourseDTO;
import com.example.education_center.dto.LearnerDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchNotiDTO extends SearchDTO{
    private Date start_date;
    private Date end_date;
    private CourseDTO courseDTO;
}
