package com.github.attacktive.msaorder.order.api.exception;

public class NoSuchProductException extends RuntimeException {
	public NoSuchProductException(long id) {
		super("The product with id %d does not exist! It might have been deleted.".formatted(id));
	}
}
