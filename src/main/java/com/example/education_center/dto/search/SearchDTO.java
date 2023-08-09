package com.example.education_center.dto.search;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchDTO {
    @NotBlank
    private String keyword;

    private Integer currentPage;
    private Integer size;
    private String sortedField;
}
