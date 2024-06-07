package com.github.attacktive.msaorder.order.adapter.outbound.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.attacktive.msaorder.order.adapter.inbound.OrderRequest;
import com.github.attacktive.msaorder.order.domain.Order;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`order`")
@NoArgsConstructor
public class OrderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;

	private Long quantity;

	public OrderEntity(OrderRequest orderRequest) {
		id = orderRequest.id();
		productId = orderRequest.productId();
		quantity = orderRequest.quantity();
	}

	public Order toOrder() {
		return Order.builder()
			.id(id)
			.productId(productId)
			.quantity(quantity)
			.build();
	}
}
