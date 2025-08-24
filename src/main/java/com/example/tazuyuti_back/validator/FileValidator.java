package com.example.tazuyuti_back.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import com.example.tazuyuti_back.helpers.Anottation.FileConstraint;

public class FileValidator implements ConstraintValidator<FileConstraint, MultipartFile> {

    // private long maxSize;
    // private String allowedExtensions;

    @Override
    public void initialize(FileConstraint constraintAnnotation) {
        // this.maxSize = constraintAnnotation.maxSize();
        // this.allowedExtensions = constraintAnnotation.allowedExtensions();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // if (file == null || file.isEmpty()) {
        //     return false;
        // }

        // if (file.getSize() > maxSize) {
        //     return false;
        // }

        // String fileName = file.getOriginalFilename();
        // return fileName != null && fileName.matches(allowedExtensions);
        return true;
    }
}