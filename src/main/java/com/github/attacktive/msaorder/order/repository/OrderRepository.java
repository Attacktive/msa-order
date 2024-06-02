package com.github.attacktive.msaorder.order.repository;

import java.util.List;
import java.util.Optional;

import com.github.attacktive.msaorder.order.api.request.OrderRequest;
import com.github.attacktive.msaorder.order.domain.Order;

public interface OrderRepository {
	Optional<Order> findById(long id);

	List<Order> findAll();

	Order save(OrderRequest addOrderRequest);

	void deleteById(long id);
}
