package com.github.attacktive.msaorder.order.application.request;

import javax.validation.constraints.NotNull;

public record OrderProductRequest(@NotNull Long productId) implements OrderRequest {
	@Override
	public Long id() {
		return null;
	}
}
