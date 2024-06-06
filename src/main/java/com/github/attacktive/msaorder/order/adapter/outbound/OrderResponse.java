package com.github.attacktive.msaorder.order.adapter.outbound;

import com.github.attacktive.msaorder.order.domain.Product;

public record OrderResponse(Long id, Product product) { }
