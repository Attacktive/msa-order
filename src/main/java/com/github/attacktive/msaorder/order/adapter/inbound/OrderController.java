package com.github.attacktive.msaorder.order.adapter.inbound;

import java.util.List;
import javax.validation.Valid;

import com.github.attacktive.msaorder.order.adapter.outbound.OrderResponse;
import com.github.attacktive.msaorder.order.port.inbound.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderUseCase orderUseCase;

	@GetMapping
	public ResponseEntity<List<OrderResponse>> getOrders() {
		ResponseEntity<List<OrderResponse>> responseEntity;
		var products = orderUseCase.getOrders();
		if (products.isEmpty()) {
			responseEntity = new ResponseEntity<>(products, HttpStatus.NO_CONTENT);
		} else {
			responseEntity = ResponseEntity.ok(products);
		}

		return responseEntity;
	}

	@RequestMapping(method = RequestMethod.HEAD, path = "products/{product-id}")
	public ResponseEntity<Void> hasOrdersContainingProduct(@PathVariable("product-id") long productId) {
		ResponseEntity<Void> responseEntity;

		var orderExists = orderUseCase.orderExistsHavingProduct(productId);
		if (orderExists) {
			responseEntity = ResponseEntity.ok().build();
		} else {
			responseEntity = ResponseEntity.notFound().build();
		}

		return responseEntity;
	}

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public OrderResponse getOrder(@PathVariable long id) {
		return orderUseCase.getOrder(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse orderProduct(@RequestBody @Valid OrderProductRequest orderProductRequest) {
		return orderUseCase.orderProduct(orderProductRequest);
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public OrderResponse changeOrder(@PathVariable long id, @RequestBody @Valid ChangeOrderRequest changeOrderRequest) {
		return orderUseCase.changeOrder(id, changeOrderRequest);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOrder(@PathVariable long id) {
		orderUseCase.deleteOrder(id);
	}
}
