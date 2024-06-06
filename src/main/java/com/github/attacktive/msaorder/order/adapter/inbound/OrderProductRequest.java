package com.github.attacktive.msaorder.order.adapter.inbound;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.lang.Nullable;

public record OrderProductRequest(@NotNull Long productId, @NotNull @PositiveOrZero Long quantity) implements OrderRequest {
	@Override
	@Nullable
	public Long id() {
		return null;
	}
}
