package com.example.InternShip.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<String> handlingRuntimeException(RuntimeException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    ResponseEntity<String> handlingIllegalArgumentException(IllegalArgumentException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    ResponseEntity<String> handlingEntityNotFoundException(EntityNotFoundException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler( value = MethodArgumentNotValidException.class)
    ResponseEntity<String> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        String message = null;
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        if (enumKey != null) {
            try {
                message = ErrorCode.valueOf(enumKey).getMessage();
            } catch (IllegalArgumentException e) {
                message = "Kiểm tra lại request đi";
            }
        }

        return ResponseEntity.badRequest().body(message);
    }
}
