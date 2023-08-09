package com.example.education_center.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO<T> {
    private int status; //200, 400, 500
    private String msg; //

    //nếu = null thì không trả về
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

}
