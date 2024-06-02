package com.github.attacktive.msaorder.order.domain;

import lombok.Builder;

@Builder
public record Order(long id, long productId) { }
