package com.reactivo.tecnologia.domain.utils;

import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public final class ValidationHelper {

    private ValidationHelper() {}

    public static <T> Mono<T> validateRequest(T obj, Predicate<T> condition) {
        return Mono.just(obj)
                   .filter(condition)
                   .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST)));
    }
}