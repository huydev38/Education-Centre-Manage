package com.example.education_center.controller;

import com.example.education_center.dto.ResponseDTO;
import com.example.education_center.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j

public class ExceptionController{
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseDTO<String> notFound(NotFoundException e){

        return ResponseDTO.<String>builder().status(404).msg("No data").build();
    }
}
