package com.github.attacktive.msaorder.order.api.request;

import org.springframework.lang.Nullable;

public interface OrderRequest {
	@Nullable
	Long id();

	Long productId();
}
