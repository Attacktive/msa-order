package com.github.attacktive.msaorder.order.port.inbound;

import java.util.List;

import com.github.attacktive.msaorder.order.application.request.ChangeOrderRequest;
import com.github.attacktive.msaorder.order.application.request.OrderProductRequest;
import com.github.attacktive.msaorder.order.application.response.OrderResponse;

public interface OrderUseCase {
	List<OrderResponse> getOrders();

	OrderResponse getOrder(long id);

	OrderResponse orderProduct(OrderProductRequest orderProductRequest);

	OrderResponse changeOrder(long id, ChangeOrderRequest changeOrderRequest);

	void deleteOrder(long id);
}
