package com.github.attacktive.msaorder.order.application.request;

import org.springframework.lang.Nullable;

public interface OrderRequest {
	@Nullable
	Long id();

	Long productId();
}
