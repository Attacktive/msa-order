package com.github.attacktive.msaorder.order.repository.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.attacktive.msaorder.order.api.request.OrderRequest;
import com.github.attacktive.msaorder.order.domain.Order;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`order`")
@NoArgsConstructor
public class OrderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long productId;

	public OrderEntity(OrderRequest orderRequest) {
		productId = orderRequest.productId();
	}

	public Order toOrder() {
		return Order.builder()
			.id(id)
			.productId(productId)
			.build();
	}
}
