package com.github.attacktive.msaorder.order.adapter.inbound;

import java.util.List;
import java.util.stream.Collectors;

import com.github.attacktive.msaorder.order.application.exception.NoSuchProductException;
import com.github.attacktive.msaorder.order.application.request.ChangeOrderRequest;
import com.github.attacktive.msaorder.order.application.request.OrderProductRequest;
import com.github.attacktive.msaorder.order.application.request.OrderRequest;
import com.github.attacktive.msaorder.order.application.response.OrderResponse;
import com.github.attacktive.msaorder.order.application.response.Product;
import com.github.attacktive.msaorder.order.application.util.ResponseEntityUtils;
import com.github.attacktive.msaorder.order.domain.Order;
import com.github.attacktive.msaorder.order.port.inbound.OrderUseCase;
import com.github.attacktive.msaorder.order.port.outbound.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderUseCase {
	private final OrderRepository orderRepository;
	private final WebClient webClient;

	@Override
	public List<OrderResponse> getOrders() {
		var orders = orderRepository.findAll();

		var productIds = orders.stream()
			.map(Order::productId)
			.collect(Collectors.toSet());

		var typeReference = new ParameterizedTypeReference<List<Product>>() { };

		var productsResponse = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("size", Integer.MAX_VALUE)
				.queryParam("product-id", productIds)
				.build()
			)
			.retrieve()
			.toEntity(typeReference)
			.log()
			.block();

		var products = ResponseEntityUtils.getBody(productsResponse);

		return orderRepository.findAll()
			.stream()
			.map(order -> {
				var product = products.stream()
					.filter(productCandidate -> productCandidate.id() == order.productId())
					.findAny()
					.orElseThrow(() -> new NoSuchProductException(order.productId()));

				return new OrderResponse(order.id(), product);
			})
			.toList();
	}

	@Override
	public OrderResponse getOrder(long id) {
		return orderRepository.findById(id)
			.map(order -> {
				var productResponse = webClient.get()
					.uri(uriBuilder -> uriBuilder
						.path("/")
						.path(String.valueOf(id))
						.build()
					)
					.retrieve()
					.toEntity(Product.class)
					.log()
					.block();

				var product = ResponseEntityUtils.getBody(productResponse);

				return new OrderResponse(id, product);
			})
			.orElseThrow(() -> new NoSuchProductException(id));
	}

	@Override
	public OrderResponse orderProduct(OrderProductRequest orderProductRequest) {
		var product = retrieveProduct(orderProductRequest);
		var order = orderRepository.save(orderProductRequest);
		return new OrderResponse(order.id(), product);
	}

	@Override
	public OrderResponse changeOrder(long id, ChangeOrderRequest changeOrderRequest) {
		var productId = orderRepository.findById(id)
			.map(Order::id)
			.orElseThrow(() -> new NoSuchProductException(id));

		var product = retrieveProduct(changeOrderRequest);
		var order = orderRepository.save(changeOrderRequest.withId(productId));

		return new OrderResponse(order.id(), product);
	}

	@Override
	public void deleteOrder(long id) {
		orderRepository.findById(id)
			.orElseThrow(() -> new NoSuchProductException(id));

		orderRepository.deleteById(id);
	}

	private Product retrieveProduct(OrderRequest orderRequest) {
		var productResponse = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/")
				.path(String.valueOf(orderRequest.productId()))
				.build()
			)
			.retrieve()
			.toEntity(Product.class)
			.log()
			.block();

		return ResponseEntityUtils.getBody(productResponse);
	}
}
