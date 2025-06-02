package com.task11.dto;

import java.util.Objects;

public final class RouteKey {

    private final String method;
    private final String path;


    public RouteKey(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RouteKey routeKey = (RouteKey) o;
        return Objects.equals(method, routeKey.method) && Objects.equals(path, routeKey.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }

    @Override
    public String toString() {
        return "RouteKey{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
