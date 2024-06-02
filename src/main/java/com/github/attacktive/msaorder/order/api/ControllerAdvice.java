package com.github.attacktive.msaorder.order.api;

import javax.xml.bind.ValidationException;

import com.github.attacktive.msaorder.order.api.exception.NoSuchProductException;
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
	public void handleHttpMessageConversionException(HttpMessageConversionException httpMessageConversionException) {
		var message = "Couldn't parse the request body: %s".formatted(httpMessageConversionException.getMessage());
		log.error(message, httpMessageConversionException);
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleValidationException(ValidationException validationException) {
		var message = "Failed to validate the request: %s".formatted(validationException.getMessage());
		log.error(message, validationException);
	}

	@ExceptionHandler(NoSuchProductException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleNoSuchProductException(NoSuchProductException noSuchProductException) {
		log.error(noSuchProductException.getMessage(), noSuchProductException);
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleThrowable(Throwable throwable) {
		log.error(throwable.getMessage(), throwable);
	}
}
