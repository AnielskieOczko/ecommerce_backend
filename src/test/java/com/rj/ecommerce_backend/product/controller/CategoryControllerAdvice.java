package com.rj.ecommerce_backend.product.controller;

import com.rj.ecommerce_backend.product.dtos.ErrorDTO;
import com.rj.ecommerce_backend.product.exceptions.CategoryAlreadyExistsException;
import com.rj.ecommerce_backend.product.exceptions.CategoryNotFoundException;
import com.rj.ecommerce_backend.product.exceptions.InvalidCategoryDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CategoryControllerAdvice {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(InvalidCategoryDataException.class)
    public ResponseEntity<ErrorDTO> handleInvalidCategoryDataException(InvalidCategoryDataException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }
}
