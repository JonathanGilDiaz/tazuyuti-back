/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 05 Ene 2025
 * @date 05/01/2025
 */
package com.example.residencia_back.configuration;

import com.example.residencia_back.helpers.SystemText;
import com.example.residencia_back.models.utilities.Response;
import com.example.residencia_back.models.utilities.Error;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import java.nio.file.AccessDeniedException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;
        String message = "";
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (exception instanceof BadCredentialsException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.DATOS_ACCESOS_INCORRECTOS);
            message = SystemText.Exception.DATOS_ACCESOS_INCORRECTOS;
        }
        
        if (exception instanceof AccountStatusException) {
            httpStatus = HttpStatus.FORBIDDEN;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.CUENTA_BLOQUEADA);
            message = SystemText.Exception.CUENTA_BLOQUEADA;
        }
        
        if (exception instanceof AccessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.PERMISO_DENEGADO_RECURSOS);
            message = SystemText.Exception.PERMISO_DENEGADO_RECURSOS;
        }
        
        if (exception instanceof SignatureException) {
            httpStatus = HttpStatus.FORBIDDEN;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.FIRMA_JWT_INVALIDA);
            message = SystemText.Exception.FIRMA_JWT_INVALIDA;
        }
        
        if (exception instanceof ExpiredJwtException) {
            httpStatus = HttpStatus.FORBIDDEN;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.TOKEN_EXPIRADO);
            message = SystemText.Exception.TOKEN_EXPIRADO;
        }
        
        if (exception instanceof MissingServletRequestPartException) {
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
            errorDetail.setProperty("description", SystemText.General.FALTA_INFORMACION);
            message = SystemText.General.FALTA_INFORMACION;
        }
        
        if (exception instanceof NoHandlerFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.URL_NO_EXISTE);
            message = SystemText.Exception.URL_NO_EXISTE;
        }
        
        if (exception instanceof MaxUploadSizeExceededException) {
            httpStatus = HttpStatus.PAYLOAD_TOO_LARGE;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.ERROR_LIMITE_MAXIMO_REQUEST_UPLOAD);
            message = SystemText.Exception.ERROR_LIMITE_MAXIMO_REQUEST_UPLOAD;
        }
        
        if (exception instanceof MethodArgumentTypeMismatchException || exception instanceof NullPointerException ||
            exception instanceof UnexpectedTypeException || exception instanceof JpaObjectRetrievalFailureException ||
            exception instanceof DateTimeParseException || exception instanceof JpaSystemException || 
            exception instanceof InvalidDataAccessApiUsageException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.INFORMACION_INCORRECTA_SOLICITUD);
            message = SystemText.Exception.INFORMACION_INCORRECTA_SOLICITUD;
        }

        if (errorDetail == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
            errorDetail.setProperty("description", SystemText.Exception.ERROR_INTERNO_SERVIDOR);
            message = SystemText.Exception.ERROR_INTERNO_SERVIDOR;
        }

		System.err.println("-----------------");
		logger.error(exception.getClass().getName());
		logger.error(errorDetail.getDetail());
		System.err.println("-----------------");

        errorDetail.setDetail(SystemText.General.ERROR_PROCESO);

        return ResponseEntity.status(httpStatus).body(new Response(false, message, errorDetail));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleConversionErrors(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, SystemText.General.FALTA_INFORMACION, new Error(List.of(SystemText.Exception.ERROR_JSON_INVALIDO))));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleConversionErrors(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, SystemText.General.FALTA_INFORMACION, new Error(List.of(SystemText.Exception.CONSTRAINT_VIOLATION))));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> notValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            String fieldName = err.getField();
            // String errorMessage = err.getDefaultMessage();
            // errors.add(String.format("Error en el campo '%s': %s", fieldName, errorMessage));
            errors.add(SystemText.Exception.ERROR_PARAMETRO + fieldName);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, SystemText.General.FALTA_INFORMACION, new Error(errors)));
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
    }
    
}
