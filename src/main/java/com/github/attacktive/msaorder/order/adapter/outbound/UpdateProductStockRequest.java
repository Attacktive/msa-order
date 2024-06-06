package com.github.attacktive.msaorder.order.adapter.outbound;

import javax.validation.constraints.NotNull;

import com.github.attacktive.msaorder.order.adapter.inbound.OrderRequest;

public record UpdateProductStockRequest(Long id, @NotNull Long stockChange) {
	public UpdateProductStockRequest(OrderRequest orderRequest) {
		this(orderRequest, false);
	}

	public UpdateProductStockRequest(OrderRequest orderRequest, boolean isReverse) {
		this(orderRequest.productId(), ((isReverse ? 1 : -1) * orderRequest.quantity()));
	}
}
