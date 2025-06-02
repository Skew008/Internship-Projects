package com.restaurantbackend.dto;

import org.json.JSONObject;

import java.util.List;

public record Pageable(int offset, Sort sort, boolean paged, int pageSize, int pageNumber, boolean unpaged) {

    public static JSONObject toJson(Pageable pageable) {
        return new JSONObject()
                .put("offset", pageable.offset())
                .put("sort", Sort.toJson(pageable.sort()))
                .put("paged", pageable.paged())
                .put("pageSize", pageable.pageSize())
                .put("pageNumber", pageable.pageNumber())
                .put("unpaged", pageable.unpaged());
    }
}
