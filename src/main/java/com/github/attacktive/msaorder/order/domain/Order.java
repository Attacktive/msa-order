package com.github.attacktive.msaorder.order.domain;

import lombok.Builder;

@Builder
public record Order(Long id, Long productId, Long quantity) { }
