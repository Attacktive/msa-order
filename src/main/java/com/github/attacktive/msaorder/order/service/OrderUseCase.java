package com.github.attacktive.msaorder.order.service;

import java.util.List;

import com.github.attacktive.msaorder.order.api.request.OrderProductRequest;
import com.github.attacktive.msaorder.order.api.request.ChangeOrderRequest;
import com.github.attacktive.msaorder.order.api.response.OrderResponse;

public interface OrderUseCase {
	List<OrderResponse> getOrders();

	OrderResponse getOrder(long id);

	OrderResponse orderProduct(OrderProductRequest orderProductRequest);

	OrderResponse changeOrder(long id, ChangeOrderRequest changeOrderRequest);

	void deleteOrder(long id);
}
