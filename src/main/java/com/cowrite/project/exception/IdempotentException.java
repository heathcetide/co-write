package com.cowrite.project.exception;

/**
 * 幂等性异常
 *
 * @author heathcetide
 * @since 1.0.0
 */
public class IdempotentException extends RuntimeException {
    public IdempotentException(String message) {
        super(message);
    }
}