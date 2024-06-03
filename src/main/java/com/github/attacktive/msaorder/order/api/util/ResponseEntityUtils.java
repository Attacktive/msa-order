package com.github.attacktive.msaorder.order.api.util;

import java.util.Objects;
import java.util.Optional;

import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@UtilityClass
public class ResponseEntityUtils {
	@NonNull
	public static <T> T getBody(@Nullable ResponseEntity<T> responseEntity) {
		var body = Optional.ofNullable(responseEntity)
			.map(ResponseEntity::getBody)
			.orElseThrow(() -> new NullPointerException("The response retrieved by WebClient is null!"));

		return Objects.requireNonNull(body, "The response body retrieved by WebClient is null!");
	}
}
