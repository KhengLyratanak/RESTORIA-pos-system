package com.nak.demo.exception;

import com.nak.demo.Model.BaseResponseModel;
import com.nak.demo.Model.BaseResponseModelWithData;
import com.nak.demo.dto.base.Response;
import com.nak.demo.exception.model.CustomAuthenticationException;
import com.nak.demo.exception.model.DuplicateException;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.exception.model.UnprocessableEntityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> handleResourceNotFoundException(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.badRequest("fail",ex.getMessage()));
    }
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Response> handleDuplicateResourceException(DuplicateException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.badRequest("fail",ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGenericException(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.badRequest("fail","unexpected error occured:" +ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap();
        ex.getBindingResult().getAllErrors()
                .forEach((error) ->{
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();

            errors.put(fieldName,message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.badRequest("fail","validation failed",errors));
    }
    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Response> handleUnprocessableEntity(UnprocessableEntityException ex){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Response.badRequest("fail",ex.getMessage()));
    }
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<Response> handleAuthenticationException(CustomAuthenticationException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.badRequest("fail",ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(Response.error("400", "fail", "invalid value for '" + ex.getName() + "', expected yyyy-MM-dd"));
    }
}


