package com.example.education_center.dto.search;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchTeacherDTO extends SearchDTO {
    public String phone;
    public String email;
}
