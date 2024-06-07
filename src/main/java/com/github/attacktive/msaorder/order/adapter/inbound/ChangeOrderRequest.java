package com.github.attacktive.msaorder.order.adapter.inbound;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record ChangeOrderRequest(Long id, @NotNull Long productId, @NotNull @Positive Long quantity) implements OrderRequest {
	public ChangeOrderRequest withId(Long id) {
		return new ChangeOrderRequest(id, productId, quantity);
	}
}
