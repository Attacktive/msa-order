package com.github.attacktive.msaorder.order.adapter.inbound;

import javax.validation.constraints.NotNull;

public record OrderProductRequest(@NotNull Long productId) implements OrderRequest {
	@Override
	public Long id() {
		return null;
	}
}
