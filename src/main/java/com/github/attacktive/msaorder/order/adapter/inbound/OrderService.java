package com.github.attacktive.msaorder.order.adapter.inbound;

import java.util.List;
import java.util.stream.Collectors;

import com.github.attacktive.msaorder.order.adapter.NoSuchProductException;
import com.github.attacktive.msaorder.order.adapter.outbound.OrderResponse;
import com.github.attacktive.msaorder.order.adapter.outbound.Product;
import com.github.attacktive.msaorder.common.util.ResponseEntityUtils;
import com.github.attacktive.msaorder.order.domain.Order;
import com.github.attacktive.msaorder.order.port.inbound.OrderUseCase;
import com.github.attacktive.msaorder.order.port.outbound.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderUseCase {
	private final OrderPort orderPort;
	private final WebClient webClient;

	@Override
	public List<OrderResponse> getOrders() {
		var orders = orderPort.findAll();

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

		return orderPort.findAll()
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
		return orderPort.findById(id)
			.map(order -> {
				var product = retrieveProduct(order.productId());

				return new OrderResponse(id, product);
			})
			.orElseThrow(() -> new NoSuchProductException(id));
	}

	@Override
	public OrderResponse orderProduct(OrderProductRequest orderProductRequest) {
		var product = retrieveProduct(orderProductRequest.productId());
		var order = orderPort.save(orderProductRequest);
		return new OrderResponse(order.id(), product);
	}

	@Override
	public OrderResponse changeOrder(long id, ChangeOrderRequest changeOrderRequest) {
		var productId = orderPort.findById(id)
			.map(Order::id)
			.orElseThrow(() -> new NoSuchProductException(id));

		var product = retrieveProduct(changeOrderRequest.productId());
		var order = orderPort.save(changeOrderRequest.withId(productId));

		return new OrderResponse(order.id(), product);
	}

	@Override
	public void deleteOrder(long id) {
		orderPort.findById(id)
			.orElseThrow(() -> new NoSuchProductException(id));

		orderPort.deleteById(id);
	}

	private Product retrieveProduct(Long productId) {
		var productResponse = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/")
				.path(String.valueOf(productId))
				.build()
			)
			.retrieve()
			.onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.just(new NoSuchProductException(productId)))
			.toEntity(Product.class)
			.log()
			.block();

		return ResponseEntityUtils.getBody(productResponse);
	}
}
