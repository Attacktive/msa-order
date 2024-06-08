package com.github.attacktive.msaorder.order.adapter.outbound;

import com.github.attacktive.msaorder.order.domain.Product;

public record OrderResponse(Long id, Product product, Long quantity) {
	public static OrderResponse ofNonExistentProduct(Long id, Long productId, Long quantity) {
		return new OrderResponse(id, Product.nonExistent(productId), quantity);
	}
}
