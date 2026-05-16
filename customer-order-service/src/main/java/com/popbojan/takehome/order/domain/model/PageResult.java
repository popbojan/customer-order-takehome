package com.popbojan.takehome.order.domain.model;

import java.util.List;

public record PageResult<T>(List<T> items, long total, int limit, int offset) {
}
