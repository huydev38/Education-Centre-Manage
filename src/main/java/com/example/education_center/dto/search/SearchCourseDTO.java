package com.example.education_center.dto.search;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchCourseDTO extends SearchDTO{
    //keyword la search ten
    private Date start_date;
    private Date end_date;
    @NotBlank
    private int status;

}

//khi search yeu cau chon status