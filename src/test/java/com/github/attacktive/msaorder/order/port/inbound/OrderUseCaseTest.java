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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
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

	@Autowired
	private WebClient webClient;

	@Mock
	private ExchangeFunction exchangeFunctionMock;

	@BeforeEach
	void mock() throws JsonProcessingException {
		var product = new Product(1L, "product", "product description", 1000L, 10L);
		var responseBody =objectMapper.writeValueAsString(product);

		// fixme: Mocking below does not work. 🤷
		exchangeFunctionMock = clientRequest -> Mono.just(
			ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(responseBody)
				.build()
		);

		webClient.mutate()
			.exchangeFunction(exchangeFunctionMock)
			.build();
	}

	@BeforeEach
	void truncateTable() {
		orderRepository.deleteAll();
	}

	@Test
	@DisplayName("orderProduct")
	void testOrderProduct() {
		var orderProductRequest = new OrderProductRequest(1L, 1L);
		orderUseCase.orderProduct(orderProductRequest);
	}
}
