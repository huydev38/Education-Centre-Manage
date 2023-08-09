package com.example.education_center.dto.search;

import com.example.education_center.dto.CentreDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchAddressDTO extends SearchDTO{
    private CentreDTO centreDTO;
}
