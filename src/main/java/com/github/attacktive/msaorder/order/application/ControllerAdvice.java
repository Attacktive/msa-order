package com.github.attacktive.msaorder.order.application;

import javax.xml.bind.ValidationException;

import com.github.attacktive.msaorder.order.application.exception.NoSuchProductException;
import com.github.attacktive.msaorder.order.application.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
	@ExceptionHandler(HttpMessageConversionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleHttpMessageConversionException(HttpMessageConversionException httpMessageConversionException) {
		var message = "Couldn't parse the request body: %s".formatted(httpMessageConversionException.getMessage());
		log.error(message, httpMessageConversionException);

		return new ErrorResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(ValidationException validationException) {
		var message = "Failed to validate the request: %s".formatted(validationException.getMessage());
		log.error(message, validationException);

		return new ErrorResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(NoSuchProductException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNoSuchProductException(NoSuchProductException noSuchProductException) {
		log.error(noSuchProductException.getMessage(), noSuchProductException);

		return new ErrorResponse(HttpStatus.NOT_FOUND, noSuchProductException.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleThrowable(Throwable throwable) {
		log.error(throwable.getMessage(), throwable);

		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
	}
}
