package com.invitrocore.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/* Recurso no encontrado */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {

		ApiError error = new ApiError(
				ex.getMessage(),
				HttpStatus.NOT_FOUND.value());

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	/* Bad Request */
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {

		ApiError error = new ApiError(
				ex.getMessage(),
				HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {

		ApiError error = new ApiError(
				"Correo o contraseña incorrectos",
				HttpStatus.UNAUTHORIZED.value());

		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handlerAccessDenied(AccessDeniedException ex) {
		ApiError error = new ApiError(
				"No tienes permisos para esta acción",
				HttpStatus.FORBIDDEN.value());
		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}

	/* Validaciones */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.findFirst()
				.orElse("Error de validación");

		ApiError error = new ApiError(
				message,
				HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/* Error general */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneral(Exception ex) {

		log.error("Error no controlado: ", ex);
		ApiError error = new ApiError(
				"Error interno del servidor",
				HttpStatus.INTERNAL_SERVER_ERROR.value());

		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
