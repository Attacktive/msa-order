package com.github.attacktive.msaorder.order.port.inbound;

import java.util.List;

import com.github.attacktive.msaorder.order.adapter.inbound.ChangeOrderRequest;
import com.github.attacktive.msaorder.order.adapter.inbound.OrderProductRequest;
import com.github.attacktive.msaorder.order.adapter.outbound.OrderResponse;

public interface OrderUseCase {
	List<OrderResponse> getOrders();

	OrderResponse getOrder(long id);

	OrderResponse orderProduct(OrderProductRequest orderProductRequest);

	OrderResponse changeOrder(long id, ChangeOrderRequest changeOrderRequest);

	void deleteOrder(long id);
}
