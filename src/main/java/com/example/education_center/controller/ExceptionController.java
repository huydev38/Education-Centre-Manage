package com.example.education_center.controller;

import com.example.education_center.exception.NotAuthenticateException;
import com.example.education_center.exception.NotAvailableException;
import com.example.education_center.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j

public class ExceptionController{
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> notFound(NotFoundException e){

        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NotAuthenticateException.class})
    public ResponseEntity<Object> notFound(NotAuthenticateException e){

        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({NotAvailableException.class})
    public ResponseEntity<Object> notFound(NotAvailableException e){

        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }

}
