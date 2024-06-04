package com.github.attacktive.msaorder.order.application.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public record ErrorResponse(@JsonIgnore HttpStatus httpStatus, String message) {
	@JsonGetter("httpStatus")
	public int httpStatusValue() {
		return httpStatus.value();
	}
}
