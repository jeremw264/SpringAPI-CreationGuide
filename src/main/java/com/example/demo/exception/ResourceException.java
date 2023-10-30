package com.example.demo.exception;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Exception class that represents resource-related errors.
 */
@Getter
@Setter
public class ResourceException extends Exception {

	private final String errorCode;
	private final HttpStatus status;

	/**
	 * Constructs a new ResourceException with the specified error code, message, and HTTP status.
	 *
	 * @param errorCode The error code associated with the exception.
	 * @param message   The detailed error message.
	 * @param status    The HTTP status associated with the exception.
	 */
	public ResourceException(String errorCode, String message, HttpStatus status) {
		super(message);
		this.errorCode = errorCode;
		this.status = status;
	}
}
