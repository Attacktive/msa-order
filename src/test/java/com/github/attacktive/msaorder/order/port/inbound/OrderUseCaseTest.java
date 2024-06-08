package com.github.attacktive.msaorder.order.port.inbound;

import java.util.Collections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.attacktive.msaorder.order.adapter.inbound.ChangeOrderRequest;
import com.github.attacktive.msaorder.order.adapter.inbound.OrderProductRequest;
import com.github.attacktive.msaorder.order.adapter.outbound.persistence.OrderRepository;
import com.github.attacktive.msaorder.order.domain.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderUseCase")
@Transactional
class OrderUseCaseTest {
	@Autowired
	private OrderUseCase orderUseCase;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ExchangeFunction exchangeFunction;

	@BeforeEach
	void truncateTable() {
		orderRepository.deleteAll();
	}

	@Test
	@DisplayName("getOrders")
	void testGetOrders() throws JsonProcessingException {
		var mockProduct = new Product(1L, "product", "product description", 1000L, 10L);
		mockExchangeFunction(mockProduct);

		var orderProductRequest = new OrderProductRequest(1L, 1L);
		orderUseCase.orderProduct(orderProductRequest);

		var mockProducts = Collections.singletonList(mockProduct);
		mockExchangeFunction(mockProducts);

		var orders = orderUseCase.getOrders();
		Assertions.assertEquals(1, orders.size());

		var order = orders.get(0);
		Assertions.assertEquals(1, order.quantity());

		var orderedProduct = order.product();
		Assertions.assertEquals(1, orderedProduct.id());
		Assertions.assertEquals("product", orderedProduct.name());
		Assertions.assertEquals("product description", orderedProduct.description());
		Assertions.assertEquals(1000, orderedProduct.price());
		Assertions.assertEquals(10, orderedProduct.quantity());
	}

	@Test
	@DisplayName("orderExistsHavingProduct")
	void testOrderExistsHavingProduct() throws JsonProcessingException {
		var mockProduct = new Product(1L, "product", "product description", 1000L, 10L);
		mockExchangeFunction(mockProduct);

		var orderProductRequest = new OrderProductRequest(1L, 1L);
		orderUseCase.orderProduct(orderProductRequest);

		var orderExists = orderUseCase.orderExistsHavingProduct(1);

		Assertions.assertTrue(orderExists);
	}

	@Test
	@DisplayName("getOrder")
	void testGetOrder() throws JsonProcessingException {
		var mockProduct = new Product(1L, "product", "product description", 1000L, 10L);
		mockExchangeFunction(mockProduct);

		var orderProductRequest = new OrderProductRequest(1L, 1L);
		var orderId = orderUseCase.orderProduct(orderProductRequest).id();

		var order = orderUseCase.getOrder(orderId);
		Assertions.assertEquals(1, order.quantity());

		var orderedProduct = order.product();
		Assertions.assertEquals(1, orderedProduct.id());
		Assertions.assertEquals("product", orderedProduct.name());
		Assertions.assertEquals("product description", orderedProduct.description());
		Assertions.assertEquals(1000, orderedProduct.price());
		Assertions.assertEquals(10, orderedProduct.quantity());
	}

	@Test
	@DisplayName("changeOrder: the quantity")
	void testChangeOrderQuantity() throws JsonProcessingException {
		var mockProduct = new Product(1L, "product", "product description", 1000L, 10L);
		mockExchangeFunction(mockProduct);

		var orderProductRequest = new OrderProductRequest(1L, 1L);
		var order = orderUseCase.orderProduct(orderProductRequest);
		var orderId = order.id();
		var product = order.product();

		var changeOrderRequest = new ChangeOrderRequest(orderId, 1L, 2L);
		var changedOrder = orderUseCase.changeOrder(orderId, changeOrderRequest);
		Assertions.assertEquals(orderId, changedOrder.id());
		Assertions.assertEquals(2, changedOrder.quantity());

		var changedProduct = changedOrder.product();
		Assertions.assertEquals(product.id(), changedProduct.id());

		/*
		 * The following test fails ☠ because the response is mocked.
		 * I wouldn't change the mock response and test since that's pointless.
		 */
		//Assertions.assertEquals(product.quantity() - 1, changedProduct.quantity());

		Assertions.assertTrue(orderRepository.findById(orderId).isPresent());
		Assertions.assertEquals(1, orderRepository.count());
	}

	@Test
	@DisplayName("deleteOrder")
	void testDeleteOrder() throws JsonProcessingException {
		var mockProduct = new Product(1L, "product", "product description", 1000L, 10L);
		mockExchangeFunction(mockProduct);

		var orderProductRequest = new OrderProductRequest(1L, 1L);
		var orderId = orderUseCase.orderProduct(orderProductRequest).id();

		orderUseCase.deleteOrder(orderId);
		Assertions.assertTrue(orderRepository.findById(orderId).isEmpty());
		Assertions.assertEquals(0, orderRepository.count());
	}

	@Test
	@DisplayName("orderProduct")
	void testOrderProduct() throws JsonProcessingException {
		var mockProduct = new Product(1L, "product", "product description", 1000L, 10L);
		mockExchangeFunction(mockProduct);

		var orderProductRequest = new OrderProductRequest(1L, 1L);
		var orderResponse = orderUseCase.orderProduct(orderProductRequest);
		var orderedProduct = orderResponse.product();

		Assertions.assertEquals(1, orderedProduct.id());
		Assertions.assertEquals("product", orderedProduct.name());
		Assertions.assertEquals("product description", orderedProduct.description());
		Assertions.assertEquals(1000, orderedProduct.price());
		Assertions.assertEquals(10, orderedProduct.quantity());

		Assertions.assertEquals(1, orderRepository.count());
	}

	private void mockExchangeFunction(Object desiredResponse) throws JsonProcessingException {
		var responseBody = objectMapper.writeValueAsString(desiredResponse);

		Mockito.when(exchangeFunction.exchange(Mockito.any(ClientRequest.class)))
			.thenReturn(
				Mono.just(
					ClientResponse.create(HttpStatus.OK)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.body(responseBody)
						.build()
				)
			);
	}
}
