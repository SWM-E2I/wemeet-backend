package com.e2i.wemeet.rest.support;

import java.io.File;

public record MultipartRequest<T>(
    String fileName,
    T data
) {

    public static MultipartRequest<Object> of(String fileName, File file) {
        return new MultipartRequest<>(fileName, file);
    }

    public static <T> MultipartRequest<T> of(String fileName, T file) {
        return new MultipartRequest<>(fileName, file);
    }
}
