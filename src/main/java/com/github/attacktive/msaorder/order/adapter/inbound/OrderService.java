package com.github.attacktive.msaorder.order.adapter.inbound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.attacktive.msaorder.common.util.ResponseEntityUtils;
import com.github.attacktive.msaorder.order.adapter.NoSuchProductException;
import com.github.attacktive.msaorder.order.adapter.outbound.OrderResponse;
import com.github.attacktive.msaorder.order.adapter.outbound.UpdateProductStockRequest;
import com.github.attacktive.msaorder.order.domain.Order;
import com.github.attacktive.msaorder.order.domain.Product;
import com.github.attacktive.msaorder.order.domain.StockManipulationException;
import com.github.attacktive.msaorder.order.port.inbound.OrderUseCase;
import com.github.attacktive.msaorder.order.port.outbound.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

		var products = Optional.ofNullable(productsResponse)
			.map(ResponseEntity::getBody)
			.orElseGet(() -> {
				log.warn("The body of the response is null which probably means there's a dangling order!");

				return Collections.emptyList();
			});

		return orderPort.findAll()
			.stream()
			.map(order -> products.stream()
				.filter(productCandidate -> productCandidate.id().equals(order.productId()))
				.findAny()
				.map(product -> new OrderResponse(order.id(), product, order.quantity()))
				.orElseGet(() -> OrderResponse.ofNonExistentProduct(order.id(), order.productId(), order.quantity()))
			)
			.toList();
	}

	@Override
	public boolean orderExistsHavingProduct(long targetProductId) {
		return orderPort.findAll()
			.stream()
			.map(Order::productId)
			.anyMatch(productId -> productId.equals(targetProductId));
	}

	@Override
	public OrderResponse getOrder(long id) {
		return orderPort.findById(id)
			.map(order -> {
				var product = retrieveProduct(order.productId());

				return new OrderResponse(id, product, order.quantity());
			})
			.orElseThrow(() -> new NoSuchProductException(id));
	}

	@Override
	public OrderResponse orderProduct(OrderProductRequest orderProductRequest) {
		var updateProductStockRequest = new UpdateProductStockRequest(orderProductRequest);
		var product = updateProductStock(updateProductStockRequest);

		try {
			var order = orderPort.save(orderProductRequest);

			return new OrderResponse(order.id(), product, order.quantity());
		} catch (Exception exception) {
			log.warn(String.format("Order placement (%s) has failed; trying to issue a compensation order.", orderProductRequest), exception);

			var reverseUpdateProductStockRequest = updateProductStockRequest.reverse();
			updateProductStock(reverseUpdateProductStockRequest);

			throw exception;
		}
	}

	@Override
	public OrderResponse changeOrder(long id, ChangeOrderRequest changeOrderRequest) {
		var order = orderPort.findById(id)
			.orElseThrow(() -> new NoSuchProductException(id));

		List<UpdateProductStockRequest> updateProductStockRequests = new ArrayList<>();

		if (order.productId().equals(changeOrderRequest.productId())) {
			var stockChange = order.quantity() - changeOrderRequest.quantity();

			var updateProductStockRequest = new UpdateProductStockRequest(order.productId(), stockChange);
			updateProductStockRequests.add(updateProductStockRequest);
		} else {
			var cancellingUpdateProductStockRequest = new UpdateProductStockRequest(order.productId(), order.quantity());
			updateProductStockRequests.add(cancellingUpdateProductStockRequest);

			var updateProductStockRequest = new UpdateProductStockRequest(changeOrderRequest);
			updateProductStockRequests.add(updateProductStockRequest);
		}

		updateProductStockRequests.forEach(this::updateProductStock);

		var product = retrieveProduct(changeOrderRequest.productId());

		try {
			order = orderPort.save(changeOrderRequest.withId(order.id()));

			return new OrderResponse(order.id(), product, order.quantity());
		} catch (Exception exception) {
			log.warn(String.format("Order change (%s) has failed; trying to issue a compensation order.", changeOrderRequest), exception);

			updateProductStockRequests.stream()
				.map(UpdateProductStockRequest::reverse)
				.forEach(this::updateProductStock);

			throw exception;
		}
	}

	@Override
	public void deleteOrder(long id) {
		var order = orderPort.findById(id)
			.orElseThrow(() -> new NoSuchProductException(id));

		var updateProductStockRequest = new UpdateProductStockRequest(order.productId(), order.quantity());
		updateProductStock(updateProductStockRequest);

		try {
			orderPort.deleteById(id);
		} catch (Exception exception) {
			log.warn(String.format("Order deletion (%s) has failed; trying to issue a compensation order.", id), exception);

			var reverseUpdateProductStockRequest = updateProductStockRequest.reverse();
			updateProductStock(reverseUpdateProductStockRequest);

			throw exception;
		}
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

	private Product updateProductStock(UpdateProductStockRequest updateProductStockRequest) {
		log.info("updateProductStock: {}", updateProductStockRequest);

		try {
			var updateProductStockResponse = webClient.patch()
				.uri(uriBuilder -> uriBuilder
					.path("/")
					.path(String.valueOf(updateProductStockRequest.id()))
					.build()
				)
				.bodyValue(updateProductStockRequest)
				.retrieve()
				.toEntity(Product.class)
				.log()
				.block();

			return ResponseEntityUtils.getBody(updateProductStockResponse);
		} catch (Exception exception) {
			throw new StockManipulationException(exception);
		}
	}
}
