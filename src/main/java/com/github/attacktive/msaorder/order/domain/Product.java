package com.github.attacktive.msaorder.order.domain;

public record Product(Long id, String name, String description, Long price, Long quantity) {
	public static Product nonExistent(Long id) {
		return new Product(id, "(Removed product)", null, null, null);
	}
}
