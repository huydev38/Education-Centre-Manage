package com.example.education_center.dto.search;

import com.example.education_center.dto.CentreDTO;
import lombok.Data;

@Data
public class SearchRoomDTO extends SearchDTO{
    private CentreDTO centreDTO;
}
