package com.github.attacktive.msaorder.order.adapter.inbound;

import javax.validation.constraints.NotNull;

public record ChangeOrderRequest(Long id, @NotNull Long productId) implements OrderRequest {
	public ChangeOrderRequest withId(Long id) {
		return new ChangeOrderRequest(id, productId);
	}
}
