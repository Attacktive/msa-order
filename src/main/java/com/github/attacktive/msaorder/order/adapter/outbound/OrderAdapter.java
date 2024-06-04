package com.github.attacktive.msaorder.order.adapter.outbound;

import java.util.List;
import java.util.Optional;

import com.github.attacktive.msaorder.order.adapter.outbound.persistence.OrderEntity;
import com.github.attacktive.msaorder.order.adapter.outbound.persistence.OrderRepository;
import com.github.attacktive.msaorder.order.application.request.OrderRequest;
import com.github.attacktive.msaorder.order.domain.Order;
import com.github.attacktive.msaorder.order.port.outbound.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderAdapter implements OrderPort {
	private final OrderRepository orderRepository;

	@Override
	@Transactional(readOnly = true)
	public Optional<Order> findById(long id) {
		return orderRepository.findById(id)
			.map(OrderEntity::toOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Order> findAll() {
		return orderRepository.findAll()
			.stream()
			.map(OrderEntity::toOrder)
			.toList();
	}

	@Override
	@Transactional
	public Order save(OrderRequest orderRequest) {
		return orderRepository.save(new OrderEntity(orderRequest))
			.toOrder();
	}

	@Override
	@Transactional
	public void deleteById(long id) {
		orderRepository.deleteById(id);
	}
}
