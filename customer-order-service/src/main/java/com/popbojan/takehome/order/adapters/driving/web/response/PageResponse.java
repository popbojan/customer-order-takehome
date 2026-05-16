package com.popbojan.takehome.order.adapters.driving.web.response;

import java.util.List;

public record PageResponse<T>(List<T> items, long total, int limit, int offset) {
}
