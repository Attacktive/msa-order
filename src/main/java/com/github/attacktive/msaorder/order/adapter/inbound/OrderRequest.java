package com.github.attacktive.msaorder.order.adapter.inbound;

import org.springframework.lang.Nullable;

public interface OrderRequest {
	@Nullable
	Long id();

	Long productId();
}
