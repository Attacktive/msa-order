package com.github.attacktive.msaorder.order.adapter.outbound;

import javax.validation.constraints.NotNull;

import com.github.attacktive.msaorder.order.adapter.inbound.OrderRequest;

public record UpdateProductStockRequest(Long id, @NotNull Long stockChange) {
	public UpdateProductStockRequest(OrderRequest orderRequest) {
		this(orderRequest.productId(), -orderRequest.quantity());
	}

	public UpdateProductStockRequest reverse() {
		return new UpdateProductStockRequest(id, -stockChange);
	}
}
