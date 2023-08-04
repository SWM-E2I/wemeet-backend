package com.e2i.wemeet.support.config;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.internal.InternalServerException;
import java.lang.reflect.Field;

public abstract class ReflectionUtils {

    private ReflectionUtils() {
        throw new InternalServerException(ErrorCode.UNEXPECTED_INTERNAL);
    }

    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.UNEXPECTED_INTERNAL);
        }
    }

    public static <T, V> void setFieldValue(T target, String fieldName, V value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new InternalServerException(ErrorCode.UNEXPECTED_INTERNAL);
        }
    }
}
