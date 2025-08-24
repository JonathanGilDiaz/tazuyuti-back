package com.example.tazuyuti_back.helpers.Anottation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.tazuyuti_back.validator.FileValidator;

@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileConstraint {
    
    String message() default "Archivo no válido";
    
    long maxSize() default 5242880; // 5MB for default
    
    String allowedExtensions() default ".*\\.(jpg|jpeg|png|pdf)"; // Formats allowed by default
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};

}