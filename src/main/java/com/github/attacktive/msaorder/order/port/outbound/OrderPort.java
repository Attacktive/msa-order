package com.github.attacktive.msaorder.order.port.outbound;

import java.util.List;
import java.util.Optional;

import com.github.attacktive.msaorder.order.adapter.inbound.OrderRequest;
import com.github.attacktive.msaorder.order.domain.Order;

public interface OrderPort {
	Optional<Order> findById(long id);

	List<Order> findAll();

	Order save(OrderRequest addOrderRequest);

	void deleteById(long id);
}
