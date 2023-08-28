package com.example.education_center.dto.search;

import com.example.education_center.dto.CourseDTO;
import com.example.education_center.dto.LearnerDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchScoreDTO extends SearchDTO{
    private LearnerDTO learnerDTO;
    private CourseDTO courseDTO;
    private Date start_date;
    private Date end_date;
}
