package com.github.attacktive.msaorder.order.port.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.attacktive.msaorder.order.adapter.inbound.OrderProductRequest;
import com.github.attacktive.msaorder.order.adapter.outbound.persistence.OrderRepository;
import com.github.attacktive.msaorder.order.domain.Product;
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
	@DisplayName("orderProduct")
	void testOrderProduct() {
		var product = new Product(1L, "product", "product description", 1000L, 10L);
		mockExchangeFunction(product);

		var orderProductRequest = new OrderProductRequest(1L, 1L);
		orderUseCase.orderProduct(orderProductRequest);
	}

	private void mockExchangeFunction(Object desiredResponse) {
		String responseBody;

		try {
			responseBody = objectMapper.writeValueAsString(desiredResponse);
		} catch (JsonProcessingException jsonProcessingException) {
			throw new RuntimeException(jsonProcessingException);
		}

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
