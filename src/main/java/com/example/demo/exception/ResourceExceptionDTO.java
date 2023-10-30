package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceExceptionDTO {
	public String errorCode;
	public String errorMessage;
	public String requestURL;
	public HttpStatus status;
}
