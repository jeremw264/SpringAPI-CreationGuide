package com.example.demo.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalHandlerControllerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResourceExceptionDTO> unknowError(HttpServletRequest req, Exception exception) {

        ResourceExceptionDTO res = ResourceExceptionDTO.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString().substring(4))
                .errorMessage(exception.getMessage())
                .requestURL(req.getRequestURL().toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        exception.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<ResourceExceptionDTO> resourceError(HttpServletRequest req, ResourceException exception) {

        ResourceExceptionDTO res = ResourceExceptionDTO.builder()
                .errorCode(exception.getErrorCode() != null ? exception.getErrorCode() : "Undefined")
                .errorMessage(exception.getMessage())
                .requestURL(req.getRequestURL().toString())
                .status(exception.getStatus())
                .build();

        return ResponseEntity.status(exception.getStatus()).body(res);
    }

    /**
     * Handler for handling method argument validation errors.
     *
     * @param exception The MethodArgumentNotValidException object.
     * @param headers   The HTTP headers.
     * @param status    The HTTP status code.
     * @param request   The web request.
     * @return ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        StringBuilder errorMessage = new StringBuilder();

        exception.getBindingResult().getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()));

        ResourceExceptionDTO res = ResourceExceptionDTO.builder()
                .errorCode("FormValidationError")
                .errorMessage(errorMessage.toString())
                .requestURL(((ServletWebRequest) request).getRequest().getRequestURI())
                .status((HttpStatus) status)
                .build();
        ;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
