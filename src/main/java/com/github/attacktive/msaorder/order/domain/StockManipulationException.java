package com.github.attacktive.msaorder.order.domain;

public class StockManipulationException extends RuntimeException {
	public StockManipulationException(Throwable cause) {
		super("Failed to handle the stock.", cause);
	}
}
